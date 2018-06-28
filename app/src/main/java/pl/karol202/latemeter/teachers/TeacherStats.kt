package pl.karol202.latemeter.teachers

import android.content.Context
import pl.karol202.latemeter.R

enum class TeacherStats(val title: Int, val valueSupplier: (Context, Teacher) -> String)
{
	ACCUMULATED_LATENESS(R.string.teacher_stats_accumulated_lateness, { context, teacher ->
		teacher.getSumOfTardies().format(context, true)
	}),
	AVERAGE_LATENESS(R.string.teacher_stats_average_lateness, { context, teacher ->
		teacher.getAverageOfTardies()?.format(context, true) ?: context.getString(R.string.timespan_null)
	})
}