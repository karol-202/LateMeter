package pl.karol202.latemeter.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import pl.karol202.latemeter.R
import pl.karol202.latemeter.teachers.Teacher
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.teachers.Teachers.Sorting
import pl.karol202.latemeter.utils.findView

class ScheduleHourTeacherAdapter private constructor(
		@get:JvmName("ctx") private val context: Context,
		teachersWithIds: List<Teachers.TeacherWithId?>
) : ArrayAdapter<Teacher>(context, 0, teachersWithIds.map { it?.teacher })
{
	companion object
	{
		fun create(context: Context, teachers: Teachers) =
				ScheduleHourTeacherAdapter(context, createTeachersWithIdsList(teachers))

		private fun createTeachersWithIdsList(teachers: Teachers) =
				teachers.sortedBy(Sorting.BY_NAME_ASCENDING).map { it as Teachers.TeacherWithId? } +
						null as Teachers.TeacherWithId?
	}

	private val ids = teachersWithIds.map { it?.id }

	fun getIndexOfTeacherOfId(id: String?) = ids.indexOf(id.takeIf { ids.contains(it) })

	fun getIdOfTeacherAtPosition(index: Int) = ids[index]

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
			getView(position, false, convertView, parent)

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?) =
			getView(position, true, convertView, parent)

	private fun getView(position: Int, hideNull: Boolean, convertView: View?, parent: ViewGroup?): View
	{
		val teacher = getItem(position)

		val view = if(teacher == null && hideNull) return View(context)
				   else convertView?.takeIf { it is LinearLayout } ?:
						LayoutInflater.from(context).inflate(R.layout.spinner_item_teacher, parent, false)

		val imageColor = view.findView<ImageView>(R.id.image_teacher_color)
		imageColor.setColorFilter(teacher?.color ?: -1)
		imageColor.visibility = if(teacher != null) View.VISIBLE else View.GONE

		val textName = view.findView<TextView>(R.id.text_teacher_name)
		textName.text = teacher?.name ?: context.getText(R.string.hint_select_teacher)

		return view
	}
}