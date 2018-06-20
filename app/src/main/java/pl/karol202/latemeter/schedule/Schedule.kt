package pl.karol202.latemeter.schedule

import android.content.Context
import pl.karol202.latemeter.schedule.DayOfWeek.*

class Schedule private constructor(
		context: Context
) {
	companion object
	{
		fun loadSchedule(context: Context) = Schedule(context)

		private fun DayOfWeek.toPairWithSchedule(context: Context) = this to DaySchedule(context, this)
	}

	private val scheduleDays = mapOf(MONDAY.toPairWithSchedule(context),
									 TUESDAY.toPairWithSchedule(context),
									 WEDNESDAY.toPairWithSchedule(context),
									 THURSDAY.toPairWithSchedule(context),
									 FRIDAY.toPairWithSchedule(context),
									 SATURDAY.toPairWithSchedule(context),
									 SUNDAY.toPairWithSchedule(context))

	init
	{
		loadSchedule(context)
	}

	fun getDaySchedule(dayOfWeek: DayOfWeek) = scheduleDays[dayOfWeek] ?: throw Exception("Unknown day of week: $dayOfWeek")

	private fun loadSchedule(context: Context)
	{
		scheduleDays.values.forEach { it.loadSchedule(context) }
	}
}