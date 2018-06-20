package pl.karol202.latemeter.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.latemeter.R
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.utils.findView
import java.util.*

class DayScheduleAdapter(
		private val context: Context,
		private val daySchedule: DaySchedule,
		private val teachers: Teachers,
		private val listener: OnScheduleHourListener
) : RecyclerView.Adapter<DayScheduleAdapter.ViewHolder>()
{
	interface OnScheduleHourListener
	{
		fun onStartHourChange(scheduleHour: ScheduleHour)

		fun onEndHourChange(scheduleHour: ScheduleHour)

		fun onTeacherChange(scheduleHour: ScheduleHour, teacherId: String)

		fun onRemove(scheduleHour: ScheduleHour)
	}

	inner class ViewHolder(
			view: View
	) : RecyclerView.ViewHolder(view)
	{
		private val teachersAdapter = ScheduleHourTeacherAdapter.create(context, teachers)

		private val textOrdinal = view.findView<TextView>(R.id.text_schedule_hour_ordinal)
		private val textStartHour = view.findView<TextView>(R.id.text_schedule_hour_start)
		private val textEndHour = view.findView<TextView>(R.id.text_schedule_hour_end)
		private val imageRemove = view.findView<ImageView>(R.id.image_schedule_hour_remove)
		private val spinnerTeacher = view.findView<Spinner>(R.id.spinner_schedule_hour_teacher)
		private val textError = view.findView<TextView>(R.id.text_schedule_hour_error)

		private var scheduleHour: ScheduleHour? = null

		init
		{
			textStartHour.setOnClickListener { scheduleHour?.let { listener.onStartHourChange(it) } }
			textEndHour.setOnClickListener { scheduleHour?.let { listener.onEndHourChange(it) } }
			imageRemove.setOnClickListener { scheduleHour?.let { listener.onRemove(it) } }
		}

		fun bind(ordinal: Int, scheduleHour: ScheduleHour)
		{
			this.scheduleHour = scheduleHour

			textOrdinal.text = (ordinal + 1).toString()
			textStartHour.text = formatTime(scheduleHour.start)
			textEndHour.text = formatTime(scheduleHour.end)

			spinnerTeacher.adapter = teachersAdapter
			spinnerTeacher.setSelection(teachersAdapter.getIndexOfTeacherOfId(scheduleHour.teacher))
			spinnerTeacher.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
			{
				override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
				{
					listener.onTeacherChange(scheduleHour, teachersAdapter.getIdOfTeacherByPosition(position) ?: return)
				}

				override fun onNothingSelected(parent: AdapterView<*>?) { }
			}

			scheduleHour.error?.let { textError.setText(it.message) }
			textError.visibility = if(scheduleHour.error != null) View.VISIBLE else View.GONE
		}

		private fun formatTime(time: ScheduleHour.Time): String
		{
			val format = android.text.format.DateFormat.getTimeFormat(context)
			val calendar = GregorianCalendar.getInstance()
			calendar[Calendar.HOUR_OF_DAY] = time.hour
			calendar[Calendar.MINUTE] = time.minute
			return format.format(calendar.time)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.item_schedule_hour, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount() = daySchedule.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.bind(position, daySchedule[position])
	}
}