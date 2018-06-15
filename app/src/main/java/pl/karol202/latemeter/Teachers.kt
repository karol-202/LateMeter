package pl.karol202.latemeter

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import java.io.Serializable

data class Teacher(var name: String, var color: Int) : Serializable

class Teachers private constructor(context: Context)
{
	private val KEY_LENGTH = "teachers_length"
	private val KEY_TEACHER_N = "teachers_"

	private val list = mutableListOf<Teacher>()

	val size: Int
		get() = list.size

	init
	{
	    loadTeachers(context)
	}

	operator fun get(index: Int) = list[index]

	operator fun set(index: Int, teacher: Teacher)
	{
		list[index] = teacher
	}

	fun addTeacher(teacher: Teacher)
	{
		list.add(teacher)
	}

	fun removeTeacher(index: Int)
	{
		list.removeAt(index)
	}

	private fun loadTeachers(context: Context)
	{
		list.clear()

		val gson = Gson()
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		val length = prefs.getInt(KEY_LENGTH, 0)
		for(i in 0 until length)
			list.add(gson.fromJson(prefs.getString(KEY_TEACHER_N + i, ""), Teacher::class.java))
	}

	fun saveTeachers(context: Context)
	{
		val gson = Gson()
		val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
		editor.putInt(KEY_LENGTH, size)
		for(i in 0 until size)
			editor.putString(KEY_TEACHER_N + i, gson.toJson(list[i]))
		editor.apply()
	}

	companion object
	{
		fun loadTeachers(context: Context) = Teachers(context)
	}
}