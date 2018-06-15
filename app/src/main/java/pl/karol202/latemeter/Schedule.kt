package pl.karol202.latemeter

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson

class Schedule private constructor(context: Context)
{
	enum class Error(val message: Int)
	{
		INVALID(R.string.error_schedule_hour_invalid),
		NEGATIVE_TIMESPAN(R.string.error_schedule_hour_negative_timespan),
		OVERLAP(R.string.error_schedule_hour_overlap)
	}

	data class Time(val hour: Int, val minute: Int): Comparable<Time>
	{
		val valid: Boolean
			get() = hour in 0..23 && minute in 0..59

		override fun compareTo(other: Time) = when
		{
			hour > other.hour -> 1
			hour < other.hour -> -1
			else -> when {
				minute > other.minute -> 1
				minute < other.minute -> -1
				else -> 0
			}
		}
	}

	class ScheduleHour(@Transient var schedule: Schedule, _start: Time, _end: Time, @Transient var error: Error? = null)
	{
		var start = _start
			set(value)
			{
				field = value
				schedule.checkSchedule()
			}

		var end = _end
			set(value)
			{
				field = value
				schedule.checkSchedule()
			}

		init
		{
			schedule.checkSchedule()
		}

		fun overlapsWith(other: ScheduleHour) = start in other.start..other.end
	}

	private val KEY_LENGTH = "schedule_length"
	private val KEY_SCHEDULE_HOUR_N = "schedule_hour_"

	private val MAX_LENGTH = 20

	private val scheduleHours = mutableListOf<Schedule.ScheduleHour>()

	val size: Int
		get() = scheduleHours.size

	init
	{
		loadSchedule(context)
	}

	operator fun get(index: Int) = scheduleHours[index]

	operator fun set(index: Int, scheduleHour: ScheduleHour)
	{
		scheduleHours[index] = scheduleHour
	}

	fun addScheduleHour(start: Time, end: Time): Int?
	{
		if(size >= MAX_LENGTH) return null
		scheduleHours.add(ScheduleHour(this, start, end))
		checkSchedule()
		return size - 1
	}

	fun removeScheduleHour(scheduleHour: ScheduleHour)
	{
		scheduleHours.remove(scheduleHour)
		checkSchedule()
	}

	fun getIndexOf(scheduleHour: ScheduleHour) = scheduleHours.indexOf(scheduleHour)

	private fun checkSchedule()
	{
		for(scheduleHour in scheduleHours)
		{
			scheduleHour.error = when
			{
				!scheduleHour.start.valid || !scheduleHour.end.valid -> Error.INVALID
				scheduleHour.start >= scheduleHour.end -> Error.NEGATIVE_TIMESPAN
				else -> {
					var overlaps = false
					for(comparedHour in scheduleHours)
						if(comparedHour != scheduleHour && scheduleHour.overlapsWith(comparedHour)) overlaps = true
					if(overlaps) Error.OVERLAP else null
				}
			}
		}
	}

	private fun loadSchedule(context: Context)
	{
		scheduleHours.clear()

		val gson = Gson()
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		val length = prefs.getInt(KEY_LENGTH, 0)
		for(i in 0 until length)
		{
			val scheduleHour = gson.fromJson(prefs.getString(KEY_SCHEDULE_HOUR_N + i, ""), Schedule.ScheduleHour::class.java)
			scheduleHour.schedule = this
			scheduleHours.add(scheduleHour)
		}
		checkSchedule()
	}

	fun saveSchedule(context: Context)
	{
		val gson = Gson()
		val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
		editor.putInt(KEY_LENGTH, scheduleHours.size)
		for(i in 0 until scheduleHours.size)
			editor.putString(KEY_SCHEDULE_HOUR_N + i, gson.toJson(scheduleHours[i]))
		editor.apply()
	}

	companion object
	{
		fun loadSchedule(context: Context) = Schedule(context)
	}
}