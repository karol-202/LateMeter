package pl.karol202.latemeter.schedule

import pl.karol202.latemeter.R

class ScheduleHour(@Transient var daySchedule: DaySchedule, _start: Time, _end: Time)
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
	val valid: Boolean
		get() = start.valid && end.valid

	fun checkError()
	{
		error = when
		{
			!start.valid || !end.valid -> Error.INVALID
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