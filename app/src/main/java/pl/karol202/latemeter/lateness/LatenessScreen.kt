package pl.karol202.latemeter.lateness

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.karol202.latemeter.R
import pl.karol202.latemeter.main.Screen

class LatenessScreen : Screen()
{
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.screen_lateness, container, false)

		return view
	}
}