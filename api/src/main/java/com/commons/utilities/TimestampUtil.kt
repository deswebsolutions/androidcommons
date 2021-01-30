package com.commons.utilities

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Provides the current timestamp in the format "yyyy-MM-dd hh:mm:ss.SSS"
 *
 * @return current timestamp in the format "yyyy-MM-dd hh:mm:ss.SSS"
 */
val currentTimestampStringFormat: String
    get() {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        return sdf.format(currentTimestamp)
    }


/**
 * Returns the current timestamp
 *
 * @return current timestamp
 */
val currentTimestamp: String
    @SuppressLint("SimpleDateFormat")
    get() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDateandTime = sdf.format(Date())
        return currentDateandTime
    }
val currentYear : String
    @SuppressLint("SimpleDateFormat")
    get() {
        val sdf = SimpleDateFormat("yyyy")
        return sdf.format(Date())
    }
val currentDate: String
    @SuppressLint("SimpleDateFormat")
    get() {
        val sdf = SimpleDateFormat("yyyy MM dd HH mm")
        val currentDateandTime = sdf.format(Date())
        return currentDateandTime
    }

val currentTimeMillis : Long
    get() {
        return System.currentTimeMillis()
    }
/**
 * Formats the given timestamp object in the format HH:mm
 *
 * @param timestamp a timestamp object
 * @return the given timestamp as a String object with format HH:mm - return empty if timestamp is null
 */
@SuppressLint("SimpleDateFormat")
fun formatTimestamp(timestamp: Timestamp?, format: String): String {
    return if (null != timestamp) {
        val sdf = SimpleDateFormat(format)
        sdf.format(timestamp)
    } else {
        ""
    }
}
@SuppressLint("SimpleDateFormat")
fun getDateTime(s: String): String {
    //val dateInString = TimestampUtil.date_con(s)
    val date = getCurrentDateTime(s)
    val dateInString = date.toString("hh:mm a")
    return dateInString
}
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

@SuppressLint("SimpleDateFormat")
fun getCurrentDateTime(date : String): Date {
    try {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        cal.time = sdf.parse(date)// all done
        return cal.time
    }catch (e : Exception){
        e.printStackTrace()
    }
    return Date()
}
@SuppressLint("SimpleDateFormat")
fun convertDateToLong(date : String) : Long{
    val f = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val d = f.parse(date)
    return d.time

}
fun convertLongToDate(date: String): String{
    val millisecond = date.toLong()
    // or you already have long value of date, use this instead of milliseconds variable.
    // or you already have long value of date, use this instead of milliseconds variable.
    val dateString: String =
        DateFormat.format("yyyy-MM-dd hh:mm a", Date(millisecond)).toString()
    return dateString
}
fun convertLongDate(date: Long): String{
    val millisecond = date
    // or you already have long value of date, use this instead of milliseconds variable.
    // or you already have long value of date, use this instead of milliseconds variable.
    val dateString: String =
        DateFormat.format("yyyy-MM-dd", Date(millisecond)).toString()
    return dateString
}
fun convertLongToDateTime(date: String): String{
    val millisecond = date.toLong()
    // or you already have long value of date, use this instead of milliseconds variable.
    // or you already have long value of date, use this instead of milliseconds variable.
    val dateString: String =
        DateFormat.format("yyyy-MM-dd HH:mm:ss", Date(millisecond)).toString()
    return dateString
}
/**
 * Given a String object with the format "yyyy-MM-dd hh:mm:ss.SSS", returns a timestamp object
 *
 * @param timestamp a String object representing a timestamp in the format "yyyy-MM-dd hh:mm:ss.SSS"
 * @return a timestamp object
 */
fun getTimestampFromString(timestamp: String): Timestamp? {
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val parsedDate = dateFormat.parse(timestamp)
        return Timestamp(parsedDate.time)
    } catch (e: ParseException) {
        return null
    }

}