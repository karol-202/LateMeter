package pl.karol202.latemeter.teachers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pl.karol202.latemeter.R
import pl.karol202.latemeter.schedule.ScheduleHour
import pl.karol202.latemeter.utils.*

class TeacherActivity : AppCompatActivity()
{
	companion object
	{
		const val KEY_ID = "id"
		const val KEY_TEACHER = "teacher"
		const val KEY_SCHEDULE_HOUR = "schedule_hour" //When teacher is created by selecting "Create new teacher" item from schedule hour's teacher menu

		const val RESULT_CANCEL = 0
		const val RESULT_OK = 1
		const val RESULT_REMOVE = 2
	}

	private val statsAdapter by lazy { TeacherStatsAdapter(this, teacher) }

	private val toolbar by view<Toolbar>(R.id.toolbar)
	private val editTextLayoutName by view<TextInputLayout>(R.id.editTextLayout_teacher_name)
	private val editTextName by view<TextInputEditText>(R.id.editText_teacher_name)
	private val panelTeacherColor by view<View>(R.id.panel_teacher_color)
	private val imageTeacherColor by view<ImageView>(R.id.image_teacher_color)
	private val recyclerStats by view<RecyclerView>(R.id.recycler_teacher_stats)
	private val buttonDone by view<FloatingActionButton>(R.id.button_teacher_done)

	private val id by lazy { intent.getString(KEY_ID) }
	private val teacher by lazy { intent.getSerializable(KEY_TEACHER) ?:
		Teacher(string(R.string.default_teacher_name), color(R.color.teacher_default_color)) }
	private val scheduleHour by lazy { intent.getSerializable<ScheduleHour>(KEY_SCHEDULE_HOUR) }

	private var newColor: Int? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_teacher)

		setSupportActionBar(toolbar)
		val actionBar = supportActionBar ?: throw Exception("No action bar")
		actionBar.setDisplayHomeAsUpEnabled(true)

		editTextName.setText(teacher.name)
		editTextName.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?)
			{
				checkName()
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
		})

		panelTeacherColor.setOnClickListener { editTeacherColor() }

		imageTeacherColor.setColorFilter(teacher.color)

		recyclerStats.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
		recyclerStats.adapter = statsAdapter

		buttonDone.setOnClickListener { applyTeacher() }

		setResult(RESULT_CANCEL)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean
	{
		menuInflater.inflate(R.menu.activity_teachers, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId)
	{
		R.id.item_teacher_remove -> showTeacherRemovalDialog().let { true }
		else -> super.onOptionsItemSelected(item)
	}

	private fun checkName(): Boolean
	{
		val valid = !editTextName.text.isNullOrBlank()
		if(!valid) editTextLayoutName.error = getString(R.string.error_teacher_name_blank)
		else editTextLayoutName.isErrorEnabled = false
		return valid
	}

	@SuppressLint("InflateParams")
	private fun editTeacherColor()
	{
		var dialog: AlertDialog? = null

		val adapter = TeacherColorsAdapter(this) { color ->
			newColor = color
			imageTeacherColor.setColorFilter(color)
			dialog?.dismiss()
		}

		val view = LayoutInflater.from(this).inflate(R.layout.dialog_teacher_color, null)

		val recyclerColor = view.findViewById<RecyclerView>(R.id.recycler_dialog_teacher_color)
		recyclerColor.layoutManager = GridLayoutManager(this, 5)
		recyclerColor.adapter = adapter

		val builder = AlertDialog.Builder(this)
		builder.setTitle(R.string.dialog_teacher_color)
		builder.setView(view)
		builder.setNegativeButton(R.string.button_cancel, null)

		dialog = builder.show()
	}

	private fun applyTeacher()
	{
		if(!checkName()) return
		teacher.name = editTextName.text.toString()
		newColor?.let { teacher.color = it }

		finishWithResult(RESULT_OK)
	}

	private fun showTeacherRemovalDialog()
	{
		var dialog: AlertDialog? = null
		val builder = AlertDialog.Builder(this)
		builder.setTitle(R.string.dialog_teacher_remove)
		builder.setMessage(R.string.dialog_teacher_remove_message)
		builder.setPositiveButton(R.string.button_delete) { _, _ -> removeTeacher() }
		builder.setNegativeButton(R.string.button_cancel) { _, _ -> dialog?.dismiss() }
		dialog = builder.show()
	}

	private fun removeTeacher() = finishWithResult(RESULT_REMOVE)

	private fun finishWithResult(result: Int)
	{
		val intent = Intent()
		intent.putExtra(KEY_ID, id)
		intent.putExtra(KEY_TEACHER, teacher)
		intent.putExtra(KEY_SCHEDULE_HOUR, scheduleHour)
		setResult(result, intent)
		finish()
	}
}