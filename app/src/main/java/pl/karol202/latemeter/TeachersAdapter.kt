package pl.karol202.latemeter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeachersAdapter(private val context: Context, private val teachers: Teachers, private val listener: (Teacher) -> Unit) : RecyclerView.Adapter<TeachersAdapter.ViewHolder>()
{
	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	{
		private val imageAvatar = view.findViewById<ImageView>(R.id.image_teacher_avatar)
		private val textName = view.findViewById<TextView>(R.id.text_teacher_name)

		private var teacher: Teacher? = null

		init
		{
			view.setOnClickListener { onClick() }
		}

		private fun onClick()
		{
			listener(teacher ?: return)
		}

		fun bind(teacher: Teacher)
		{
			this.teacher = teacher

			imageAvatar.imageTintList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.RED))
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
		val teacher = teachers[position]
		holder.bind(teacher)
	}
}