package pl.karol202.latemeter

import androidx.fragment.app.Fragment

enum class Screen(val id: Int, val fragmentSupplier: () -> Fragment)
{
	LATENESS(R.id.item_screen_lateness, { LatenessScreen() }),
	TEACHERS(R.id.item_screen_teachers, { TeachersScreen() }),
	SCHEDULE(R.id.item_screen_schedule, { ScheduleScreen() });

	companion object
	{
		fun findScreenById(id: Int): Screen?
		{
			for(screen in values()) if(screen.id == id) return screen
			return null
		}
	}
}