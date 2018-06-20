package pl.karol202.latemeter.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import pl.karol202.latemeter.R

class SummarizedEditTextPreference(
		private val preferenceContext: Context,
		attrs: AttributeSet
) : EditTextPreference(preferenceContext, attrs)
{
	override fun setText(text: String?)
	{
		super.setText(text)
		summary = text
	}

	override fun getSummary(): CharSequence? = preferenceContext.resources.getQuantityString(R.plurals.setting_schedule_hour_duration_summary, text.toInt(), text.toInt())
}