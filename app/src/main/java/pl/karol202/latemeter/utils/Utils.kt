package pl.karol202.latemeter.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import java.io.Serializable

// Activity

fun <V : View> Activity.view(@IdRes id: Int) = lazy { this.findView<V>(id) }

fun <V : View> Activity.findView(@IdRes id: Int) = this.findViewById<V>(id) ?: throw Exception("View not found")

// View

fun <V : View> View.findView(@IdRes id: Int) = this.findViewById<V>(id) ?: throw Exception("View not found")

// Context

fun Context.preferences() = PreferenceManager.getDefaultSharedPreferences(this)!!

fun Context.string(@StringRes string: Int) = this.getString(string)!!

fun Context.string(@StringRes string: Int, vararg args: Any?) = this.getString(string, *args)!!

fun Context.color(@ColorRes color: Int) = ResourcesCompat.getColor(this.resources, color, null)

fun Context.drawable(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

// Intent

fun Intent?.getString(key: String): String? = this?.getStringExtra(key)

@Suppress("unchecked_cast") fun <T : Serializable> Intent?.getSerializable(key: String) = this?.getSerializableExtra(key) as? T

// Other

operator fun TimeSpan.div(factor: Int?) = factor?.let { this / it }

fun TimeSpan?.format(context: Context, seconds: Boolean) = this?.format(context, seconds) ?: TimeSpan.formatNullTimeSpan(context)