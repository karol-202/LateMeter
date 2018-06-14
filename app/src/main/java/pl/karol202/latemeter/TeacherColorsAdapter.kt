package pl.karol202.latemeter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class TeacherColorsAdapter(private val context: Context, private val listener: (Int) -> Unit) : RecyclerView.Adapter<TeacherColorsAdapter.ViewHolder>()
{
	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	{
		private val imageColor = view.findViewById<ImageView>(R.id.image_teacher_color)

		private var color: Int? = null

		init
		{
			view.setOnClickListener { onClick() }
		}

		fun onClick()
		{
			listener(color ?: return)
		}

		fun bind(color: Int)
		{
			this.color = color
			imageColor.setColorFilter(color)
		}
	}

	private val colors: List<Int>

	init
	{
		val typedArray = context.resources.obtainTypedArray(R.array.teacher_colors)
		colors = List(typedArray.length()) { typedArray.getColor(it, Color.BLACK) }
		typedArray.recycle()
		println(colors.size)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.item_teacher_color, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount() = colors.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.bind(colors[position])
	}
}