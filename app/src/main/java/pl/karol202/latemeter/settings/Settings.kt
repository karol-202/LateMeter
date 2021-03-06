package pl.karol202.latemeter.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import pl.karol202.latemeter.R
import pl.karol202.latemeter.utils.string

class Settings<T> private constructor(
		@StringRes val keyRes: Int,
		@StringRes val defaultValueRes: Int
)
{
	companion object
	{
		val DEFAULT_SCHEDULE_HOUR_DURATION = Settings<Int>(R.string.setting_schedule_hour_duration_key, R.string.default_schedule_hour_duration)

		inline fun <reified T> getSetting(context: Context, setting: Settings<T>): T
		{
			val preferences = PreferenceManager.getDefaultSharedPreferences(context)
			val key = context.string(setting.keyRes)
			val defaultValueString = context.string(setting.defaultValueRes)
			return when(T::class)
			{
				String::class -> getStringSetting(preferences, key, defaultValueString) as T
				Int::class -> getIntSetting(preferences, key, defaultValueString) as T
				else -> throw Exception("Unsupported type of setting $key")
			}
		}

		fun getStringSetting(preferences: SharedPreferences, key: String, defaultValueString: String): String =
				preferences.getString(key, defaultValueString)

		fun getIntSetting(preferences: SharedPreferences, key: String, defaultValueString: String): Int =
				preferences.getString(key, defaultValueString).toInt()
	}
}