package pl.karol202.latemeter.utils

import android.content.Context
import java.io.Serializable

abstract class TimeBase<T : TimeBase<T>> protected constructor(
		val hour: Int,
		val minute: Int,
		val second: Int
) : Serializable, Comparable<T>
{
	val time = hour * 3600 + minute * 60 + second

	abstract fun format(context: Context, seconds: Boolean = false): String

	override fun compareTo(other: T) = when
	{
		hour > other.hour -> 1
		hour < other.hour -> -1
		else -> when {
			minute > other.minute -> 1
			minute < other.minute -> -1
			else -> when {
				second > other.second -> 1
				second < other.second -> -1
				else -> 0
			}
		}
	}

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(javaClass != other?.javaClass) return false

		other as T?

		if(hour != other.hour) return false
		if(minute != other.minute) return false
		if(second != other.second) return false

		return true
	}

	override fun hashCode(): Int
	{
		var result = hour
		result = 31 * result + minute
		result = 31 * result + second
		return result
	}
}