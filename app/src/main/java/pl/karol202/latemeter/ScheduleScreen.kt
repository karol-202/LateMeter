package pl.karol202.latemeter

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ScheduleScreen : Fragment()
{
	private val is24h by lazy { DateFormat.is24HourFormat(requireContext()) }
	private val schedule by lazy { Schedule.loadSchedule(requireContext()) }

	private val adapter by lazy { ScheduleAdapter(requireContext(), schedule, object : ScheduleAdapter.OnScheduleHourListener {
		override fun onStartHourChange(scheduleHour: Schedule.ScheduleHour) = showStartHourPicker(scheduleHour)

		override fun onEndHourChange(scheduleHour: Schedule.ScheduleHour) = showEndHourPicker(scheduleHour)

		override fun onRemove(scheduleHour: Schedule.ScheduleHour) = showRemovalDialog(scheduleHour)
	}) }

	var coordinator: CoordinatorLayout? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.screen_schedule, container, false)

		coordinator = view.findViewById(R.id.coordinator_schedule)

		val recyclerSchedule = view.findViewById<RecyclerView>(R.id.recycler_schedule)
		recyclerSchedule.layoutManager = LinearLayoutManager(requireContext())
		recyclerSchedule.adapter = adapter
		recyclerSchedule.addItemDecoration(ItemDivider(requireContext()))

		val buttonAddHour = view.findViewById<FloatingActionButton>(R.id.button_add_schedule_hour)
		buttonAddHour.setOnClickListener { addScheduleHour() }

		return view
	}

	private fun addScheduleHour()
	{
		val position = schedule.addScheduleHour(Schedule.Time(0, 0), Schedule.Time(0, 0))
		if(position == null)
		{
			Snackbar.make(coordinator ?: return, R.string.message_schedule_full, Snackbar.LENGTH_SHORT).show()
			return
		}
		schedule.saveSchedule(requireContext())
		adapter.notifyItemInserted(position)
		adapter.notifyItemRangeChanged(0, schedule.size - 1)
	}

	private fun showStartHourPicker(scheduleHour: Schedule.ScheduleHour)
	{
		val startTime = scheduleHour.start
		val picker = TimePickerDialog(context, { _, h, m -> onStartHourChange(scheduleHour, h, m) }, startTime.hour, startTime.minute, is24h)
		picker.show()
	}

	private fun onStartHourChange(scheduleHour: Schedule.ScheduleHour, hour: Int, minute: Int)
	{
		scheduleHour.start = Schedule.Time(hour, minute)
		schedule.saveSchedule(requireContext())
		adapter.notifyItemRangeChanged(0, schedule.size)
	}

	private fun showEndHourPicker(scheduleHour: Schedule.ScheduleHour)
	{
		val endTime = scheduleHour.end
		val picker = TimePickerDialog(context, { _, h, m -> onEndHourChange(scheduleHour, h, m) }, endTime.hour, endTime.minute, is24h)
		picker.show()
	}

	private fun onEndHourChange(scheduleHour: Schedule.ScheduleHour, hour: Int, minute: Int)
	{
		scheduleHour.end = Schedule.Time(hour, minute)
		schedule.saveSchedule(requireContext())
		adapter.notifyItemRangeChanged(0, schedule.size)
	}

	private fun showRemovalDialog(scheduleHour: Schedule.ScheduleHour)
	{
		val builder = AlertDialog.Builder(requireContext())
		builder.setTitle(R.string.dialog_schedule_hour_remove)
		builder.setMessage(R.string.dialog_schedule_hour_remove_message)
		builder.setPositiveButton(R.string.button_delete) { _, _ -> onRemove(scheduleHour) }
		builder.setNegativeButton(R.string.button_cancel, null)
		builder.show()
	}

	private fun onRemove(scheduleHour: Schedule.ScheduleHour)
	{
		val position = schedule.getIndexOf(scheduleHour)
		schedule.removeScheduleHour(scheduleHour)
		schedule.saveSchedule(requireContext())
		adapter.notifyItemRemoved(position)
		adapter.notifyItemRangeChanged(0, schedule.size)
	}
}