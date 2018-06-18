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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pl.karol202.latemeter.R
import pl.karol202.latemeter.utils.findView

class TeacherActivity : AppCompatActivity()
{
	companion object
	{
		val KEY_INDEX = "index"
		val KEY_TEACHER = "teacher"

		val RESULT_CANCEL = 0
		val RESULT_OK = 1
		val RESULT_REMOVE = 2
	}

	private val toolbar by lazy { findView<Toolbar>(R.id.toolbar) }
	private val editTextLayoutName by lazy { findView<TextInputLayout>(R.id.editTextLayout_teacher_name) }
	private val editTextName by lazy { findView<TextInputEditText>(R.id.editText_teacher_name) }
	private val itemTeacherColor by lazy { findView<View>(R.id.item_teacher_color) }
	private val imageTeacherColor by lazy { findView<ImageView>(R.id.image_teacher_color) }
	private val buttonDone by lazy { findView<FloatingActionButton>(R.id.button_teacher_done) }

	private val index: Int by lazy { intent.getIntExtra(KEY_INDEX, -1) }
	private val teacher: Teacher by lazy {
		(intent.getSerializableExtra(KEY_TEACHER) as? Teacher) ?: Teacher(getString(R.string.default_teacher_name), ResourcesCompat.getColor(resources, R.color.teacher_default_color, null))
	}

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

		itemTeacherColor.setOnClickListener { editTeacherColor() }

		imageTeacherColor.setColorFilter(teacher.color)

		buttonDone.setOnClickListener { applyTeacher() }

		setResult(RESULT_CANCEL)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean
	{
		menuInflater.inflate(R.menu.activity_teachers, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId)
	{
		R.id.item_teacher_remove -> showTeacherRemovalDialog().let { true }
		else -> super.onOptionsItemSelected(item)
	}

	@SuppressLint("InflateParams")
	private fun editTeacherColor()
	{
		var dialog: AlertDialog? = null

		val adapter = TeacherColorsAdapter(this) {
			newColor = it
			imageTeacherColor.setColorFilter(it)
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

		val intent = Intent()
		intent.putExtra(KEY_INDEX, index)
		intent.putExtra(KEY_TEACHER, teacher)
		setResult(RESULT_OK, intent)
		finish()
	}

	private fun checkName(): Boolean
	{
		val valid = !editTextName.text.isNullOrBlank()
		if(!valid) editTextLayoutName.error = getString(R.string.error_teacher_name_blank)
		else editTextLayoutName.isErrorEnabled = false
		return valid
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

	private fun removeTeacher()
	{
		val intent = Intent()
		intent.putExtra(KEY_INDEX, index)
		intent.putExtra(KEY_TEACHER, teacher)
		setResult(RESULT_REMOVE, intent)
		finish()
	}
}