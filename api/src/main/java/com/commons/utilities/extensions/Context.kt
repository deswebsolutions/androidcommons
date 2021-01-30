package com.commons.utilities.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.provider.CallLog
import android.provider.MediaStore
import android.telecom.TelecomManager
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.commons.R
import com.commons.heipers.views.*
import com.commons.utilities.getIntSession
import com.commons.utilities.getStringSession
import com.commons.utilities.searchNumber
import com.commons.utilities.setIntSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.runOnUiThread
import java.io.ByteArrayOutputStream
import java.util.*


var mContext : Context? = null

fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

val Context.baseConfig: BaseConfig get() = BaseConfig.newInstance(this)
val Context.config: Config get() = Config.newInstance(applicationContext)

fun Context.contactColor(number : String) : Int {
    val phone = searchNumber(number)
    if (getIntSession("color_$phone") == -1) {
        setIntSession("color_${searchNumber(number)}", getRandomColor())
    }
    return getIntSession("color_$phone")
}
fun areNotEqual(a : Any, b : Any):Boolean {
    return a != b
}
fun Context.getTimeFormat() = if (baseConfig.use24HourFormat) TIME_FORMAT_24 else TIME_FORMAT_12
val Context.telecomManager: TelecomManager get() = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
val activityScope = CoroutineScope(Dispatchers.Main)
val Context.windowManager: WindowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager
val Context.notificationManager: NotificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
val Context.shortcutManager: ShortcutManager
    @RequiresApi(Build.VERSION_CODES.N_MR1)
get() = getSystemService(ShortcutManager::class.java) as ShortcutManager
val Context.audioManager get()= getSystemService(Context.AUDIO_SERVICE) as AudioManager
fun Context.geRecentCallsCursor() = CursorLoader(this, CallLog.Calls.CONTENT_URI.buildUpon().build(), null, null, null, null)

fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(this,
    getPermissionString(permId)
) == PackageManager.PERMISSION_GRANTED
val default_permission=arrayOf(getPermissionString(PERMISSION_READ_CALL_LOG), getPermissionString(PERMISSION_READ_PHONE_STATE),getPermissionString(PERMISSION_READ_CONTACTS) )
fun getPermissionString(id: Int) = when (id) {
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    PERMISSION_CAMERA -> Manifest.permission.CAMERA
    PERMISSION_RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
    PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
    PERMISSION_WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
    PERMISSION_READ_CALENDAR -> Manifest.permission.READ_CALENDAR
    PERMISSION_WRITE_CALENDAR -> Manifest.permission.WRITE_CALENDAR
    PERMISSION_CALL_PHONE -> Manifest.permission.CALL_PHONE
    PERMISSION_READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
    PERMISSION_WRITE_CALL_LOG -> Manifest.permission.WRITE_CALL_LOG
    PERMISSION_GET_ACCOUNTS -> Manifest.permission.GET_ACCOUNTS
    PERMISSION_READ_SMS -> Manifest.permission.READ_SMS
    PERMISSION_SEND_SMS -> Manifest.permission.SEND_SMS
    PERMISSION_READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
    else -> ""
}


fun Context.toast(id: Int, length: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        toast(getString(id), length)
    }
}

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        if (isOnMainThread()) {
            doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
    }
}
private fun doToast(context: Context, message: String, length: Int) {
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            Toast.makeText(context, message, length).show()
        }
    } else {
        Toast.makeText(context, message, length).show()
    }
}

fun Context.showErrorToast(msg: String, length: Int = Toast.LENGTH_LONG) {
    toast(String.format(getString(R.string.an_error_occurred), msg), length)
}

fun Context.showErrorToast(exception: Exception, length: Int = Toast.LENGTH_LONG) {
    showErrorToast(exception.toString(), length)
}
/**
 * Get the dpi for this phone
 *
 * @return the dpi
 */
fun Context.dpi(): Float {
    val displayMetrics = DisplayMetrics()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.densityDpi.toFloat()
}

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param context Context to get resources and device specific display metrics
 * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @return A float value to represent px equivalent to dp depending on device density
 */

fun Context.convertDpToPixel(dp: Int): Float {
    return dp * (dpi() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * Vibrate the phone
 *
 * @param millis the amount of milliseconds to vibrate the phone for.
 */
/**
 * Vibrate the phone for `DEFAULT_VIBRATE_LENGTH` milliseconds
 */
fun Context.vibrate(millis: Long = DEFAULT_VIBRATE_LENGTH) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE)) else vibrator.vibrate(millis)
}
fun Context.getRandomColor() : Int{
    val androidColors: IntArray = resources.getIntArray(R.array.randomcolors)
    return androidColors[Random().nextInt(androidColors.size)]
}

/**
 * Returns only the numbers from a string (removes special characters and spaces
 *
 * @param string the string to get all the numbers from
 * @return string the numbers extracted from the given string
 */
fun getOnlyNumbers(string: String): String {
    return string.replace("[^0-9#*]".toRegex(), "")
}

/**
 * Check for permissions by a given list
 * Return true *only* if all of the given permissions are granted
 *
 * @param context     from where the function is being called
 * @param permissions permission to check if granted
 * @return boolean is permissions granted / not
 */

fun Context.setClipboard(text: String) {
    val clipboard =
        getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = ClipData.newPlainText("Copied", text)
    clipboard.setPrimaryClip(clip)
    toast(getString(R.string.copied))
}


fun Context.editPhoneNumberInDialer(phone: String){
    val phoneIntent = Intent(
        Intent.ACTION_DIAL, Uri.fromParts(
            "tel", phone, null
        )
    )
    startActivity(phoneIntent)
}

@SuppressLint("Recycle")
fun Context.getRealPathFromURI(mPhotoUri: Uri?): String? {
    val cursor: Cursor = contentResolver.query(mPhotoUri!!, null, null, null, null)!!
    cursor.moveToFirst()
    val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
    return cursor.getString(idx)
}
fun Context.getImageUri(inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String = MediaStore.Images.Media.insertImage(
        contentResolver,
        inImage,
        "Title",
        null
    )
    return Uri.parse(path)
}


fun Context.getTextSize() = resources.getDimension(R.dimen.bigger_text_size)



