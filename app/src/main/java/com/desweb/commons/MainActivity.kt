package com.desweb.commons

import android.os.Bundle
import com.commons.activities.AbsThemeActivity
import com.commons.utilities.extensions.NO_TITLE

class MainActivity : AbsThemeActivity(NO_TITLE) {
    override fun authSuccess() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}