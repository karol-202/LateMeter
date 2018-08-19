package pl.karol202.latemeter.main

import pl.karol202.latemeter.R
import pl.karol202.latemeter.lateness.LatenessScreen
import pl.karol202.latemeter.schedule.ScheduleScreen
import pl.karol202.latemeter.teachers.TeachersScreen

enum class ScreenType(val id: Int, val fragmentSupplier: () -> Screen)
{
	LATENESS(R.id.item_screen_lateness, { LatenessScreen() }),
	TEACHERS(R.id.item_screen_teachers, { TeachersScreen() }),
	SCHEDULE(R.id.item_screen_schedule, { ScheduleScreen() });

	companion object
	{
		fun findScreenById(id: Int) = values().find { it.id == id }
	}
}