package pl.karol202.latemeter.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class Time private constructor(
		hour: Int,
		minute: Int,
		second: Int
) : TimeBase<Time>(hour, minute, second)
{
	companion object
	{
		val zero = createTime(0, 0, 0) ?: throw Exception()

		fun fromSeconds(time: Int): Time?
		{
			val seconds = time % 60
			val minutes = (time / 60) % 60
			val hours = (time / 3600)

			return createTime(hours, minutes, seconds)
		}

		fun createTime(hour: Int, minute: Int, second: Int) =
				if(hour in 0..23 && minute in 0..59 && second in 0..59) Time(hour, minute, second) else null
	}

	init
	{
		if(hour !in 0..23 || minute !in 0..59 || second !in 0..59) throw Exception("Invalid time: $hour:$minute:$second")
	}

	operator fun plus(other: TimeSpan) = fromSeconds(time + other.time)

	operator fun minus(other: TimeSpan) = fromSeconds(time - other.time)

	operator fun minus(other: Time) = TimeSpan.fromSeconds(time - other.time)

	override fun format(context: Context, seconds: Boolean): String
	{
		val style = if(seconds) SimpleDateFormat.MEDIUM else SimpleDateFormat.SHORT
		val format = SimpleDateFormat.getTimeInstance(style)

		val calendar = GregorianCalendar.getInstance()
		calendar[Calendar.HOUR_OF_DAY] = hour
		calendar[Calendar.MINUTE] = minute
		calendar[Calendar.SECOND] = second
		return format.format(calendar.time)
	}
}