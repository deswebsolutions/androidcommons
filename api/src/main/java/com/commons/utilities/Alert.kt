package com.commons.utilities

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.commons.R
import com.commons.utilities.extensions.DARK_THEME
import com.commons.utilities.extensions.verifyAvailableNetwork

fun Activity.alertOkCancel(color: Int,message: String, callback: (confirmed: Boolean) -> Unit){
    // set message of alert dialog
    runOnUiThread {
        val wrapper: Context = ContextThemeWrapper(this, R.style.alertTheme)
        val dialogBuilder = AlertDialog.Builder(wrapper)
        dialogBuilder.setMessage(message)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                try {
                    dialog.cancel()
                    callback(true)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, id ->
                dialog.cancel()
                callback(false)
            }

        val alert = dialogBuilder.create()
        alert.alertButton(color)
        alert.show()
    }

}

fun AlertDialog.alertButton(color : Int) {
    setOnShowListener {
        if(context.getBoolSession(DARK_THEME)!!) {
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.white))
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, color))
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, color))
        }

    }
}

fun Activity.internetAlert(color: Int,callback: (Boolean) -> Unit){
    if(verifyAvailableNetwork()){
        callback(true)
    }else{
        alertOk(color,getString(R.string.something_went_wrong)){
            callback(it)
        }
    }
}
fun Activity.alertOk(color: Int,message: String){
    // set message of alert dialog
    runOnUiThread{
        val wrapper: Context = ContextThemeWrapper(this, R.style.alertTheme)
        val dialogBuilder = AlertDialog.Builder(wrapper)
        dialogBuilder.setMessage(message)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                try {
                    dialog.cancel()
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        val alert = dialogBuilder.create()
        alert.alertButton(color)
        alert.show()
    }

}
fun Activity.alertOk(color: Int,message: String, callback: (confirmed: Boolean) -> Unit){
    // set message of alert dialog
    runOnUiThread {
        val wrapper: Context = ContextThemeWrapper(this, R.style.alertTheme)
        val dialogBuilder = AlertDialog.Builder(wrapper)
        dialogBuilder.setMessage(message)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                try {
                    dialog.cancel()
                    callback(true)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        val alert = dialogBuilder.create()
        alert.alertButton(color)
        alert.show()
    }

}