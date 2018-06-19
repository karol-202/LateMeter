package pl.karol202.latemeter.teachers

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pl.karol202.latemeter.R
import pl.karol202.latemeter.main.Screen
import pl.karol202.latemeter.utils.findView

class TeachersScreen : Screen()
{
	companion object
	{
		private const val KEY_SORTING = "sorting"

		private const val REQUEST_EDIT_TEACHER = 1
		private const val REQUEST_ADD_TEACHER = 2
	}

	private val teachers by lazy { Teachers.loadTeachers(requireContext()) }
	private val adapter by lazy { TeachersAdapter(requireContext(), teachers, sorting) { i, t -> editTeacher(i, t) } }

	private var sorting: TeachersAdapter.Sorting = TeachersAdapter.Sorting.BY_NAME_ASCENDING
		set(value)
		{
			field = value
			adapter.sorting = value
		}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		restoreState(savedInstanceState)
		val view = inflater.inflate(R.layout.screen_teachers, container, false)

		val recyclerTeachers = view.findView<RecyclerView>(R.id.recycler_teachers)
		recyclerTeachers.layoutManager = LinearLayoutManager(requireContext())
		recyclerTeachers.adapter = adapter

		val buttonAddTeacher = view.findView<FloatingActionButton>(R.id.button_add_teacher)
		buttonAddTeacher.setOnClickListener { addTeacher() }

		setHasOptionsMenu(true)
		return view
	}

	private fun restoreState(state: Bundle?)
	{
		if(state == null) return
		sorting = state.getSerializable(KEY_SORTING) as TeachersAdapter.Sorting
	}

	override fun onSaveInstanceState(outState: Bundle)
	{
		outState.putSerializable(KEY_SORTING, sorting)
		super.onSaveInstanceState(outState)
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
	{
		inflater?.inflate(R.menu.screen_teachers, menu ?: return)
		return super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId)
	{
		R.id.item_teachers_sort -> {
			showSortingDialog()
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	private fun showSortingDialog()
	{
		var sorting = adapter.sorting
		val builder = AlertDialog.Builder(requireContext())
		builder.setTitle(R.string.dialog_sorting)
		builder.setSingleChoiceItems(getSortingMethodsArray(), sorting.ordinal) { _, which ->
			sorting = TeachersAdapter.Sorting.values()[which]
		}
		builder.setPositiveButton(R.string.button_sort) { _, _ ->
			this.sorting = sorting
			adapter.notifyDataSetChanged()
		}
		builder.setNegativeButton(R.string.button_cancel, null)
		builder.show()
	}

	private fun getSortingMethodsArray() =
			TeachersAdapter.Sorting.values().map { requireContext().getString(it.text) }.toTypedArray()

	private fun addTeacher()
	{
		val intent = Intent(context, TeacherActivity::class.java)
		startActivityForResult(intent, REQUEST_ADD_TEACHER)
	}

	private fun editTeacher(id: String, teacher: Teacher)
	{
		val intent = Intent(context, TeacherActivity::class.java)
		intent.putExtra(TeacherActivity.KEY_ID, id)
		intent.putExtra(TeacherActivity.KEY_TEACHER, teacher)
		startActivityForResult(intent, REQUEST_EDIT_TEACHER)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		val id = data?.getStringExtra(TeacherActivity.KEY_ID)
		val teacher = data?.getSerializableExtra(TeacherActivity.KEY_TEACHER) as? Teacher ?: return //In case of null 'data' or no teacher (cancel)
		when
		{
			requestCode == REQUEST_ADD_TEACHER && resultCode == TeacherActivity.RESULT_OK -> teachers.addTeacher(teacher)
			requestCode == REQUEST_EDIT_TEACHER && resultCode == TeacherActivity.RESULT_OK -> teachers[id ?: return] = teacher
			requestCode == REQUEST_EDIT_TEACHER && resultCode == TeacherActivity.RESULT_REMOVE -> teachers.removeTeacher(id ?: return)
		}
		teachers.saveTeachers(requireContext())
		adapter.notifyDataSetChanged()
	}
}