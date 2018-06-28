package pl.karol202.latemeter.teachers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.latemeter.R
import pl.karol202.latemeter.utils.findView
import pl.karol202.latemeter.utils.format

class TeachersAdapter(
		private val context: Context,
		private val teachers: Teachers,
		var sorting: Teachers.Sorting,
		private val listener: (String, Teacher) -> Unit
) : RecyclerView.Adapter<TeachersAdapter.ViewHolder>()
{
	inner class ViewHolder(
			view: View
	) : RecyclerView.ViewHolder(view)
	{
		private val imageAvatar = view.findView<AppCompatImageView>(R.id.image_teacher_color)
		private val textName = view.findView<TextView>(R.id.text_teacher_name)
		private val textAverageLateness = view.findView<TextView>(R.id.text_teacher_average_lateness)

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
			textAverageLateness.text = teacher.averageOfTardies.format(context, true)
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
		val (id, teacher) = teachers.sortedBy(sorting)[position]
		holder.bind(id, teacher)
	}
}