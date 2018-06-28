package pl.karol202.latemeter.schedule

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import pl.karol202.latemeter.R
import pl.karol202.latemeter.teachers.Teacher
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.teachers.Teachers.Sorting
import pl.karol202.latemeter.utils.findView

class ScheduleHourTeacherAdapter private constructor(
		context: Context,
		teachersWithIds: List<Teachers.TeacherWithId?>
) : ArrayAdapter<Teacher>(context, 0, teachersWithIds.map { it?.teacher })
{
	private inner class ViewHolder(view: View)
	{
		private val imageColor = view.findView<ImageView>(R.id.image_teacher_color)
		private val textName = view.findView<TextView>(R.id.text_teacher_name)

		fun bind(teacher: Teacher?, showCreateItemIfTeacherNull: Boolean)
		{
			if(teacher != null)
			{
				imageColor.setImageResource(R.drawable.teacher_avatar)
				imageColor.alpha = 1f
				imageColor.setColorFilter(teacher.color)
				imageColor.visibility = View.VISIBLE

				textName.text = teacher.name
			}
			else
			{
				imageColor.setImageResource(R.drawable.ic_add_black_24dp)
				imageColor.alpha = 0.84f
				imageColor.setColorFilter(Color.BLACK)
				imageColor.visibility = if(showCreateItemIfTeacherNull) View.VISIBLE else View.GONE

				textName.setText(if(showCreateItemIfTeacherNull) R.string.hint_create_teacher else R.string.hint_select_teacher)
			}
		}
	}

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

	private fun getView(position: Int, showCreateItemIfTeacherNull: Boolean, convertView: View?, parent: ViewGroup?): View
	{
		val teacher: Teacher? = getItem(position)
		val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_teacher, parent, false)
		val holder = view.tag as? ViewHolder ?: ViewHolder(view).also { view.tag = it }
		holder.bind(teacher, showCreateItemIfTeacherNull)
		return view
	}
}