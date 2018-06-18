package pl.karol202.latemeter.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import pl.karol202.latemeter.R

class SummarizedEditTextPreference(private val preferenceContext: Context, attrs: AttributeSet) : EditTextPreference(preferenceContext, attrs)
{
	override fun setText(text: String?)
	{
		super.setText(text)
		summary = text
	}

	override fun getSummary(): CharSequence? = preferenceContext.resources.getQuantityString(R.plurals.preference_schedule_hour_duration_value, text.toInt(), text.toInt())
}