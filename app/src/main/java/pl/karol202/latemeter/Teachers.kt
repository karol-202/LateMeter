package pl.karol202.latemeter

class Teachers private constructor()
{
	private val list = mutableListOf<Teacher>()

	val size: Int
		get() = list.size

	init
	{
	    loadTeachers()
	}

	operator fun get(index: Int) = list[index]

	fun addTeacher(teacher: Teacher)
	{
		list.add(teacher)
		saveTeachers()
	}

	fun removeTeacher(teacher: Teacher)
	{
		list.remove(teacher)
		saveTeachers()
	}

	private fun loadTeachers()
	{

	}

	fun saveTeachers()
	{

	}

	companion object
	{
		fun loadTeachers() = Teachers()
	}
}