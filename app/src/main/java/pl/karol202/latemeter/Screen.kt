package pl.karol202.latemeter

import androidx.fragment.app.Fragment

enum class Screen(val icon: Int, val itemName: Int, val fragmentSupplier: () -> Fragment)
{
	LATENESS(R.drawable.ic_lateness_black_24dp, R.string.screen_lateness, { LatenessScreen() }),
	TEACHERS(R.drawable.ic_teachers_black_24dp, R.string.screen_teachers, { TeachersScreen() }),
	SCHEDULE(R.drawable.ic_schedule_black_24dp, R.string.screen_schedule, { ScheduleScreen() })
}