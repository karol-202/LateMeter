package pl.karol202.latemeter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TeachersScreen : Fragment()
{
	private val REQUEST_EDIT_TEACHER = 1
	private val REQUEST_ADD_TEACHER = 2

	private val teachers by lazy { Teachers.loadTeachers(requireContext()) }

	private val adapter by lazy { TeachersAdapter(requireContext(), teachers) { i, t -> editTeacher(i, t) } }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.screen_teachers, container, false)

		val recyclerTeachers = view.findViewById<RecyclerView>(R.id.recycler_teachers)
		recyclerTeachers.layoutManager = LinearLayoutManager(requireContext())
		recyclerTeachers.adapter = adapter

		val buttonAddTeacher = view.findViewById<FloatingActionButton>(R.id.button_add_teacher)
		buttonAddTeacher.setOnClickListener { addTeacher() }

		return view
	}

	private fun addTeacher()
	{
		val intent = Intent(context, TeacherActivity::class.java)
		startActivityForResult(intent, REQUEST_ADD_TEACHER)
	}

	private fun editTeacher(index: Int, teacher: Teacher)
	{
		val intent = Intent(context, TeacherActivity::class.java)
		intent.putExtra(TeacherActivity.KEY_INDEX, index)
		intent.putExtra(TeacherActivity.KEY_TEACHER, teacher)
		startActivityForResult(intent, REQUEST_EDIT_TEACHER)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		val index = data?.getIntExtra(TeacherActivity.KEY_INDEX, -1) ?: return
		val teacher = data.getSerializableExtra(TeacherActivity.KEY_TEACHER) as? Teacher ?: return
		when
		{
			requestCode == REQUEST_ADD_TEACHER && resultCode == TeacherActivity.RESULT_OK -> teachers.addTeacher(teacher)
			requestCode == REQUEST_EDIT_TEACHER && resultCode == TeacherActivity.RESULT_OK -> teachers[index] = teacher
			requestCode == REQUEST_EDIT_TEACHER && resultCode == TeacherActivity.RESULT_REMOVE -> teachers.removeTeacher(index)
		}
		teachers.saveTeachers(requireContext())
		adapter.notifyDataSetChanged()
	}
}