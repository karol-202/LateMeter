package pl.karol202.latemeter.schedule

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import pl.karol202.latemeter.R
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.utils.findView

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

		fun onSubjectChange(scheduleHour: ScheduleHour, subject: String?): Boolean

		fun onTeacherChange(scheduleHour: ScheduleHour, teacherId: String): Boolean

		fun onTeacherCreate(scheduleHour: ScheduleHour)

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
		private val editTextSubject = view.findView<TextInputEditText>(R.id.editText_schedule_hour_subject)
		private val spinnerTeacher = view.findView<Spinner>(R.id.spinner_schedule_hour_teacher)
		private val textError = view.findView<TextView>(R.id.text_schedule_hour_error)

		private var ordinal: Int? = null
		private var scheduleHour: ScheduleHour? = null
		private var binding = false
		private var boundTeacherSelection: Int? = null

		init
		{
			textStartHour.setOnClickListener { scheduleHour?.let(listener::onStartHourChange) }
			textEndHour.setOnClickListener { scheduleHour?.let(listener::onEndHourChange) }
			imageRemove.setOnClickListener { scheduleHour?.let(listener::onRemove) }
			editTextSubject.addTextChangedListener(object : TextWatcher
			{
				override fun afterTextChanged(s: Editable?)
				{
					if(binding) return
					scheduleHour?.let { hour ->
						val update = listener.onSubjectChange(hour, s.toString())
						if(update) updateError(hour)
					}
				}

				override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

				override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
			})
			spinnerTeacher.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
			{
				override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
				{
					boundTeacherSelection.let {
						boundTeacherSelection = null
						if(it == position) return
					}

					scheduleHour?.let { hour ->
						val teacher = teachersAdapter.getIdOfTeacherAtPosition(position)
						val update = teacher?.let { listener.onTeacherChange(hour, it) } ?: run {
							listener.onTeacherCreate(hour)
							false
						}
						if(update) updateError(hour)
					}
				}

				override fun onNothingSelected(parent: AdapterView<*>?) { }
			}
		}

		fun bind(ordinal: Int, scheduleHour: ScheduleHour)
		{
			this.ordinal = ordinal
			this.scheduleHour = scheduleHour
			binding = true

			textOrdinal.text = (ordinal + 1).toString()
			textStartHour.text = scheduleHour.start.format(context)
			textEndHour.text = scheduleHour.end.format(context)

			editTextSubject.setText(scheduleHour.subject)

			spinnerTeacher.adapter = teachersAdapter
			spinnerTeacher.setSelection(teachersAdapter.getIndexOfTeacherOfId(scheduleHour.teacher))
			boundTeacherSelection = spinnerTeacher.selectedItemPosition

			updateError(scheduleHour)

			binding = false
		}

		fun updateError(scheduleHour: ScheduleHour)
		{
			scheduleHour.error?.let { textError.setText(it.message) }
			textError.visibility = if(scheduleHour.error != null) View.VISIBLE else View.GONE
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