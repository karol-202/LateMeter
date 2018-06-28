package pl.karol202.latemeter.schedule

import android.content.Context
import pl.karol202.latemeter.schedule.DayOfWeek.*
import pl.karol202.latemeter.teachers.Teachers

class Schedule private constructor(
		context: Context,
		teachers: Teachers
) {
	companion object
	{
		fun loadSchedule(context: Context, teachers: Teachers) = Schedule(context, teachers)

		private fun DayOfWeek.toPairWithSchedule(context: Context, teachers: Teachers) = this to DaySchedule(context, teachers, this)
	}

	private val scheduleDays = mapOf(MONDAY.toPairWithSchedule(context, teachers),
									 TUESDAY.toPairWithSchedule(context, teachers),
									 WEDNESDAY.toPairWithSchedule(context, teachers),
									 THURSDAY.toPairWithSchedule(context, teachers),
									 FRIDAY.toPairWithSchedule(context, teachers),
									 SATURDAY.toPairWithSchedule(context, teachers),
									 SUNDAY.toPairWithSchedule(context, teachers))

	fun getDaySchedule(dayOfWeek: DayOfWeek) = scheduleDays[dayOfWeek] ?: throw Exception("Unknown day of week: $dayOfWeek")
}