package pl.karol202.latemeter.utils

import android.content.Context
import pl.karol202.latemeter.R
import kotlin.math.roundToInt

class TimeSpan private constructor(
		hour: Int,
		minute: Int,
		second: Int
) : TimeBase<TimeSpan>(hour, minute, second)
{
	companion object
	{
		val zero = createTimeSpan(0, 0, 0)!!

		fun fromMinutes(minutes: Int) =
				createTimeSpan(minutes / 60, minutes % 60, 0)!!

		fun fromSeconds(time: Int): TimeSpan
		{
			val seconds = time % 60
			val minutes = (time / 60) % 60
			val hours = (time / 3600)

			return createTimeSpan(hours, minutes, seconds)!!
		}

		private fun createTimeSpan(hour: Int, minute: Int, second: Int) =
				if(minute in 0..59 && second in 0..59) TimeSpan(hour, minute, second) else null

		fun formatNullTimeSpan(context: Context): String = context.getString(R.string.timespan_null)
	}

	init
	{
		if(minute !in 0..59 || second !in 0..59) throw Exception("Invalid time span: $hour:$minute:$second")
	}

	operator fun plus(other: TimeSpan) = fromSeconds(time + other.time)

	operator fun minus(other: TimeSpan) = fromSeconds(time - other.time)

	operator fun times(factor: Int) = this * factor.toFloat()

	operator fun times(factor: Float) = fromSeconds((time * factor).roundToInt())

	operator fun div(factor: Int) = this / factor.toFloat()

	operator fun div(factor: Float) = fromSeconds((time / factor).roundToInt())

	override fun format(context: Context, seconds: Boolean): String =
			if(seconds) context.getString(R.string.timespan_format_long, hour, minute, second)
			else context.getString(R.string.timespan_format_short, hour, minute)
}