package com.commons.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

fun Context.getSharedAccount(key: String): MutableSet<String>?{
    val mContext: Context = createPackageContext("com.callmode.android",
        Context.CONTEXT_IGNORE_SECURITY
    )

    val sharedpreferences: SharedPreferences = mContext.getSharedPreferences(key, Context.MODE_PRIVATE)
    val set: MutableSet<String>? = sharedpreferences.getStringSet(key, null)
    return set
}
fun Context.setBoolSession(key: String = "", value: Boolean = false){
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    val editor = sharedpreferences.edit()
    editor.putBoolean(key, value)
    editor.apply()
}

fun Context.getBoolSession(key: String = ""): Boolean? {
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    return sharedpreferences.getBoolean(key, false)
}

fun Context.setStringSession(key: String = "", value: String = "") {
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    val editor = sharedpreferences.edit()
    editor.putString(key, value)
    editor.apply()
}
fun Context.getStringSession(key: String = "") : String?{
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    return sharedpreferences.getString(key, "")
}

fun Context.setLongSession(key: String, value: Long = 0) {
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    val editor = sharedpreferences.edit()
    editor.putLong(key, value)
    editor.apply()
}
fun Context.getIntSession(key: String = "", default: Int = 0) : Int {
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    return sharedpreferences.getInt(key, default)
}fun Context.setIntSession(key: String, value: Int = 0) {
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    val editor = sharedpreferences.edit()
    editor.putInt(key, value)
    editor.apply()
}
fun Context.getLongSession(key: String = "") : Long {
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    return sharedpreferences.getLong(key, 0)
}


@SuppressLint("CommitPrefEdits")
fun Context.setStringArraySession(key: String, data: String){
    val arraylist = ArrayList<String>()
    arraylist.add(data)
    if(getStringArraySession(key) != null){
        for (getdata in getStringArraySession(key)!!){
            arraylist.add(getdata)
        }
    }
    setArrayData(key, arraylist)
}
fun Context.removeStringArraySession(key: String, data: String){
    val list = getStringArraySession(key)
    if(list?.isNotEmpty()!!){
        if(list.contains(data))
            list.remove(data)
        setArrayData(key, list.toCollection(ArrayList()))
    }

}
fun Context.removeAllStringArraySessions(key: String){
    if(getStringArraySession(key) != null) {
        val list = getStringArraySession(key)
        list!!.removeAll(list)
    }
}
fun Context.setArrayData(key: String, arraylist: ArrayList<String>) {

    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    val mEdit1 = sharedpreferences.edit()
    val set: MutableSet<String> = HashSet()
    set.addAll(arraylist)
    mEdit1.putStringSet(key, set)
    mEdit1.apply()
}
fun Context.getStringArraySession(key: String) : MutableSet<String>?{
    val sharedpreferences: SharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
    val set: MutableSet<String>? = sharedpreferences.getStringSet(key, null)
    return set
}