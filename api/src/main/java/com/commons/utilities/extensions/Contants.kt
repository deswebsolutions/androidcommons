package com.commons.utilities.extensions

import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import java.util.*

val normalizeRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}

/**
 *  date and time
 */
const val TIME_FORMAT_12 = "hh:mm a"
const val TIME_FORMAT_24 = "HH:mm"

const val DATE_FORMAT_ONE = "dd.MM.yyyy"
const val DATE_FORMAT_TWO = "dd/MM/yyyy"
const val DATE_FORMAT_THREE = "MM/dd/yyyy"
const val DATE_FORMAT_FOUR = "yyyy-MM-dd"
const val DATE_FORMAT_FIVE = "d MMMM yyyy"
const val DATE_FORMAT_SIX = "MMMM d yyyy"
const val DATE_FORMAT_SEVEN = "MM-dd-yyyy"
const val DATE_FORMAT_EIGHT = "dd-MM-yyyy"

const val EMPTY=""

//vibration
const val SHORT_VIBRATE_LENGTH: Long = 20
const val DEFAULT_VIBRATE_LENGTH: Long = 100

var sLocale: Locale? = null

/**
 *  whatsapp
 */
const val WHATSAPP_PACKAGE="com.whatsapp"
const val WHATSAPP_VOICE_CALL="vnd.android.cursor.item/vnd.com.whatsapp.voip.call"
const val WHATSAPP_VIDEO_CALL="vnd.android.cursor.item/vnd.com.whatsapp.video.call"

const val ACTION_ANSWER = "ANSWER"
const val ACTION_HANGUP = "HANGUP"
const val ACTION_CALLBACK = "CALLBACK"
const val ACTION_SENDMESSAGE = "SEND_MESSAGE"
const val CALL_RECORDER = "call_recorder"
const val CALL_DATE = "calldate"
const val VERIFICATION_TIME = "verifcation_time"
const val VERIFICATION_CODE = "verifcation_code"
const val NOTIFICATION_TIME = "notification_time"
const val ACTIVE_CALL_NUMBER = "active_call_number"
const val ACTIVE_CALL_RECORDING_TIME = "active_call_recording_time"

/**
 *  Handlers
 */
const val TIME_START = 1
const val TIME_STOP = 0
const val TIME_UPDATE = 2
const val REFRESH_RATE = 100


/**
 *  permission actions (notification access, settings )
 */
const val PERMISSIONS = "permissions"
const val ACTION_PERMISSION="action_permission"
const val ACTION_TYPE="action_type"
const val TITLE = "title"
const val HINT = "hint"
const val TAP_TO ="tap_to"
const val PERMISSION_REQUEST="permission_request"
/**
 *  Permissions
 */
const val PERMISSION_READ_STORAGE = 1
const val PERMISSION_WRITE_STORAGE = 2
const val PERMISSION_CAMERA = 3
const val PERMISSION_RECORD_AUDIO = 4
const val PERMISSION_READ_CONTACTS = 5
const val PERMISSION_WRITE_CONTACTS = 6
const val PERMISSION_READ_CALENDAR = 7
const val PERMISSION_WRITE_CALENDAR = 8
const val PERMISSION_CALL_PHONE = 9
const val PERMISSION_READ_CALL_LOG = 10
const val PERMISSION_WRITE_CALL_LOG = 11
const val PERMISSION_GET_ACCOUNTS = 12
const val PERMISSION_READ_SMS = 13
const val PERMISSION_SEND_SMS = 14
const val PERMISSION_READ_PHONE_STATE = 15
const val PERMISSION_ACCESS_ACTION = 16
var displayContactSources = ArrayList<String>()
fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

/**
 *  account stages
 */
const val ACCOUNT_STATE = "account_state"
const val STATE_PRIVACY ="stage_privacy"
const val STATE_AUTHENTICATE ="stage_auth"
const val STATE_BIO_DATA ="stage_bio_data"
const val STATE_VERIFICATION ="stage_verify"
const val STATE_PIN_SETUP ="stage_pin"

const val REQUEST_CODE_SET_DEFAULT_DIALER = 1005
val GENERIC_PERM_HANDLER = 100
val SELECT_NUMBER = 11
const val NO_TITLE = 0

val BATTERY_PERMISSION = 1002
val NOTIFICATION_PRIVATE_CALLS = 2
val NOTIFICATION_ALL_CALLS = 4
val NOTIFICATION_BLOCKING_CALLS = 3
val NOTIFICATION_PLEASE_WAIT = 5
val NOTIFICATION_NOTIFIER = 6

//audio
const val TONE_LENGTH_MS = 150 // The length of DTMF tones in milliseconds
const val TONE_RELATIVE_VOLUME = 80 // The DTMF tone volume relative to other sounds in the stream
const val DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF // Stream type used to play the DTMF tones off call, and mapped to the volume control keys

/**
 *  report a problem
 */
var IMAGE_TYPE = 0
var PICK_IMAGE_REQUEST = 1
var URI_1: Uri? = null
var URI_2: Uri? = null
var URI_3: Uri? = null

/**
 * authentication
 */
val PHONE_NUMBER="phone"
val AUTH_NUMBER="auth_phone"
val AUTH_COUNTRY_CODE="auth_country_code"

val IS_DEVICE_LOCKED = "is_device_locked"
val IS_TEST_ACCOUNT ="is_test_account"
val SHARED_ACCOUNT ="shared_account"

/**
 *  response (success or fail)
 */
val SUCCESS = "success"
val NUMBERS="numbers"
val PRIVATE_NUMBER="private_number"
/**
 *  clicks and loads
 **/
var HAS_CLICKED = false

/**
 * Action Permission
 */
val NOTIFICATION_ACCESS = "notification_access"
val SYSTEM_SETTINGS_ACCESS = "modify_system_settings_access"
val SYSTEM_PERMISSION = "system_permission"


/**
 * shared preferences
 */
const val PREFS_KEY = "Prefs"
const val SPEED_DIAL = "speed_dial"
const val TEXT_COLOR = "text_color"
const val BACKGROUND_COLOR = "background_color"
const val PRIMARY_COLOR = "primary_color_2"
const val USE_24_HOUR_FORMAT = "use_24_hour_format"
const val DEFAULT_TAB = "default_tab"
const val LAST_USED_VIEW_PAGER_PAGE = "last_used_view_pager_page"


/**
 * display mode
 */
const val DARK_THEME = "dark_theme"