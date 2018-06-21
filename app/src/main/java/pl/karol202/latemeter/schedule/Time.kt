package pl.karol202.latemeter.schedule

class Time private constructor(
		val hour: Int,
		val minute: Int
) : Comparable<Time>
{
	companion object
	{
		val zero = createTime(0, 0) ?: throw Exception("Could not create zero time")

		fun fromMinutes(minutes: Int) = createTime(minutes / 60, minutes % 60)

		fun createTime(hour: Int, minute: Int) = if(hour in 0..23 && minute in 0..59) Time(hour, minute) else null
	}

	init
	{
		if(hour !in 0..23 || minute !in 0..59) throw Exception("Invalid time: $hour:$minute")
	}

	operator fun plus(other: Time): Time?
	{
		val minutes = minute + other.minute
		val hours = hour + other.hour + minutes / 60
		return createTime(hours, minutes % 60)
	}

	override fun compareTo(other: Time) = when
	{
		hour > other.hour -> 1
		hour < other.hour -> -1
		else -> when {
			minute > other.minute -> 1
			minute < other.minute -> -1
			else -> 0
		}
	}

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(javaClass != other?.javaClass) return false

		other as Time

		if(hour != other.hour) return false
		if(minute != other.minute) return false

		return true
	}

	override fun hashCode(): Int
	{
		var result = hour
		result = 31 * result + minute
		return result
	}
}