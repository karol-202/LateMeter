package pl.karol202.latemeter.teachers

import android.content.Context
import pl.karol202.latemeter.R
import pl.karol202.latemeter.utils.format

enum class TeacherStats(
		val title: Int,
		val valueSupplier: (Context, Teacher) -> String
)
{
	ACCUMULATED_LATENESS(R.string.teacher_stats_accumulated_lateness, { context, teacher ->
		teacher.sumOfTardies.format(context, true)
	}),
	AVERAGE_LATENESS(R.string.teacher_stats_average_lateness, { context, teacher ->
		teacher.averageOfTardies.format(context, true)
	})
}