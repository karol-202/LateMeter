package pl.karol202.latemeter.schedule

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import pl.karol202.latemeter.R
import pl.karol202.latemeter.main.AppFragment
import pl.karol202.latemeter.settings.Settings
import pl.karol202.latemeter.utils.ItemDivider

class DayScheduleFragment : AppFragment()
{
	companion object
	{
		const val KEY_DAY_OF_WEEK = "dayOfWeek"
	}

	private val teachers by lazy { requireMainActivity().teachers }
	private val daySchedule by lazy { requireMainActivity().schedule.getDaySchedule(dayOfWeek) }
	private val is24h by lazy { DateFormat.is24HourFormat(requireContext()) }
	private val defaultScheduleHourDuration by lazy {
		ScheduleHour.Time.fromMinutes(Settings.getSetting(requireContext(), Settings.DEFAULT_SCHEDULE_HOUR_DURATION)) ?:
		ScheduleHour.Time.zero }

	private val scheduleScreen by lazy { parentFragment as? ScheduleScreen ?:
										 throw Exception("DayScheduleFragment can only be used in scheduleScreen") }
	private val adapter by lazy {
		DayScheduleAdapter(requireContext(), daySchedule, teachers, object : DayScheduleAdapter.OnScheduleHourListener
		{
			override fun onStartHourChange(scheduleHour: ScheduleHour) = showStartHourPicker(scheduleHour)

			override fun onEndHourChange(scheduleHour: ScheduleHour) = showEndHourPicker(scheduleHour)

			override fun onTeacherChange(scheduleHour: ScheduleHour, teacherId: String) =
					this@DayScheduleFragment.onTeacherChange(scheduleHour, teacherId)

			override fun onRemove(scheduleHour: ScheduleHour) = showRemovalDialog(scheduleHour)
		})
	}

	private val dayOfWeek by lazy {arguments?.getSerializable(KEY_DAY_OF_WEEK) as? DayOfWeek ?: throw Exception("dayOfWeek not passed to fragment") }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.fragment_schedule_day, container, false)

		val recyclerScheduleDay = view.findViewById<RecyclerView>(R.id.recycler_schedule_day)
		recyclerScheduleDay.layoutManager = LinearLayoutManager(requireContext())
		recyclerScheduleDay.adapter = adapter
		recyclerScheduleDay.addItemDecoration(ItemDivider(requireContext()))

		return view
	}

	fun addScheduleHour()
	{
		val position = daySchedule.addScheduleHour(ScheduleHour.Time.zero, ScheduleHour.Time.zero)
		if(position == null)
		{
			Snackbar.make(scheduleScreen.coordinator ?: return, R.string.message_schedule_full, Snackbar.LENGTH_SHORT).show()
			return
		}
		daySchedule.saveSchedule(requireContext())
		adapter.notifyItemInserted(position)
		sortScheduleHours()
		adapter.notifyItemRangeChanged(0, daySchedule.size - 1)
	}

	private fun showStartHourPicker(scheduleHour: ScheduleHour)
	{
		val startTime = scheduleHour.start
		val picker = TimePickerDialog(context, { _, h, m -> onStartHourChange(scheduleHour, h, m) }, startTime.hour, startTime.minute, is24h)
		picker.show()
	}

	private fun onStartHourChange(scheduleHour: ScheduleHour, hour: Int, minute: Int)
	{
		scheduleHour.start = ScheduleHour.Time.createTime(hour, minute) ?: throw Exception("Invalid time")
		scheduleHour.end = scheduleHour.start + defaultScheduleHourDuration ?: scheduleHour.end
		daySchedule.saveSchedule(requireContext())
		sortScheduleHours()
		adapter.notifyItemRangeChanged(0, daySchedule.size)
	}

	private fun showEndHourPicker(scheduleHour: ScheduleHour)
	{
		val endTime = scheduleHour.end
		val picker = TimePickerDialog(context, { _, h, m -> onEndHourChange(scheduleHour, h, m) }, endTime.hour, endTime.minute, is24h)
		picker.show()
	}

	private fun onEndHourChange(scheduleHour: ScheduleHour, hour: Int, minute: Int)
	{
		scheduleHour.end = ScheduleHour.Time.createTime(hour, minute) ?: throw Exception("Invalid time")
		daySchedule.saveSchedule(requireContext())
		sortScheduleHours()
		adapter.notifyItemRangeChanged(0, daySchedule.size)
	}

	private fun onTeacherChange(scheduleHour: ScheduleHour, teacherId: String)
	{
		scheduleHour.teacher = teacherId
		daySchedule.saveSchedule(requireContext())
	}

	private fun showRemovalDialog(scheduleHour: ScheduleHour)
	{
		val builder = AlertDialog.Builder(requireContext())
		builder.setTitle(R.string.dialog_schedule_hour_remove)
		builder.setMessage(R.string.dialog_schedule_hour_remove_message)
		builder.setPositiveButton(R.string.button_delete) { _, _ -> onRemove(scheduleHour) }
		builder.setNegativeButton(R.string.button_cancel, null)
		builder.show()
	}

	private fun onRemove(scheduleHour: ScheduleHour)
	{
		val position = daySchedule.getIndexOf(scheduleHour)
		daySchedule.removeScheduleHour(scheduleHour)
		daySchedule.saveSchedule(requireContext())
		adapter.notifyItemRemoved(position)
		sortScheduleHours()
		adapter.notifyItemRangeChanged(0, daySchedule.size)
	}

	private fun sortScheduleHours()
	{
		val update = daySchedule.sortSchedule()
		when(update)
		{
			is DaySchedule.FullUpdate -> adapter.notifyDataSetChanged()
			is DaySchedule.MoveUpdate -> adapter.notifyItemMoved(update.from, update.to)
		}
	}
}