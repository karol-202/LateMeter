package pl.karol202.latemeter.teachers

import android.content.Context
import com.google.gson.Gson
import pl.karol202.latemeter.R
import pl.karol202.latemeter.utils.TimeSpan
import pl.karol202.latemeter.utils.preferences
import java.util.*

class Teachers private constructor(context: Context)
{
	data class TeacherWithId(val id: String, val teacher: Teacher)

	enum class Sorting(val text: Int, val comparator: (Teacher, Teacher) -> Int)
	{
		BY_NAME_ASCENDING(R.string.teachers_sorting_by_name_ascending, { a, b -> a.name.compareTo(b.name, true) }),
		BY_NAME_DESCENDING(R.string.teachers_sorting_by_name_descending, { a, b -> b.name.compareTo(a.name, true) }),
		BY_AVERAGE_LATENESS_ASCENDING(R.string.teachers_sorting_by_average_lateness_ascending, { a, b ->
			(a.averageOfTardies ?: TimeSpan.zero).compareTo(b.averageOfTardies ?: TimeSpan.zero)
		}),
		BY_AVERAGE_LATENESS_DESCENDING(R.string.teachers_sorting_by_average_lateness_descending, { a, b ->
			(b.averageOfTardies ?: TimeSpan.zero).compareTo(a.averageOfTardies ?: TimeSpan.zero)
		})
	}

	companion object
	{
		private const val KEY_LENGTH = "teachers_length"
		private const val KEY_TEACHER_ID = "teacher_%d_id"
		private const val KEY_TEACHER_CONTENT = "teacher_%d_content"

		fun loadTeachers(context: Context) = Teachers(context)
	}

	private val teachers = mutableMapOf<String, Teacher>()

	val size: Int
		get() = teachers.size

	init
	{
	    loadTeachers(context)
	}

	fun sortedBy(sorting: Sorting) =
			teachers.entries.map { TeacherWithId(it.key, it.value) }.sortedWith(createComparator(sorting))

	private fun createComparator(sorting: Sorting) =
			Comparator<TeacherWithId> { a, b -> sorting.comparator(a.teacher, b.teacher) }

	operator fun get(id: String) = teachers[id]

	operator fun set(id: String, teacher: Teacher)
	{
		teachers[id] = teacher
	}

	operator fun contains(id: String) = id in teachers

	fun addTeacher(teacher: Teacher): String
	{
		val id = Teacher.createUniqueID()
		teachers[id] = teacher
		return id
	}

	fun removeTeacher(id: String)
	{
		teachers.remove(id)
	}

	private fun loadTeachers(context: Context)
	{
		teachers.clear()

		val gson = Gson()
		val prefs = context.preferences()
		val length = prefs.getInt(KEY_LENGTH, 0)
		for(i in 0 until length)
		{
			val id = prefs.getString(String.format(KEY_TEACHER_ID, i), null) ?: continue
			val content = prefs.getString(String.format(KEY_TEACHER_CONTENT, i), null) ?: continue
			teachers[id] = gson.fromJson(content, Teacher::class.java)
		}
	}

	fun saveTeachers(context: Context)
	{
		val gson = Gson()
		val editor = context.preferences().edit()
		editor.putInt(KEY_LENGTH, size)
		for(i in 0 until size)
		{
			val id = teachers.keys.elementAt(i)
			editor.putString(String.format(KEY_TEACHER_ID, i), id)
			editor.putString(String.format(KEY_TEACHER_CONTENT, i), gson.toJson(teachers[id]))
		}
		editor.apply()
	}
}