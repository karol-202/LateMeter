package pl.karol202.latemeter.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import pl.karol202.latemeter.R

class SettingsFragment : PreferenceFragmentCompat()
{
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
	{
		addPreferencesFromResource(R.xml.settings)
	}
}