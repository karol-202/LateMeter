package pl.karol202.latemeter

class Teachers
{
	private val list = mutableListOf<Teacher>()

	val size: Int
		get() = list.size

	operator fun get(index: Int) = list[index]

	companion object
	{
		fun loadTeachers()
		{

		}
	}
}