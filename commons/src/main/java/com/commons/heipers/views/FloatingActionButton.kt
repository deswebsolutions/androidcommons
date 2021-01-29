package com.commons.heipers.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.commons.utilities.extensions.applyColorFilter
import com.commons.utilities.extensions.getContrastColor
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FloatingActionButton :
    FloatingActionButton {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setColors(textColor: Int, accentColor: Int, backgroundColor: Int) {
        backgroundTintList = ColorStateList.valueOf(accentColor)
        applyColorFilter(accentColor.getContrastColor())
    }
}