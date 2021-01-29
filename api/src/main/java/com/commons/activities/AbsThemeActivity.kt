package com.commons.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.commons.R
import com.commons.utilities.actionOnPermission
import com.commons.utilities.extensions.DARK_THEME
import com.commons.utilities.extensions.GENERIC_PERM_HANDLER
import com.commons.utilities.getBoolSession
import com.commons.utilities.isAskingPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@SuppressLint("onConstantResourceId","NonConstantResourceId")
abstract class AbsThemeActivity(val title : Int) : AppCompatActivity() {
    val activityScope = CoroutineScope(Dispatchers.Main)


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        /**
         *  setting up action bar title and back arrow
         *  if title (which is an int) is 0, hide action bar
         *  else show title and add back arrow
         */
        if(title != 0) {
            if(title == R.string.empty){
                supportActionBar!!.elevation=0F
            }
            supportActionBar!!.title = getString(title)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }else{
            supportActionBar!!.hide()
            supportActionBar!!.title = getString(R.string.empty)
        }

        if(getBoolSession(DARK_THEME)!!) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == GENERIC_PERM_HANDLER && grantResults.isNotEmpty()) {
            actionOnPermission?.invoke(grantResults[0] == 0)
        }
    }

    abstract fun authSuccess()
}