package pl.karol202.latemeter.schedule

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import pl.karol202.latemeter.R

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

	class ScheduleHour(@Transient var schedule: Schedule, _start: Time, _end: Time)
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

		@Transient
		var error: Error? = null
			private set
		val valid: Boolean
			get() = start.valid && end.valid

		init
		{
			schedule.checkSchedule()
		}

		fun checkError()
		{
			error = when
			{
				!start.valid || !end.valid -> Error.INVALID
				start >= end -> Error.NEGATIVE_TIMESPAN
				else -> {
					var overlaps = false
					for(comparedHour in schedule.scheduleHours)
						if(comparedHour != this && overlapsWith(comparedHour)) overlaps = true
					if(overlaps) Error.OVERLAP else null
				}
			}
		}

		private fun overlapsWith(other: ScheduleHour) = start in other.start..other.end

		override fun equals(other: Any?): Boolean
		{
			if(this === other) return true
			if(javaClass != other?.javaClass) return false

			other as ScheduleHour

			if(start != other.start) return false
			if(end != other.end) return false

			return true
		}

		override fun hashCode(): Int
		{
			var result = start.hashCode()
			result = 31 * result + end.hashCode()
			return result
		}
	}

	private val KEY_LENGTH = "schedule_length"
	private val KEY_SCHEDULE_HOUR_N = "schedule_hour_"

	private val MAX_LENGTH = 20

	private val scheduleHours = mutableListOf<ScheduleHour>()

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

	interface OrderUpdate
	class MoveUpdate(val from: Int, val to: Int) : OrderUpdate
	class FullUpdate : OrderUpdate

	fun sortSchedule(): OrderUpdate?
	{
		scheduleHours.forEach { if(!it.valid) return null }
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
				val hoursWithoutCurrent = scheduleHours.toMutableList().filter { it.start != current }
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

	private fun checkSchedule()
	{
		scheduleHours.forEach { it.checkError() }
	}

	private fun loadSchedule(context: Context)
	{
		scheduleHours.clear()

		val gson = Gson()
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		val length = prefs.getInt(KEY_LENGTH, 0)
		for(i in 0 until length)
		{
			val scheduleHour = gson.fromJson(prefs.getString(KEY_SCHEDULE_HOUR_N + i, ""), ScheduleHour::class.java)
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