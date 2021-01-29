package com.commons.heipers.fragments

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import butterknife.ButterKnife
import com.commons.activities.AbsThemeActivity

abstract class ViewPagerFragment(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    protected var activity: AbsThemeActivity? = null

    fun setupFragment(activity: AbsThemeActivity) {
        if (this.activity == null) {
            this.activity = activity
            ButterKnife.bind(this)
            setupFragment()
        }

    }

    abstract fun setupFragment()

    abstract fun onSearchClosed()

    abstract fun onSearchQueryChanged(text: String)

    abstract fun onSuccess(data : Any?=null)
}
