package pl.karol202.latemeter.utils

import android.content.Context
import android.text.format.DateFormat
import java.util.*
import java.io.Serializable

data class Date(
		val year: Int,
		val month: Int,
		val day: Int
) : Serializable, Comparable<Date>
{
	fun format(context: Context): String
	{
		val format = DateFormat.getDateFormat(context)

		val calendar = GregorianCalendar.getInstance()
		calendar[Calendar.YEAR] = year
		calendar[Calendar.MONTH] = month
		calendar[Calendar.DAY_OF_MONTH] = day
		return format.format(calendar.time)
	}

	override fun compareTo(other: Date) = when
	{
		year > other.year -> 1
		year < other.year -> -1
		else -> when {
			month > other.month -> 1
			month < other.month -> -1
			else -> when {
				day > other.day -> 1
				day < other.day -> -1
				else -> 0
			}
		}
	}
}