package pl.karol202.latemeter.schedule

import pl.karol202.latemeter.R
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.utils.Time
import java.io.Serializable

class ScheduleHour(
		@Transient var daySchedule: DaySchedule,
		@Transient var teachers: Teachers,
		start: Time,
		end: Time,
		subject: String?,
		teacher: String?
) : Serializable {
	enum class Error(val message: Int)
	{
		NEGATIVE_TIMESPAN(R.string.error_schedule_hour_negative_timespan),
		OVERLAP(R.string.error_schedule_hour_overlap),
		SUBJECT_BLANK(R.string.error_schedule_hour_subject_blank),
		TEACHER_BLANK(R.string.error_schedule_hour_teacher_blank)
	}

	var start = start
		set(value)
		{
			field = value
			daySchedule.checkSchedule()
		}

	var end = end
		set(value)
		{
			field = value
			daySchedule.checkSchedule()
		}

	var subject = subject
		set(value)
		{
			field = value
			daySchedule.checkSchedule()
		}

	var teacher = teacher
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
			daySchedule.checkIfHourIsOverlappingWithOther(this) -> Error.OVERLAP
			subject.isNullOrBlank() -> Error.SUBJECT_BLANK
			teacher?.let { it !in teachers } ?: true -> Error.TEACHER_BLANK
			else -> null
		}
	}

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(javaClass != other?.javaClass) return false

		other as ScheduleHour

		if(start != other.start) return false
		if(end != other.end) return false
		if(subject != other.subject) return false
		if(teacher != other.teacher) return false

		return true
	}

	override fun hashCode(): Int
	{
		var result = start.hashCode()
		result = 31 * result + end.hashCode()
		result = 31 * result + (subject?.hashCode() ?: 0)
		result = 31 * result + (teacher?.hashCode() ?: 0)
		return result
	}
}