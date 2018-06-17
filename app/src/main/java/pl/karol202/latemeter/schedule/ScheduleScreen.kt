package pl.karol202.latemeter.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pl.karol202.latemeter.R
import pl.karol202.latemeter.main.Screen
import pl.karol202.latemeter.utils.findView

class ScheduleScreen : Screen()
{
	override val isUsingTabLayout = true

	private val adapter by lazy { DaysFragmentsAdapter(requireContext(), requireFragmentManager()) }

	var coordinator: CoordinatorLayout? = null
	private var viewPager: ViewPager? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.screen_schedule, container, false)

		coordinator = view.findView(R.id.coordinator_schedule)

		val viewPager = view.findView<ViewPager>(R.id.viewPager_schedule)
		viewPager.adapter = adapter
		requireMainActivity().tabLayout.setupWithViewPager(viewPager)
		this.viewPager = viewPager

		val buttonAddHour = view.findView<FloatingActionButton>(R.id.button_add_schedule_hour)
		buttonAddHour.setOnClickListener { addScheduleHourToCurrentDay() }

		return view
	}

	private fun addScheduleHourToCurrentDay()
	{
		val dayOfWeek = DayOfWeek.values()[viewPager?.currentItem ?: return]
		val currentFragment = adapter.getFragment(dayOfWeek) ?: throw Exception("Current fragment is null")
		currentFragment.addScheduleHour()
	}
}