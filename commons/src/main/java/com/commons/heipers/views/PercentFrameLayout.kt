package com.commons.heipers.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout

@Deprecated("")
open class PercentFrameLayout : FrameLayout {
    private val mHelper: PercentLayoutHelper = PercentLayoutHelper(this)

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    )

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mHelper.handleMeasuredStateTooSmall()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mHelper.restoreOriginalParams()
    }

    @Deprecated("this class is deprecated along with its parent class.")
    class LayoutParams : FrameLayout.LayoutParams, PercentLayoutHelper.PercentLayoutParams {
        private var mPercentLayoutInfo: PercentLayoutHelper.PercentLayoutInfo? = null

        constructor(c: Context?, attrs: AttributeSet?) : super(c!!, attrs) {
            mPercentLayoutInfo = PercentLayoutHelper.getPercentLayoutInfo(c, attrs)
        }

        constructor(width: Int, height: Int) : super(width, height) {}


        override val percentLayoutInfo: PercentLayoutHelper.PercentLayoutInfo?
            get() {
                if (mPercentLayoutInfo == null) {
                    mPercentLayoutInfo = PercentLayoutHelper.PercentLayoutInfo()
                }
                return mPercentLayoutInfo
            }

        override fun setBaseAttributes(a: TypedArray, widthAttr: Int, heightAttr: Int) {
            PercentLayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr)
        }
    }
}