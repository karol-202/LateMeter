package pl.karol202.latemeter.teachers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.latemeter.R

class TeachersAdapter(private val context: Context, private val teachers: Teachers, var sorting: Sorting, private val listener: (String, Teacher) -> Unit) : RecyclerView.Adapter<TeachersAdapter.ViewHolder>()
{
	enum class Sorting(val text: Int, val comparator: (Teacher, Teacher) -> Int)
	{
		BY_NAME_ASCENDING(R.string.teachers_sorting_by_name_ascending, { a, b -> a.name.compareTo(b.name, true) }),
		BY_NAME_DESCENDING(R.string.teachers_sorting_by_name_descending, { a, b -> b.name.compareTo(a.name, true) });
	}

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	{
		private val imageAvatar = view.findViewById<AppCompatImageView>(R.id.image_teacher_color)
		private val textName = view.findViewById<TextView>(R.id.text_teacher_name)

		private var id: String? = null
		private var teacher: Teacher? = null

		init
		{
			view.setOnClickListener { onClick() }
		}

		private fun onClick()
		{
			listener(id ?: return, teacher ?: return)
		}

		fun bind(id: String, teacher: Teacher)
		{
			this.id = id
			this.teacher = teacher

			imageAvatar.setColorFilter(teacher.color)
			textName.text = teacher.name
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount() = teachers.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val (id, teacher) = teachers.sortedBy(createComparator(sorting))[position]
		holder.bind(id, teacher)
	}

	private fun createComparator(sorting: Sorting) =
			Comparator<Teachers.TeacherWithId> { a, b -> sorting.comparator(a.teacher, b.teacher) }
}