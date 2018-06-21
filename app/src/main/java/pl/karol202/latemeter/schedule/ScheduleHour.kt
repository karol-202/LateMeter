package pl.karol202.latemeter.schedule

import pl.karol202.latemeter.R

class ScheduleHour(
		@Transient var daySchedule: DaySchedule,
		_start: Time,
		_end: Time,
		var subject: String?,
		var teacher: String?
) {
	enum class Error(val message: Int)
	{
		NEGATIVE_TIMESPAN(R.string.error_schedule_hour_negative_timespan),
		OVERLAP(R.string.error_schedule_hour_overlap)
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

		if(daySchedule != other.daySchedule) return false
		if(subject != other.subject) return false
		if(teacher != other.teacher) return false
		if(start != other.start) return false
		if(end != other.end) return false
		if(error != other.error) return false

		return true
	}

	override fun hashCode(): Int
	{
		var result = daySchedule.hashCode()
		result = 31 * result + (subject?.hashCode() ?: 0)
		result = 31 * result + (teacher?.hashCode() ?: 0)
		result = 31 * result + start.hashCode()
		result = 31 * result + end.hashCode()
		result = 31 * result + (error?.hashCode() ?: 0)
		return result
	}
}