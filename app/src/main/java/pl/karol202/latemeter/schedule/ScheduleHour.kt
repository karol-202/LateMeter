package pl.karol202.latemeter.schedule

import pl.karol202.latemeter.R

class ScheduleHour(
		@Transient var daySchedule: DaySchedule,
		_start: Time,
		_end: Time,
		var teacher: String?
) {
	enum class Error(val message: Int)
	{
		NEGATIVE_TIMESPAN(R.string.error_schedule_hour_negative_timespan),
		OVERLAP(R.string.error_schedule_hour_overlap)
	}

	class Time private constructor(
			val hour: Int,
			val minute: Int
	) : Comparable<Time>
	{
		companion object
		{
			val zero = createTime(0, 0) ?: throw Exception("Could not create zero time")

			fun fromMinutes(minutes: Int) = createTime(minutes / 60, minutes % 60)

			fun createTime(hour: Int, minute: Int) = if(hour in 0..23 && minute in 0..59) Time(hour, minute) else null
		}

		init
		{
			if(hour !in 0..23 || minute !in 0..59) throw Exception("Invalid time: $hour:$minute")
		}

		operator fun plus(other: Time): Time?
		{
			val minutes = minute + other.minute
			val hours = hour + other.hour + minutes / 60
			return createTime(hours, minutes % 60)
		}

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

		override fun equals(other: Any?): Boolean
		{
			if(this === other) return true
			if(javaClass != other?.javaClass) return false

			other as Time

			if(hour != other.hour) return false
			if(minute != other.minute) return false

			return true
		}

		override fun hashCode(): Int
		{
			var result = hour
			result = 31 * result + minute
			return result
		}
	}

	var start = _start
		set(value)
		{
			field = value
			daySchedule.checkSchedule()
		}

	var end = _end
		set(value)
		{
			field = value
			daySchedule.checkSchedule()
		}

	@Transient
	var error: Error? = null
		private set

	fun checkError()
	{
		error = when
		{
			start >= end -> Error.NEGATIVE_TIMESPAN
			else -> checkOverlapping()
		}
	}

	private fun checkOverlapping(): Error?
	{
		var overlaps = false
		for(i in 0 until daySchedule.size)
		{
			val comparedHour = daySchedule[i]
			if(comparedHour != this && overlapsWith(comparedHour)) overlaps = true
		}
		return if(overlaps) Error.OVERLAP else null
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