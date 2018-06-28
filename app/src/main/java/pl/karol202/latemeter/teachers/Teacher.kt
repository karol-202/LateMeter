package pl.karol202.latemeter.teachers

import pl.karol202.latemeter.lateness.Tardy
import pl.karol202.latemeter.utils.Date
import pl.karol202.latemeter.utils.Time
import pl.karol202.latemeter.utils.TimeSpan
import pl.karol202.latemeter.utils.div
import java.io.Serializable
import java.util.*

class Teacher(
		var name: String,
		var color: Int
) : Serializable
{
	companion object
	{
		fun createUniqueID() = UUID.randomUUID().toString()
	}

	private val tardies = mutableListOf<Tardy>()

	val tardiesAmount: Int
		get() = tardies.size

	operator fun get(index: Int) = tardies[index]

	fun addTardy(tardy: Tardy)
	{
		tardies.add(tardy)
	}

	fun findTardy(date: Date, scheduleHourStart: Time) =
			tardies.find { it.date == date && it.expectedTime == scheduleHourStart }

	fun getSumOfTardies() = tardies.fold(TimeSpan.zero) { sum, tardy -> sum + tardy.duration }

	fun getAverageOfTardies() = getSumOfTardies() / tardiesAmount.takeUnless { it == 0 }
}