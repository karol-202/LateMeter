package pl.karol202.latemeter.schedule

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter

class DaysFragmentsAdapter(
		private val context: Context,
		private val fragmentManager: FragmentManager
) : PagerAdapter()
{
	private val fragments = mutableMapOf<DayOfWeek, DayScheduleFragment?>(DayOfWeek.MONDAY to null,
																          DayOfWeek.TUESDAY to null,
																          DayOfWeek.WEDNESDAY to null,
																          DayOfWeek.THURSDAY to null,
																          DayOfWeek.FRIDAY to null,
																          DayOfWeek.SATURDAY to null,
																          DayOfWeek.SUNDAY to null)

	override fun getCount() = fragments.size

	override fun instantiateItem(container: ViewGroup, position: Int): DayScheduleFragment
	{
		val dayOfWeek = DayOfWeek.values()[position]
		val fragment = fragments[dayOfWeek] ?: run {
			val fragment = createFragmentForDay(dayOfWeek)
			fragments[dayOfWeek] = fragment
			fragment
		}

		val transaction = fragmentManager.beginTransaction()
		transaction.add(container.id, fragment)
		transaction.commit()
		return fragment
	}

	private fun createFragmentForDay(dayOfWeek: DayOfWeek): DayScheduleFragment
	{
		val args = Bundle()
		args.putSerializable(DayScheduleFragment.KEY_DAY_OF_WEEK, dayOfWeek)

		val fragment = DayScheduleFragment()
		fragment.arguments = args
		return fragment
	}

	override fun destroyItem(container: ViewGroup, position: Int, obj: Any)
	{
		val dayOfWeek = DayOfWeek.values()[position]
		val transaction = fragmentManager.beginTransaction()
		transaction.remove(fragments[dayOfWeek] ?: return)
		transaction.commit()
		fragments[dayOfWeek] = null
	}

	override fun isViewFromObject(view: View, obj: Any) = (obj as DayScheduleFragment).view == view

	override fun getPageTitle(position: Int): CharSequence = context.getString(DayOfWeek.values()[position].dayName)

	fun getFragment(dayOfWeek: DayOfWeek) = fragments[dayOfWeek]
}