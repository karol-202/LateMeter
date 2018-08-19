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
import pl.karol202.latemeter.schedule.ScheduleHourTeacherAdapter.Element.*
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.teachers.Teachers.Sorting
import pl.karol202.latemeter.utils.findView

class ScheduleHourTeacherAdapter private constructor(
		context: Context,
		elements: List<Element>
) : ArrayAdapter<ScheduleHourTeacherAdapter.Element>(context, 0, elements)
{
	private interface ElementFilter
	sealed class Element
	{
		class TeacherElement(teacherWithId: Teachers.TeacherWithId) : Element()
		{
			val id = teacherWithId.id
			val teacher = teacherWithId.teacher
		}
		object NewTeacherElement : Element(), ElementFilter
		object NoTeacherElement : Element(), ElementFilter
	}

	private inner class ViewHolder(view: View)
	{
		private val imageColor = view.findView<ImageView>(R.id.image_teacher_color)
		private val textName = view.findView<TextView>(R.id.text_teacher_name)

		fun bind(element: Element?)
		{
			imageColor.setImageResource(if(element is TeacherElement) R.drawable.teacher_avatar else R.drawable.ic_add_black_24dp)
			imageColor.alpha = if(element is TeacherElement) 1f else 0.84f
			imageColor.setColorFilter((element as? TeacherElement)?.teacher?.color ?: Color.BLACK)
			imageColor.visibility = if(element != null && element !is NoTeacherElement) View.VISIBLE else View.GONE

			if(element is TeacherElement) textName.text = element.teacher.name
			else textName.setText(if(element is NewTeacherElement) R.string.hint_create_teacher else R.string.hint_select_teacher)
			textName.visibility = if(element != null) View.VISIBLE else View.GONE
		}
	}

	companion object
	{
		fun create(context: Context, teachers: Teachers) =
				ScheduleHourTeacherAdapter(context, createElements(teachers))

		private fun createElements(teachers: Teachers) =
				teachers.sortedBy(Sorting.BY_NAME_ASCENDING).map(Element::TeacherElement) +
						NoTeacherElement +
						NewTeacherElement
	}

	private val ids = elements.filterNot { it is NewTeacherElement }.map { (it as? TeacherElement)?.id }

	//Returns index of teacher of id or index of "no teacher" element if there id is null
	fun getIndexOfTeacherOfId(id: String?) = ids.indexOf(id.takeIf { ids.contains(it) })

	fun getIdOfTeacherAtPosition(index: Int) = (getItem(index) as? TeacherElement)?.id

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
			getView(position, convertView, parent, NewTeacherElement)

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?) =
			getView(position, convertView, parent, NoTeacherElement)

	//Skips elements of type of filter
	private fun getView(position: Int, convertView: View?, parent: ViewGroup?, filter: ElementFilter): View
	{
		val element = getItem(position).takeUnless { it == filter }
		val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_teacher, parent, false)
		val holder = view.tag as? ViewHolder ?: ViewHolder(view).also { view.tag = it }
		holder.bind(element)
		return view
	}
}