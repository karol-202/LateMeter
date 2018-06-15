package pl.karol202.latemeter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ScheduleAdapter(private val context: Context, private val schedule: Schedule, private val listener: OnScheduleHourListener) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>()
{
	interface OnScheduleHourListener
	{
		fun onStartHourChange(scheduleHour: Schedule.ScheduleHour)

		fun onEndHourChange(scheduleHour: Schedule.ScheduleHour)

		fun onRemove(scheduleHour: Schedule.ScheduleHour)
	}

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	{
		private val textOrdinal = view.findViewById<TextView>(R.id.text_schedule_hour_ordinal)
		private val textStartHour = view.findViewById<TextView>(R.id.text_schedule_hour_start)
		private val textEndHour = view.findViewById<TextView>(R.id.text_schedule_hour_end)
		private val imageRemove = view.findViewById<ImageView>(R.id.image_schedule_hour_remove)
		private val textError = view.findViewById<TextView>(R.id.text_schedule_hour_error)

		private var scheduleHour: Schedule.ScheduleHour? = null

		init
		{
			textStartHour.setOnClickListener { scheduleHour?.let { listener.onStartHourChange(it) } }
			textEndHour.setOnClickListener { scheduleHour?.let { listener.onEndHourChange(it) } }
			imageRemove.setOnClickListener { scheduleHour?.let { listener.onRemove(it) } }
		}

		fun bind(ordinal: Int, scheduleHour: Schedule.ScheduleHour)
		{
			this.scheduleHour = scheduleHour

			textOrdinal.text = (ordinal + 1).toString()
			textStartHour.text = formatTime(scheduleHour.start)
			textEndHour.text = formatTime(scheduleHour.end)

			scheduleHour.error?.let { textError.setText(it.message) }
			textError.visibility = if(scheduleHour.error != null) View.VISIBLE else View.GONE
		}

		private fun formatTime(time: Schedule.Time): String
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

	override fun getItemCount() = schedule.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.bind(position, schedule[position])
	}
}