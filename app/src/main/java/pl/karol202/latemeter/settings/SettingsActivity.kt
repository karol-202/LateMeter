package pl.karol202.latemeter.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import pl.karol202.latemeter.R
import pl.karol202.latemeter.utils.view

class SettingsActivity : AppCompatActivity()
{
	private val toolbar by view<Toolbar>(R.id.toolbar)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)

		setSupportActionBar(toolbar)
		val actionBar = supportActionBar ?: throw Exception("No action bar")
		actionBar.setDisplayHomeAsUpEnabled(true)

		attachSettingsFragment()
	}

	private fun attachSettingsFragment()
	{
		val transaction = supportFragmentManager.beginTransaction()
		transaction.add(R.id.frame_settings, SettingsFragment())
		transaction.commit()
	}
}