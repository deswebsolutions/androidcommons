package com.commons.heipers.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import com.commons.R

@Deprecated("")
class PercentLayoutHelper(@NonNull host: ViewGroup?) {
    private val mHost: ViewGroup

    /**
     * Iterates over children and changes their width and height to one calculated from percentage
     * values.
     * @param widthMeasureSpec Width MeasureSpec of the parent ViewGroup.
     * @param heightMeasureSpec Height MeasureSpec of the parent ViewGroup.
     */
    fun adjustChildren(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (DEBUG) {
            Log.d(
                TAG, "adjustChildren: " + mHost + " widthMeasureSpec: "
                        + View.MeasureSpec.toString(widthMeasureSpec) + " heightMeasureSpec: "
                        + View.MeasureSpec.toString(heightMeasureSpec)
            )
        }
        // Calculate available space, accounting for host's paddings
        val widthHint: Int = (View.MeasureSpec.getSize(widthMeasureSpec) - mHost.paddingLeft
                - mHost.paddingRight)
        val heightHint: Int = (View.MeasureSpec.getSize(heightMeasureSpec) - mHost.paddingTop
                - mHost.paddingBottom)
        var i = 0
        val N = mHost.childCount
        while (i < N) {
            val view: View = mHost.getChildAt(i)
            val params: ViewGroup.LayoutParams = view.getLayoutParams()
            if (DEBUG) {
                Log.d(TAG, "should adjust $view $params")
            }
            if (params is PercentLayoutParams) {
                val info = (params as PercentLayoutParams).percentLayoutInfo
                if (DEBUG) {
                    Log.d(TAG, "using $info")
                }
                if (info != null) {
                    if (params is ViewGroup.MarginLayoutParams) {
                        info.fillMarginLayoutParams(
                            view, params as ViewGroup.MarginLayoutParams,
                            widthHint, heightHint
                        )
                    } else {
                        info.fillLayoutParams(params, widthHint, heightHint)
                    }
                }
            }
            i++
        }
    }

    /**
     * Iterates over children and restores their original dimensions that were changed for
     * percentage values. Calling this method only makes sense if you previously called
     * [PercentLayoutHelper.adjustChildren].
     */
    fun restoreOriginalParams() {
        var i = 0
        val N = mHost.childCount
        while (i < N) {
            val view: View = mHost.getChildAt(i)
            val params: ViewGroup.LayoutParams = view.getLayoutParams()
            if (DEBUG) {
                Log.d(TAG, "should restore $view $params")
            }
            if (params is PercentLayoutParams) {
                val info = (params as PercentLayoutParams).percentLayoutInfo
                if (DEBUG) {
                    Log.d(TAG, "using $info")
                }
                if (info != null) {
                    if (params is ViewGroup.MarginLayoutParams) {
                        info.restoreMarginLayoutParams(params as ViewGroup.MarginLayoutParams)
                    } else {
                        info.restoreLayoutParams(params)
                    }
                }
            }
            i++
        }
    }

