package pl.karol202.latemeter.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import pl.karol202.latemeter.R
import pl.karol202.latemeter.main.Screen
import pl.karol202.latemeter.utils.findView

class ScheduleScreen : Screen()
{
	override val isUsingTabLayout = true

	private val adapter by lazy { DaysFragmentsAdapter(requireContext(), requireFragmentManager()) }

	private lateinit var coordinator: CoordinatorLayout

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.screen_schedule, container, false)

		coordinator = view.findView(R.id.coordinator_schedule)

		val viewPager = view.findView<ViewPager>(R.id.viewPager_schedule)
		viewPager.adapter = adapter
		requireMainActivity().tabLayout.setupWithViewPager(viewPager)

		val buttonAddHour = view.findView<FloatingActionButton>(R.id.button_add_schedule_hour)
		buttonAddHour.setOnClickListener { addScheduleHourToCurrentDay(viewPager) }

		return view
	}

	private fun addScheduleHourToCurrentDay(viewPager: ViewPager)
	{
		val dayOfWeek = DayOfWeek.values()[viewPager.currentItem]
		val currentFragment = adapter.getFragment(dayOfWeek) ?: throw Exception("Current fragment is null")
		currentFragment.addScheduleHour()
	}

	fun showSnackbar(@StringRes message: Int, duration: Int)
	{
		Snackbar.make(coordinator, message, duration).show()
	}
}