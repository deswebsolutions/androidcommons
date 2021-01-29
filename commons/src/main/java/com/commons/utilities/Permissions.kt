package com.commons.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.commons.R
import com.commons.utilities.extensions.*

var actionOnPermission: ((granted: Boolean) -> Unit)? = null
var isAskingPermissions = false

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.isAppDefaultDialer() : Boolean{
    return if (!packageName.startsWith("com.callmode.android")) {
        true
    } else if ((packageName.startsWith("com.callmode.android")) && isQPlus()) {
        val roleManager = getSystemService(RoleManager::class.java)
        roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && roleManager.isRoleHeld(
            RoleManager.ROLE_DIALER
        )
    } else {
        isMarshmallowPlus() && telecomManager.defaultDialerPackage == packageName
    }
}
@RequiresApi(Build.VERSION_CODES.M)
fun Context.isAppDefaultDialer() : Boolean{
    return if (!packageName.startsWith("com.callmode.android")) {
        true
    } else if ((packageName.startsWith("com.callmode.android")) && isQPlus()) {
        val roleManager = getSystemService(RoleManager::class.java)
        roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && roleManager.isRoleHeld(
            RoleManager.ROLE_DIALER
        )
    } else {
        isMarshmallowPlus() && telecomManager.defaultDialerPackage == packageName
    }
}
@SuppressLint("QueryPermissionsNeeded")
@RequiresApi(Build.VERSION_CODES.M)
fun Activity.launchSetDefaultDialerIntent() {
    val packageName =packageName
    if (isQPlus()) {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && !roleManager.isRoleHeld(
                RoleManager.ROLE_DIALER
            )) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
        }
    } else {
        Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
            packageName
        ).apply {

            if (resolveActivity(packageManager) != null) {
                startActivityForResult(this, REQUEST_CODE_SET_DEFAULT_DIALER)
            } else {
                toast(R.string.no_app_found)
            }
        }

    }
}

fun Activity.handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
    actionOnPermission = null
    if (hasPermission(permissionId)) {
        callback(true)
    } else {
        isAskingPermissions = true
        actionOnPermission = callback
        ActivityCompat.requestPermissions(
            this,
            arrayOf(getPermissionString(permissionId)),
            GENERIC_PERM_HANDLER
        )
    }
}
/**
 * Check is premissions granted by grant results list
 * Return true only if all permissions were granted
 *
 * @param grantResults permissions grant results
 * @return boolean is all granted / not
 */
fun Context.hasPermissions(permissions: Array<String>, callback: (granted: Boolean) -> Unit) {
    var hasGranted = true
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            hasGranted=false
        }
    }
    callback(hasGranted)
}
fun Activity.handlePermission(permissions: IntArray, callback: (granted: Boolean) -> Unit) {
    actionOnPermission = null
    var permissionList = arrayOf<String>()
    for (permission in permissions){
        permissionList += getPermissionString(permission)
    }

    hasPermissions(permissionList){
        if(it){
            callback(true)
        }else{
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(this, permissionList, GENERIC_PERM_HANDLER)
        }
    }

}
fun Activity.handlePermission(permissions: Array<String>, callback: (granted: Boolean) -> Unit) {
    actionOnPermission = null
    hasPermissions(permissions){
        if(it){
            callback(true)
        }else{
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(this, permissions, GENERIC_PERM_HANDLER)
        }
    }

}
/**
 * Asks user for permissions by a given list
 * From a fragment
 *
 * @param fragment    fragment from which to request the permissions
 * @param permissions permission to ask for
 */


@RequiresApi(Build.VERSION_CODES.M)
fun Context.checkBatteryOptimized(): Boolean {
    val pwrm = getSystemService(Context.POWER_SERVICE) as PowerManager
    val name = applicationContext.packageName
    if (isMarshmallowPlus()) {
        return !pwrm.isIgnoringBatteryOptimizations(name)
    }
    return false
}

@SuppressLint("BatteryLife")
@RequiresApi(Build.VERSION_CODES.M)
fun Activity.checkBattery() {
    if (isMarshmallowPlus()) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:${packageName}")
        startActivityForResult(intent, BATTERY_PERMISSION)
    }
}
@RequiresApi(Build.VERSION_CODES.M)
fun Activity.requestActionPermissions(permission: String, actionType: String){
    if(actionType==SYSTEM_SETTINGS_ACCESS) {
        val intent= Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:${packageName}")
        startActivity(intent)
    }else {
        val intent = Intent(permission)
        startActivity(intent)
    }

}

