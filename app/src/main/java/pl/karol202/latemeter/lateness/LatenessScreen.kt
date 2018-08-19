package pl.karol202.latemeter.lateness

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import pl.karol202.latemeter.R
import pl.karol202.latemeter.main.Screen
import pl.karol202.latemeter.schedule.DayOfWeek
import pl.karol202.latemeter.schedule.DaySchedule
import pl.karol202.latemeter.schedule.Schedule
import pl.karol202.latemeter.schedule.ScheduleHour
import pl.karol202.latemeter.teachers.Teacher
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.utils.Date
import pl.karol202.latemeter.utils.Time
import pl.karol202.latemeter.utils.findView
import pl.karol202.latemeter.utils.string
import java.util.*

class LatenessScreen : Screen()
{
	private class Now(
			private val teachers: Teachers,
			private val schedule: Schedule
	) {
		lateinit var calendar: Calendar
		lateinit var currentDate: Date
		lateinit var currentTime: Time
		lateinit var daySchedule: DaySchedule
		var hour: ScheduleHour? = null
		var teacher: Teacher? = null

		init
		{
			update()
		}

		fun update()
		{
			calendar = Calendar.getInstance()
			currentDate = getCurrentDate(calendar)
			currentTime = getCurrentTime(calendar)
			daySchedule = getCurrentDaySchedule(calendar)
			hour = daySchedule.getCurrentScheduleHour(currentTime)
			teacher = hour?.teacher?.let { teachers[it] }
		}

		private fun getCurrentDaySchedule(calendar: Calendar) = schedule.getDaySchedule(getCurrentDayOfWeek(calendar))

		private fun getCurrentDayOfWeek(calendar: Calendar) = DayOfWeek.values()[(calendar[Calendar.DAY_OF_WEEK] + 5) % 7]

		private fun getCurrentDate(calendar: Calendar) =
				Date(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

		private fun getCurrentTime(calendar: Calendar) =
				Time.createTime(calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], calendar[Calendar.SECOND]) ?:
				throw Exception()
	}

	companion object
	{
		private const val UPDATE_TIME = 1000
	}

	private val teachers by lazy { requireMainActivity().teachers }
	private val schedule by lazy { requireMainActivity().schedule }
	@get:JvmName("getC") private val context by lazy { requireContext() }
	private val handler = Handler()

	@get:JvmName("getV") private var view: View? = null
	private val textNow by lazy { view!!.findView<TextView>(R.id.text_now) }
	private val imageTeacherColor by lazy { view!!.findView<ImageView>(R.id.image_now_teacher_color) }
	private val textTeacherName by lazy { view!!.findView<TextView>(R.id.text_now_teacher_name) }
	private val textLateness by lazy { view!!.findView<TextView>(R.id.text_lateness) }
	private val buttonTeacherArrive by lazy { view!!.findView<Button>(R.id.button_teacher_arrive) }

	private val now by lazy { Now(teachers, schedule) }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		view = inflater.inflate(R.layout.screen_lateness, container, false)

		buttonTeacherArrive.setOnClickListener { onTeacherArrive() }

		onUpdate()

		return view
	}

	private fun onUpdate()
	{
		update()
		handler.postDelayed({ onUpdate() }, UPDATE_TIME.toLong())
	}

	private fun update()
	{
		now.update()

		if(now.hour != null && now.teacher != null)
		{
			val hour = now.hour!!
			val teacher = now.teacher!!
			val currentTardy = teacher.findTardy(now.currentDate, hour.start)

			textNow.text = context.string(R.string.text_now, hour.subject, hour.start.format(context), hour.end.format(context))

			imageTeacherColor.visibility = View.VISIBLE
			imageTeacherColor.setColorFilter(teacher.color)

			textTeacherName.visibility = View.VISIBLE
			textTeacherName.text = teacher.name

			textLateness.visibility = View.VISIBLE
			textLateness.text = currentTardy?.duration?.format(context, true) ?:
					(now.currentTime - hour.start).format(context, true)

			buttonTeacherArrive.visibility = View.VISIBLE
			buttonTeacherArrive.isEnabled = currentTardy == null
		}
		else
		{
			textNow.text = context.getString(R.string.text_now_nothing)

			imageTeacherColor.visibility = View.GONE

			textTeacherName.visibility = View.GONE

			textLateness.visibility = View.GONE

			buttonTeacherArrive.visibility = View.GONE
		}
	}

	private fun onTeacherArrive()
	{
		now.update()

		val hour = now.hour ?: return
		val teacher = now.teacher ?: return
		val duration = now.currentTime - hour.start
		val tardy = Tardy(now.currentDate, hour.start, duration)
		teacher.addTardy(tardy)

		update()
	}
}