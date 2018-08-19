package pl.karol202.latemeter.teachers

import androidx.annotation.ColorInt
import pl.karol202.latemeter.lateness.Tardy
import pl.karol202.latemeter.utils.Date
import pl.karol202.latemeter.utils.Time
import pl.karol202.latemeter.utils.TimeSpan
import pl.karol202.latemeter.utils.div
import java.io.Serializable
import java.util.*

class Teacher(
		var name: String,
		@ColorInt var color: Int
) : Serializable
{
	companion object
	{
		fun createUniqueID() = UUID.randomUUID().toString()
	}

	private val tardies = mutableListOf<Tardy>()

	val tardiesAmount: Int
		get() = tardies.size
	val sumOfTardies
		get() = calculateSumOfTardies()
	val averageOfTardies
		get() = calculateAverageOfTardies()

	fun addTardy(tardy: Tardy)
	{
		tardies.add(tardy)
	}

	fun getTardy(index: Int) = tardies[index]

	fun findTardy(date: Date, scheduleHourStart: Time) =
			tardies.find { it.date == date && it.expectedTime == scheduleHourStart }

	private fun calculateSumOfTardies() = tardies.fold(TimeSpan.zero) { sum, tardy -> sum + tardy.duration }

	private fun calculateAverageOfTardies() = sumOfTardies / tardiesAmount.takeUnless { it == 0 }
}