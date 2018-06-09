package pl.karol202.latemeter

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity()
{
	private val KEY_SCREEN = "screen"

	private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
	private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout_main) }
	private val recyclerDrawer by lazy { findViewById<RecyclerView>(R.id.recycler_drawer) }

	private var screen: Screen = Screen.LATENESS
		set(value)
		{
			field = value
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

	    recyclerDrawer.layoutManager = LinearLayoutManager(this)
	    recyclerDrawer.adapter = ScreenAdapter(this) { onScreenSelected(it) }

	    updateScreen()
    }

	private fun restoreState(state: Bundle?)
	{
		if(state == null) return
		screen = state[KEY_SCREEN] as Screen
	}

	override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?)
	{
		if(outState == null) return
		outState.putSerializable(KEY_SCREEN, screen)
		super.onSaveInstanceState(outState, outPersistentState)
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId)
	{
		android.R.id.home -> {
			drawerLayout.openDrawer(GravityCompat.START)
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	private fun onScreenSelected(screen: Screen)
	{
		this.screen = screen
		drawerLayout.closeDrawers()
	}

	private fun updateScreen()
	{
		val transaction = supportFragmentManager.beginTransaction()
		transaction.replace(R.id.frame_layout_main, screen.fragmentSupplier())
		transaction.commit()
	}
}
