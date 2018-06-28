package pl.karol202.latemeter.utils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.IdRes

fun <V : View> Activity.findView(@IdRes id: Int) = this.findViewById<V>(id) ?: throw Exception("View not found")

fun <V : View> View?.findView(@IdRes id: Int) = (this ?: throw Exception("View is null")).findViewById<V>(id) ?: throw Exception("View not found")

operator fun TimeSpan.div(factor: Int?) = factor?.let { this / it }

fun TimeSpan?.format(context: Context, seconds: Boolean) = this?.format(context, seconds) ?: TimeSpan.formatNullTimeSpan(context)