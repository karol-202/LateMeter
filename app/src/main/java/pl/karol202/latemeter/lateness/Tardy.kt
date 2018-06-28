package pl.karol202.latemeter.lateness

import pl.karol202.latemeter.utils.Date
import pl.karol202.latemeter.utils.Time
import pl.karol202.latemeter.utils.TimeSpan
import java.io.Serializable

data class Tardy(
		val date: Date,
		val expectedTime: Time,
		val duration: TimeSpan
) : Serializable