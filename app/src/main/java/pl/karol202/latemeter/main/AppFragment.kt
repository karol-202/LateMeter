package pl.karol202.latemeter.main

import android.content.Context
import androidx.fragment.app.Fragment

open class AppFragment : Fragment()
{
	private var mainActivity: MainActivity? = null

	override fun onAttach(context: Context?)
	{
		super.onAttach(context)
		if(context !is MainActivity) throw Exception("Screen can be attached only to MainActivity")
		mainActivity = context
	}

	override fun onDetach()
	{
		super.onDetach()
		mainActivity = null
	}

	protected fun requireMainActivity() = mainActivity ?: throw NullPointerException("MainActivity is null")
}