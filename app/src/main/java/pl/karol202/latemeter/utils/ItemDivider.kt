package pl.karol202.latemeter.utils

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.latemeter.R

class ItemDivider(
		context: Context
) : RecyclerView.ItemDecoration()
{
	private val divider = context.drawable(R.drawable.item_divider)

	override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State)
	{
		divider ?: return
		val left = parent.paddingLeft
		val right = parent.width - parent.paddingRight

		for(i in 0 until parent.childCount)
		{
			val child = parent.getChildAt(i)
			val params = child.layoutParams as RecyclerView.LayoutParams
			val top = child.bottom + params.bottomMargin
			val bottom = top + divider.intrinsicHeight
			divider.setBounds(left, top, right, bottom)
			divider.draw(canvas)
		}
	}
}