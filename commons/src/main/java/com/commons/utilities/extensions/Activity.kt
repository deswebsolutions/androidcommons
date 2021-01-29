package com.commons.utilities.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.ContactsContract
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.view.children
import com.commons.R
import java.lang.reflect.Field
import java.lang.reflect.Method

fun Activity.hideKeyboard() {
    val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
fun Context.isDeviceLocked(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isInteractive
}
fun Activity.showKeypad(editText : EditText){
    editText.requestFocus()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editText, InputMethodManager.SHOW_FORCED)
}
fun Activity.showKeyboard(){
    Handler(Looper.getMainLooper()).postDelayed({
        try {
            val v = currentFocus
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(v!!, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, 100)
}
fun Context.changeColor(menu: Menu) {
    for (icon in menu.children){
        val menuicon: Drawable =icon.icon
        menuicon.mutate()
        menuicon.setColorFilter(getColorFromAttr(R.attr.body_text_color), PorterDuff.Mode.SRC_IN)
    }
}
@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}
fun PopupMenu.setPopMenuForceShowIcon() {
    try {
        val fields: Array<Field> = javaClass.declaredFields
        for (field in fields) {
            if ("mPopup" == field.name) {
                field.isAccessible = true
                val menuPopupHelper: Any = field.get(this)
                val classPopupHelper = Class.forName(
                    menuPopupHelper
                        .javaClass.name
                )
                val setForceIcons: Method = classPopupHelper.getMethod(
                    "setForceShowIcon", Boolean::class.javaPrimitiveType
                )
                setForceIcons.invoke(menuPopupHelper, true)
                break
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}
fun popupMenu(v: View, x: Float = 0F, y: Float = 0F) : View {
    val view =v
    if(areNotEqual(x, 0F))
        view.x = v.pivotX+x

    if(areNotEqual(y, 0F))
        view.y = v.pivotY + y
    return view
}
/**
 * Builds a string without the separators
 *
 * @param list
 * @param separator
 * @return String
 */
fun joinStringsWithSeparator(list: List<String?>, separator: String): String {
    if (list.size == 0) return ""
    val builder = StringBuilder()
    for (str in list) {
        builder.append(str)
        builder.append(separator)
    }
    val result = builder.toString()
    return result.substring(0, result.length - separator.length)
}



fun Activity.sendWhatsAppMessage(contact_phone: String){
    val uri = Uri.parse("smsto:$contact_phone")
    val i = Intent(Intent.ACTION_SENDTO, uri)
    i.setPackage("com.whatsapp")
    startActivity(i)
}
fun Activity.hasWhatsApp(contactID: String): Boolean {
    var whatsAppExists = false
    val hasWhatsApp: Boolean
    val projection = arrayOf(ContactsContract.RawContacts._ID)
    val selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)"
    val selectionArgs = arrayOf(contactID, WHATSAPP_PACKAGE)
    val cursor: Cursor? = contentResolver.query(
        ContactsContract.RawContacts.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )
    if (cursor != null) {
        hasWhatsApp = cursor.moveToNext()
        if (hasWhatsApp) {
            whatsAppExists = true
        }
        cursor.close()
    }
    return whatsAppExists
}

fun Activity.whatsAppCallId(call_type: String, contact_phone: String): Long {
    var id: Long = -1
    if (hasPermission(PERMISSION_READ_CONTACTS) ) {
        val cursor: Cursor? = contentResolver
            .query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.Data._ID),
                ContactsContract.RawContacts.ACCOUNT_TYPE + " = 'com.whatsapp' " +
                        "AND " + ContactsContract.Data.MIMETYPE + " = '$call_type' " +
                        "AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + contact_phone + "%'",
                null,
                ContactsContract.Contacts.DISPLAY_NAME
            )
        if (cursor != null) { // throw an exception
            while (cursor.moveToNext()) {
                id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID))
            }
        }
        if (!cursor!!.isClosed) {
            cursor.close()
        }
    }
    return id
}
fun Activity.whatsAppCall(call_type: String, contact: String) {
    try {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW

        intent.setDataAndType(
            Uri.parse(
                "content://com.android.contacts/data/${
                    whatsAppCallId(
                        call_type,
                        contact
                    )
                }"
            ),
            call_type
        )
        intent.setPackage("com.whatsapp")
        startActivity(intent)
    } catch (e: Exception) {

    }
}

@SuppressLint("ServiceCast")
fun Context.verifyAvailableNetwork(): Boolean {
    var isConnected = false
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (isMarshmallowPlus()) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        isConnected = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            activeNetworkInfo?.run {
                isConnected = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return isConnected
}



fun ImageView.applyColorFilter(color: Int) = setColorFilter(color, PorterDuff.Mode.SRC_IN)

