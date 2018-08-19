package pl.karol202.latemeter.teachers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.latemeter.R
import pl.karol202.latemeter.utils.findView

class TeacherStatsAdapter(
		private val context: Context,
		private val teacher: Teacher
) : RecyclerView.Adapter<TeacherStatsAdapter.ViewHolder>()
{
	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	{
		private val textTitle = view.findView<TextView>(R.id.text_teacher_stat_title)
		private val textValue = view.findView<TextView>(R.id.text_teacher_stat_value)

		fun bind(stat: TeacherStats)
		{
			textTitle.setText(stat.title)
			textValue.text = stat.valueSupplier(context, teacher)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.item_teacher_stats, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount() = TeacherStats.values().size

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.bind(TeacherStats.values()[position])
	}
}