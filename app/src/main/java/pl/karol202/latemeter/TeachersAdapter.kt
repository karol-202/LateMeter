package pl.karol202.latemeter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView

class TeachersAdapter(private val context: Context, private val teachers: Teachers, private val listener: (Int, Teacher) -> Unit) : RecyclerView.Adapter<TeachersAdapter.ViewHolder>()
{
	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	{
		private val imageAvatar = view.findViewById<AppCompatImageView>(R.id.image_teacher_color)
		private val textName = view.findViewById<TextView>(R.id.text_teacher_name)

		private var index: Int? = null
		private var teacher: Teacher? = null

		init
		{
			view.setOnClickListener { onClick() }
		}

		private fun onClick()
		{
			listener(index ?: return, teacher ?: return)
		}

		fun bind(index: Int, teacher: Teacher)
		{
			this.index = index
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
		holder.bind(position, teachers[position])
	}
}