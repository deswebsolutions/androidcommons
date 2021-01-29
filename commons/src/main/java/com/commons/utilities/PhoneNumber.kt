package com.commons.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri

fun filterPhoneNumber(ccode:String,phone : String) : String{
    try {
        val filter_phone = phone.replace("[^0-9]","")
        return if(filter_phone.length > 11 ){
            filter_phone
        }else{
            var phone_number: String =if (filter_phone.length == 9) {
                filter_phone
            } else {
                filter_phone.substring(1, 10)
            }

            "$ccode$phone_number"
        }
    }catch (e : Exception){
        e.printStackTrace()
    }
    return ""
}
fun containsIllegalCharacters(displayName: String): Boolean {
    val nameLength = displayName.length
    for (i in 0 until nameLength) {
        val hs = displayName[i]
        if (hs.toInt() in 0xd800..0xdbff) {
            val ls = displayName[i + 1]
            val uc = (hs.toInt() - 0xd800) * 0x400 + (ls.toInt() - 0xdc00) + 0x10000
            if (uc in 0x1d000..0x1f77f) {
                return true
            }
        } else if (Character.isHighSurrogate(hs)) {
            val ls = displayName[i + 1]
            if (ls.toInt() == 0x20e3) {
                return true
            }
        } else { // non surrogate
            if (hs.toInt() in 0x2100..0x27ff) {
                return true
            } else if (hs.toInt() in 0x2B05..0x2b07) {
                return true
            } else if (hs.toInt() in 0x2934..0x2935) {
                return true
            } else if (hs.toInt() in 0x3297..0x3299) {
                return true
            } else if (hs.toInt() == 0xa9 || hs.toInt() == 0xae || hs.toInt() == 0x303d || hs.toInt() == 0x3030 || hs.toInt() == 0x2b55 || hs.toInt() == 0x2b1c || hs.toInt() == 0x2b1b || hs.toInt() == 0x2b50) {
                return true
            }
        }
    }
    return false
}

fun get_string(text :String) : String{
    return  text.replace(("[^\\w ]").toRegex(), "")
}

fun extractNumberFromString(source: String): String? {
    val result = StringBuilder(100)
    for (ch in source.toCharArray()) {
        if (ch in '0'..'9') {
            result.append(ch)
        }
    }
    return result.toString()
}
fun searchNumber(phone: String) : String{
    val data = extractNumberFromString(phone)!!
    return if(data.length > 9){
        data.substring((data.length-9),data.length)
    }else{
        data
    }
}

/**
 * Opens a new sms with a given number filled in
 *
 * @param activity
 * @param number
 */
fun Context.openSmsWithNumber(number: String?) {
    val uri = Uri.parse(String.format("smsto:%s", number))
    val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
    startActivity(smsIntent)
}