package pl.karol202.latemeter.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import pl.karol202.latemeter.R
import pl.karol202.latemeter.schedule.Schedule
import pl.karol202.latemeter.settings.SettingsActivity
import pl.karol202.latemeter.teachers.Teachers
import pl.karol202.latemeter.utils.view

class MainActivity : AppCompatActivity()
{
	companion object
	{
		private const val KEY_SCREEN = "screen"
	}

	val teachers by lazy { Teachers.loadTeachers(this) }
	val schedule by lazy { Schedule.loadSchedule(this, teachers) }

	private val toolbar by view<Toolbar>(R.id.toolbar)
	val tabLayout by view<TabLayout>(R.id.tabLayout_main)
	private val drawerLayout by view<DrawerLayout>(R.id.drawerLayout_main)
	private val navigationView by view<NavigationView>(R.id.navigation_view_main)

	private var screen: ScreenType? = null
		set(value)
		{
			if(value == null) throw Exception("Cannot set null screen")
			field = value
			navigationView.setCheckedItem(value.id)
			updateScreen()
		}

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
	    restoreState(savedInstanceState)

	    setSupportActionBar(toolbar)
	    val actionBar = supportActionBar ?: throw Exception("No action bar")
	    actionBar.setDisplayHomeAsUpEnabled(true)
	    actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

	    navigationView.setNavigationItemSelectedListener { onNavigationItemSelected(it) }
    }

	private fun restoreState(state: Bundle?)
	{
		state ?: return
		screen = (state[KEY_SCREEN] as? ScreenType) ?: return
	}

	override fun onResume()
	{
		super.onResume()
		screen = screen ?: ScreenType.LATENESS
	}

	override fun onSaveInstanceState(outState: Bundle?)
	{
		if(outState == null) return
		outState.putSerializable(KEY_SCREEN, screen)
		super.onSaveInstanceState(outState)
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId)
	{
		android.R.id.home -> {
			drawerLayout.openDrawer(GravityCompat.START)
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	private fun onNavigationItemSelected(item: MenuItem): Boolean
	{
		drawerLayout.closeDrawers()
		return when(item.itemId)
		{
			R.id.item_settings -> {
				startSettingsActivity()
				true
			}
			else -> onScreenItemSelected(item)
		}
	}

	private fun startSettingsActivity()
	{
		val intent = Intent(this, SettingsActivity::class.java)
		startActivity(intent)
	}

	private fun onScreenItemSelected(item: MenuItem): Boolean
	{
		screen = ScreenType.findScreenById(item.itemId) ?: return false
		return true
	}

	private fun updateScreen()
	{
		val fragment = (screen ?: throw Exception("Cannot show null screen")).fragmentSupplier()
		tabLayout.visibility = if(fragment.isUsingTabLayout) View.VISIBLE else View.GONE

		val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_main)
		if(currentFragment != null && currentFragment::class == fragment::class) return

		val transaction = supportFragmentManager.beginTransaction()
		transaction.replace(R.id.frame_main, fragment)
		transaction.commit()
	}
}
