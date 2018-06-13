package pl.karol202.latemeter

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
	private val teachers by lazy { Teachers.loadTeachers() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.screen_teachers, container, false)

		val recyclerTeachers = view.findViewById<RecyclerView>(R.id.recycler_teachers)
		recyclerTeachers.layoutManager = LinearLayoutManager(context)
		recyclerTeachers.adapter = TeachersAdapter(context, teachers, )

		val buttonAddTeacher = view.findViewById<FloatingActionButton>(R.id.button_add_teacher)
		buttonAddTeacher.setOnClickListener { teachers.addTeachers(it) }

		return view
	}
}