    /**
     * Iterates over children and checks if any of them would like to get more space than it
     * received through the percentage dimension.
     *
     * If you are building a layout that supports percentage dimensions you are encouraged to take
     * advantage of this method. The developer should be able to specify that a child should be
     * remeasured by adding normal dimension attribute with `wrap_content` value. For example
     * he might specify child's attributes as `app:layout_widthPercent="60%p"` and
     * `android:layout_width="wrap_content"`. In this case if the child receives too little
     * space, it will be remeasured with width set to `WRAP_CONTENT`.
     *
     * @return True if the measure phase needs to be rerun because one of the children would like
     * to receive more space.
     */
    fun handleMeasuredStateTooSmall(): Boolean {
        var needsSecondMeasure = false
        var i = 0
        val N = mHost.childCount
        while (i < N) {
            val view: View = mHost.getChildAt(i)
            val params: ViewGroup.LayoutParams = view.getLayoutParams()
            if (DEBUG) {
                Log.d(
                    TAG,
                    "should handle measured state too small $view $params"
                )
            }
            if (params is PercentLayoutParams) {
                val info = (params as PercentLayoutParams).percentLayoutInfo
                if (info != null) {
                    if (shouldHandleMeasuredWidthTooSmall(view, info)) {
                        needsSecondMeasure = true
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    if (shouldHandleMeasuredHeightTooSmall(view, info)) {
                        needsSecondMeasure = true
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
            }
            i++
        }
        if (DEBUG) {
            Log.d(
                TAG,
                "should trigger second measure pass: $needsSecondMeasure"
            )
        }
        return needsSecondMeasure
    }

    /* package */
    class PercentMarginLayoutParams(width: Int, height: Int) :
        ViewGroup.MarginLayoutParams(width, height) {
        // These two flags keep track of whether we're computing the LayoutParams width and height
        // in the fill pass based on the aspect ratio. This allows the fill pass to be re-entrant
        // as the framework code can call onMeasure() multiple times before the onLayout() is
        // called. Those multiple invocations of onMeasure() are not guaranteed to be called with
        // the same set of width / height.
        var mIsHeightComputedFromAspectRatio = false
        var mIsWidthComputedFromAspectRatio = false
    }

    /**
     * Container for information about percentage dimensions and margins. It acts as an extension
     * for `LayoutParams`.
     *
     */
    @Deprecated("use ConstraintLayout and Guidelines for layout support.")
    class PercentLayoutInfo() {
        /** The decimal value of the percentage-based width.  */
        var widthPercent: Float

        /** The decimal value of the percentage-based height.  */
        var heightPercent: Float

        /** The decimal value of the percentage-based left margin.  */
        var leftMarginPercent: Float

        /** The decimal value of the percentage-based top margin.  */
        var topMarginPercent: Float

        /** The decimal value of the percentage-based right margin.  */
        var rightMarginPercent: Float

        /** The decimal value of the percentage-based bottom margin.  */
        var bottomMarginPercent: Float

        /** The decimal value of the percentage-based start margin.  */
        var startMarginPercent: Float

        /** The decimal value of the percentage-based end margin.  */
        var endMarginPercent: Float

        /** The decimal value of the percentage-based aspect ratio.  */
        var aspectRatio = 0f

        /* package */
        val mPreservedParams: PercentMarginLayoutParams

        /**
         * Fills the [ViewGroup.LayoutParams.width] and [ViewGroup.LayoutParams.height]
         * fields of the passed [ViewGroup.LayoutParams] object based on currently set
         * percentage values.
         */
        fun fillLayoutParams(
            params: ViewGroup.LayoutParams, widthHint: Int,
            heightHint: Int
        ) {
            // Preserve the original layout params, so we can restore them after the measure step.
            mPreservedParams.width = params.width
            mPreservedParams.height = params.height
            // We assume that width/height set to 0 means that value was unset. This might not
            // necessarily be true, as the user might explicitly set it to 0. However, we use this
            // information only for the aspect ratio. If the user set the aspect ratio attribute,
            // it means they accept or soon discover that it will be disregarded.
            val widthNotSet = (mPreservedParams.mIsWidthComputedFromAspectRatio
                    || mPreservedParams.width == 0) && widthPercent < 0
            val heightNotSet = (mPreservedParams.mIsHeightComputedFromAspectRatio
                    || mPreservedParams.height == 0) && heightPercent < 0
            if (widthPercent >= 0) {
                params.width = Math.round(widthHint * widthPercent)
            }
            if (heightPercent >= 0) {
                params.height = Math.round(heightHint * heightPercent)
            }
            if (aspectRatio >= 0) {
                if (widthNotSet) {
                    params.width = Math.round(params.height * aspectRatio)
                    // Keep track that we've filled the width based on the height and aspect ratio.
                    mPreservedParams.mIsWidthComputedFromAspectRatio = true
                }
                if (heightNotSet) {
                    params.height = Math.round(params.width / aspectRatio)
                    // Keep track that we've filled the height based on the width and aspect ratio.
                    mPreservedParams.mIsHeightComputedFromAspectRatio = true
                }
            }
            if (DEBUG) {
                Log.d(TAG, "after fillLayoutParams: (" + params.width + ", " + params.height + ")")
            }
        }

        @Deprecated("Use\n" + "          {@link #fillMarginLayoutParams(View, ViewGroup.MarginLayoutParams, int, int)}\n" + "          for proper RTL support.")
        fun fillMarginLayoutParams(
            params: ViewGroup.MarginLayoutParams,
            widthHint: Int,
            heightHint: Int
        ) {
            fillMarginLayoutParams(null, params, widthHint, heightHint)
        }

        /**
         * Fills the margin fields of the passed [ViewGroup.MarginLayoutParams] object based
         * on currently set percentage values and the current layout direction of the passed
         * [View].
         */
        fun fillMarginLayoutParams(
            view: View?, params: ViewGroup.MarginLayoutParams,
            widthHint: Int, heightHint: Int
        ) {
            fillLayoutParams(params, widthHint, heightHint)
            // Preserve the original margins, so we can restore them after the measure step.
            mPreservedParams.leftMargin = params.leftMargin
            mPreservedParams.topMargin = params.topMargin
            mPreservedParams.rightMargin = params.rightMargin
            mPreservedParams.bottomMargin = params.bottomMargin
            MarginLayoutParamsCompat.setMarginStart(
                mPreservedParams,
                MarginLayoutParamsCompat.getMarginStart(params)
            )
            MarginLayoutParamsCompat.setMarginEnd(
                mPreservedParams,
                MarginLayoutParamsCompat.getMarginEnd(params)
            )
            if (leftMarginPercent >= 0) {
                params.leftMargin = Math.round(widthHint * leftMarginPercent)
            }
            if (topMarginPercent >= 0) {
                params.topMargin = Math.round(heightHint * topMarginPercent)
            }
            if (rightMarginPercent >= 0) {
                params.rightMargin = Math.round(widthHint * rightMarginPercent)
            }
            if (bottomMarginPercent >= 0) {
                params.bottomMargin = Math.round(heightHint * bottomMarginPercent)
            }
            var shouldResolveLayoutDirection = false
            if (startMarginPercent >= 0) {
                MarginLayoutParamsCompat.setMarginStart(
                    params,
                    Math.round(widthHint * startMarginPercent)
                )
                shouldResolveLayoutDirection = true
            }
            if (endMarginPercent >= 0) {
                MarginLayoutParamsCompat.setMarginEnd(
                    params,
                    Math.round(widthHint * endMarginPercent)
                )
                shouldResolveLayoutDirection = true
            }
            if (shouldResolveLayoutDirection && view != null) {
                // Force the resolve pass so that start / end margins are propagated to the
                // matching left / right fields
                MarginLayoutParamsCompat.resolveLayoutDirection(
                    params,
                    ViewCompat.getLayoutDirection(view)
                )
            }
            if (DEBUG) {
                Log.d(
                    TAG, "after fillMarginLayoutParams: (" + params.width + ", " + params.height
                            + ")"
                )
            }
        }

        override fun toString(): String {
            return String.format(
                "PercentLayoutInformation width: %f height %f, margins (%f, %f, "
                        + " %f, %f, %f, %f)", widthPercent, heightPercent, leftMarginPercent,
                topMarginPercent, rightMarginPercent, bottomMarginPercent, startMarginPercent,
                endMarginPercent
            )
        }

        /**
         * Restores the original dimensions and margins after they were changed for percentage based
         * values. You should call this method only if you previously called
         * [PercentLayoutHelper.PercentLayoutInfo.fillMarginLayoutParams].
         */
        fun restoreMarginLayoutParams(params: ViewGroup.MarginLayoutParams) {
            restoreLayoutParams(params)
            params.leftMargin = mPreservedParams.leftMargin
            params.topMargin = mPreservedParams.topMargin
            params.rightMargin = mPreservedParams.rightMargin
            params.bottomMargin = mPreservedParams.bottomMargin
            MarginLayoutParamsCompat.setMarginStart(
                params,
                MarginLayoutParamsCompat.getMarginStart(mPreservedParams)
            )
            MarginLayoutParamsCompat.setMarginEnd(
                params,
                MarginLayoutParamsCompat.getMarginEnd(mPreservedParams)
            )
        }

        /**
         * Restores original dimensions after they were changed for percentage based values.
         * You should call this method only if you previously called
         * [PercentLayoutHelper.PercentLayoutInfo.fillLayoutParams].
         */
        fun restoreLayoutParams(params: ViewGroup.LayoutParams) {
            if (!mPreservedParams.mIsWidthComputedFromAspectRatio) {
                // Only restore the width if we didn't compute it based on the height and
                // aspect ratio in the fill pass.
                params.width = mPreservedParams.width
            }
            if (!mPreservedParams.mIsHeightComputedFromAspectRatio) {
                // Only restore the height if we didn't compute it based on the width and
                // aspect ratio in the fill pass.
                params.height = mPreservedParams.height
            }
            // Reset the tracking flags.
            mPreservedParams.mIsWidthComputedFromAspectRatio = false
            mPreservedParams.mIsHeightComputedFromAspectRatio = false
        }

        init {
            widthPercent = -1f
            heightPercent = -1f
            leftMarginPercent = -1f
            topMarginPercent = -1f
            rightMarginPercent = -1f
            bottomMarginPercent = -1f
            startMarginPercent = -1f
            endMarginPercent = -1f
            mPreservedParams = PercentMarginLayoutParams(0, 0)
        }
    }

    /**
     * If a layout wants to support percentage based dimensions and use this helper class, its
     * `LayoutParams` subclass must implement this interface.
     *
     * Your `LayoutParams` subclass should contain an instance of `PercentLayoutInfo`
     * and the implementation of this interface should be a simple accessor.
     *
     */
    @Deprecated("this class is deprecated along with its parent class.")
    interface PercentLayoutParams {
        val percentLayoutInfo: PercentLayoutInfo?
    }

    companion object {
        private val TAG = "PercentLayout"
        private val DEBUG = false
        private val VERBOSE = false

        /**
         * Helper method to be called from [ViewGroup.LayoutParams.setBaseAttributes] override
         * that reads layout_width and layout_height attribute values without throwing an exception if
         * they aren't present.
         */
        fun fetchWidthAndHeight(
            params: ViewGroup.LayoutParams, array: TypedArray,
            widthAttr: Int, heightAttr: Int
        ) {
            params.width = array.getLayoutDimension(widthAttr, 0)
            params.height = array.getLayoutDimension(heightAttr, 0)
        }

        /**
         * Constructs a PercentLayoutInfo from attributes associated with a View. Call this method from
         * `LayoutParams(Context c, AttributeSet attrs)` constructor.
         */
        fun getPercentLayoutInfo(
            context: Context,
            attrs: AttributeSet?
        ): PercentLayoutInfo? {
            var info: PercentLayoutInfo? = null
            val array: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.PercentLayout_Layout)
            var value = array.getFraction(
                R.styleable.PercentLayout_Layout_layout_widthPercent, 1, 1,
                -1f
            )
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent width: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.widthPercent = value
            }
            value =
                array.getFraction(R.styleable.PercentLayout_Layout_layout_heightPercent, 1, 1, -1f)
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent height: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.heightPercent = value
            }
            value =
                array.getFraction(R.styleable.PercentLayout_Layout_layout_marginPercent, 1, 1, -1f)
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent margin: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.leftMarginPercent = value
                info.topMarginPercent = value
                info.rightMarginPercent = value
                info.bottomMarginPercent = value
            }
            value = array.getFraction(
                R.styleable.PercentLayout_Layout_layout_marginLeftPercent, 1, 1,
                -1f
            )
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent left margin: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.leftMarginPercent = value
            }
            value = array.getFraction(
                R.styleable.PercentLayout_Layout_layout_marginTopPercent, 1, 1,
                -1f
            )
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent top margin: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.topMarginPercent = value
            }
            value = array.getFraction(
                R.styleable.PercentLayout_Layout_layout_marginRightPercent, 1, 1,
                -1f
            )
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent right margin: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.rightMarginPercent = value
            }
            value = array.getFraction(
                R.styleable.PercentLayout_Layout_layout_marginBottomPercent, 1, 1,
                -1f
            )
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent bottom margin: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.bottomMarginPercent = value
            }
            value = array.getFraction(
                R.styleable.PercentLayout_Layout_layout_marginStartPercent, 1, 1,
                -1f
            )
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent start margin: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.startMarginPercent = value
            }
            value = array.getFraction(
                R.styleable.PercentLayout_Layout_layout_marginEndPercent, 1, 1,
                -1f
            )
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "percent end margin: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.endMarginPercent = value
            }
            value =
                array.getFraction(R.styleable.PercentLayout_Layout_layout_aspectRatio, 1, 1, -1f)
            if (value != -1f) {
                if (VERBOSE) {
                    Log.v(TAG, "aspect ratio: $value")
                }
                info = info ?: PercentLayoutInfo()
                info.aspectRatio = value
            }
            array.recycle()
            if (DEBUG) {
                Log.d(TAG, "constructed: $info")
            }
            return info
        }

        private fun shouldHandleMeasuredWidthTooSmall(
            view: View,
            info: PercentLayoutInfo
        ): Boolean {
            val state: Int = view.getMeasuredWidthAndState() and View.MEASURED_STATE_MASK
            return (state == View.MEASURED_STATE_TOO_SMALL) && (info.widthPercent >= 0
                    ) && (info.mPreservedParams.width == ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        private fun shouldHandleMeasuredHeightTooSmall(
            view: View,
            info: PercentLayoutInfo
        ): Boolean {
            val state: Int = view.getMeasuredHeightAndState() and View.MEASURED_STATE_MASK
            return (state == View.MEASURED_STATE_TOO_SMALL) && (info.heightPercent >= 0
                    ) && (info.mPreservedParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    init {
        if (host == null) {
            throw IllegalArgumentException("host must be non-null")
        }
        mHost = host
    }
}