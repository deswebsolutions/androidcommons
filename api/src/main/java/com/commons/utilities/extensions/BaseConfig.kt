package com.commons.utilities.extensions

import android.content.Context
import android.provider.Settings
import android.text.format.DateFormat
import com.commons.R
import java.text.SimpleDateFormat

open class BaseConfig(val context: Context) {
    protected val prefs = context.getSharedPrefs()
    companion object {
        fun newInstance(context: Context) = BaseConfig(context)
    }

    var textColor: Int
        get() = prefs.getInt(TEXT_COLOR, context.resources.getColor(R.color.default_text_color))
        set(textColor) = prefs.edit().putInt(TEXT_COLOR, textColor).apply()

    var backgroundColor: Int
        get() = prefs.getInt(BACKGROUND_COLOR, context.resources.getColor(R.color.default_background_color))
        set(backgroundColor) = prefs.edit().putInt(BACKGROUND_COLOR, backgroundColor).apply()


    var use24HourFormat: Boolean
        get() = prefs.getBoolean(USE_24_HOUR_FORMAT, DateFormat.is24HourFormat(context))
        set(use24HourFormat) = prefs.edit().putBoolean(USE_24_HOUR_FORMAT, use24HourFormat).apply()

    var dateFormat: String
        get() = prefs.getString(Settings.System.DATE_FORMAT, getDefaultDateFormat())!!
        set(dateFormat) = prefs.edit().putString(Settings.System.DATE_FORMAT, dateFormat).apply()


    private fun getDefaultDateFormat(): String {
        val format = DateFormat.getDateFormat(context)
        val pattern = (format as SimpleDateFormat).toLocalizedPattern()
        return when (pattern.toLowerCase().replace(" ", "")) {
            "dd/mm/y" -> DATE_FORMAT_TWO
            "mm/dd/y" -> DATE_FORMAT_THREE
            "y-mm-dd" -> DATE_FORMAT_FOUR
            "dmmmmy" -> DATE_FORMAT_ONE
            "mmmmdy" -> DATE_FORMAT_SIX
            "mm-dd-y" -> DATE_FORMAT_SEVEN
            "dd-mm-y" -> DATE_FORMAT_EIGHT
            else -> DATE_FORMAT_FIVE
        }
    }
}