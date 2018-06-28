package pl.karol202.latemeter.schedule

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.utils.Time

class DaySchedule(
		context: Context,
		private val teachers: Teachers,
		private val dayOfWeek: DayOfWeek
) {
	companion object
	{
		private const val KEY_LENGTH = "schedule_%s_length"
		private const val KEY_SCHEDULE_HOUR_N = "schedule_%s_hour_%d"

		private const val MAX_LENGTH_PER_DAY = 20
	}

	private val scheduleHours = mutableListOf<ScheduleHour>()

	val size: Int
		get() = scheduleHours.size

	init
	{
		loadSchedule(context, teachers)
	}

	operator fun get(index: Int) = scheduleHours[index]

	operator fun set(index: Int, scheduleHour: ScheduleHour)
	{
		scheduleHours[index] = scheduleHour
	}

	fun getIndexOf(scheduleHour: ScheduleHour) = scheduleHours.indexOf(scheduleHour)

	fun addScheduleHour(start: Time, end: Time): Int?
	{
		if(size >= MAX_LENGTH_PER_DAY) return null
		scheduleHours.add(ScheduleHour(this, teachers, start, end, null, null))
		checkSchedule()
		return size - 1
	}

	fun removeScheduleHour(scheduleHour: ScheduleHour)
	{
		scheduleHours.remove(scheduleHour)
		checkSchedule()
	}

	interface OrderUpdate
	class MoveUpdate(val from: Int, val to: Int) : OrderUpdate
	class FullUpdate : OrderUpdate

	fun sortSchedule(): OrderUpdate?
	{
		if(scheduleHours == scheduleHours.sortedBy { it.start }) return null

		val operation = findSingularSwap()
		scheduleHours.sortBy { it.start }
		return operation ?: FullUpdate()
	}

	private fun findSingularSwap(): OrderUpdate?
	{
		var last: Time? = null
		for(i in 0 until size)
		{
			val current = scheduleHours[i].start
			if(last == null || current >= last) last = current
			else
			{
				val hoursWithoutCurrent = scheduleHours.filter { it.start != current }
				if(hoursWithoutCurrent != hoursWithoutCurrent.sortedBy { it.start }) return FullUpdate()
				else
				{
					for(j in 0 until i)
						if(scheduleHours[j].start > current) return MoveUpdate(i, j)
				}
			}
		}
		return null
	}

	fun checkSchedule()
	{
		scheduleHours.forEach { it.checkError() }
	}

	fun getCurrentScheduleHour(time: Time): ScheduleHour?
	{
		for(scheduleHour in scheduleHours)
		{
			if(scheduleHour.error != null) continue
			if(time in scheduleHour.start..scheduleHour.end) return scheduleHour
		}
		return null
	}

	fun findSameScheduleHour(scheduleHour: ScheduleHour) = scheduleHours.find { it == scheduleHour }

	private fun loadSchedule(context: Context, teachers: Teachers)
	{
		scheduleHours.clear()

		val gson = Gson()
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		val length = prefs.getInt(String.format(KEY_LENGTH, dayOfWeek.name), 0)
		for(i in 0 until length)
		{
			val prefName = String.format(KEY_SCHEDULE_HOUR_N, dayOfWeek.name, i)
			val scheduleHour = gson.fromJson(prefs.getString(prefName, ""), ScheduleHour::class.java)
			scheduleHour.daySchedule = this
			scheduleHour.teachers = teachers
			scheduleHours.add(scheduleHour)
		}
		checkSchedule()
	}

	fun saveSchedule(context: Context)
	{
		val gson = Gson()
		val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
		editor.putInt(String.format(KEY_LENGTH, dayOfWeek.name), scheduleHours.size)
		for(i in 0 until scheduleHours.size)
		{
			val prefName = String.format(KEY_SCHEDULE_HOUR_N, dayOfWeek.name, i)
			editor.putString(prefName, gson.toJson(scheduleHours[i]))
		}
		editor.apply()
	}
}