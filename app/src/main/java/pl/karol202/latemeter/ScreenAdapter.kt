package pl.karol202.latemeter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScreenAdapter(private val context: Context, private val listener: (Screen) -> Unit) : RecyclerView.Adapter<ScreenAdapter.ViewHolder>()
{
	inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
	{
		private val imageIcon = view.findViewById<ImageView>(R.id.image_screen_icon)
		private val textName = view.findViewById<TextView>(R.id.text_screen_name)

		private var item: Screen? = null

		init
		{
			view.setOnClickListener { onClick() }
		}

		private fun onClick()
		{
			listener(item ?: return)
		}

		fun bind(item: Screen)
		{
			this.item = item
			imageIcon.setImageResource(item.icon)
			textName.setText(item.itemName)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.item_screen, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount() = Screen.values().size

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.bind(Screen.values()[position])
	}
}