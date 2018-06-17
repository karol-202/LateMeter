package pl.karol202.latemeter.main

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
import pl.karol202.latemeter.utils.findView

class MainActivity : AppCompatActivity()
{
	private val KEY_SCREEN = "screen"

	val schedule by lazy { Schedule.loadSchedule(this) }

	private val toolbar by lazy { findView<Toolbar>(R.id.toolbar) }
	val tabLayout by lazy { findView<TabLayout>(R.id.tabLayout_main) }
	private val drawerLayout by lazy { findView<DrawerLayout>(R.id.drawerLayout_main) }
	private val navigationViewScreens by lazy { findView<NavigationView>(R.id.navigation_view_screens) }

	private var screen: Screens? = null
		set(value)
		{
			if(value == null) throw Exception("Cannot set null screen")
			field = value
			navigationViewScreens.setCheckedItem(value.id)
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

	    navigationViewScreens.setNavigationItemSelectedListener { onScreenItemSelected(it) }
    }

	private fun restoreState(state: Bundle?)
	{
		if(state == null) return
		screen = (state[KEY_SCREEN] as? Screens) ?: return
	}

	override fun onResume()
	{
		super.onResume()
		if(screen == null) screen = Screens.LATENESS
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

	private fun onScreenItemSelected(item: MenuItem): Boolean
	{
		screen = Screens.findScreenById(item.itemId) ?: return false
		drawerLayout.closeDrawers()
		return true
	}

	private fun updateScreen()
	{
		val screen = (screen ?: throw Exception("Cannot show null screen")).fragmentSupplier()
		tabLayout.visibility = if(screen.isUsingTabLayout) View.VISIBLE else View.GONE

		val transaction = supportFragmentManager.beginTransaction()
		transaction.replace(R.id.frame_main, screen)
		transaction.commit()
	}
}
