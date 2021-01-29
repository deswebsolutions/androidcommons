package com.commons.heipers.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.util.Pools
import com.commons.R
import com.commons.utilities.extensions.Randoms

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.concurrent.TimeUnit

class EmojiRainLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    PercentFrameLayout(context, attrs, defStyleAttr) {
    private var mSubscriptions: CompositeSubscription? = null
    private var mWindowHeight = 0
    private var mEmojiPer = 0
    private var mDuration = 0
    private var mDropAverageDuration = 0
    private var mDropFrequency = 0
    private var mEmojis: MutableList<Drawable?>? = null
    private var mEmojiPool: Pools.SynchronizedPool<ImageView?>? = null
    fun setPer(per: Int) {
        mEmojiPer = per
    }

    fun setDuration(duration: Int) {
        mDuration = duration
    }

    fun setDropDuration(dropDuration: Int) {
        mDropAverageDuration = dropDuration
    }

    fun setDropFrequency(frequency: Int) {
        mDropFrequency = frequency
    }

    fun addEmoji(emoji: Bitmap?) {
        mEmojis!!.add(BitmapDrawable(resources, emoji))
    }

    fun addEmoji(emoji: Drawable?) {
        mEmojis!!.add(emoji)
    }

    fun addEmoji(@DrawableRes resId: Int) {
        mEmojis?.apply {
            clear()
            add(ContextCompat.getDrawable(context, resId))
        }
    }

    fun clearEmojis() {
        mEmojis!!.clear()
    }

    /**
     * Stop dropping animation after all emojis in the screen currently
     * dropping out of the screen.
     */
    fun stopDropping() {
        mDuration=0
        mSubscriptions?.clear()
    }

    /**
     * Start dropping animation.
     * The animation will last for n flow(s), which n is `mDuration`
     * divided by `mDropFrequency`.
     * The interval between two flows is `mDropFrequency`.
     * There will be `mEmojiPer` emojis dropping in each flow.
     * The dropping animation for a specific emoji is a random value with mean
     * `mDropAverageDuration` and relative offset `RELATIVE_DROP_DURATION_OFFSET`.
     */
    fun startDropping() {
        initEmojisPool()
        Randoms.setSeed(1)
        mWindowHeight = windowHeight
        val subscription = Observable.interval(mDropFrequency.toLong(), TimeUnit.MILLISECONDS)
            .take(mDuration / mDropFrequency)
            .flatMap {
                Observable.range(
                    0,
                    mEmojiPer
                )
            }
            .map{ i: Int? -> mEmojiPool!!.acquire() }
            .filter { it != null }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { startDropAnimationForSingleEmoji(it) }
            ) { obj: Throwable -> obj.printStackTrace() }
        mSubscriptions?.add(subscription)

    }
    private fun init(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.EmojiRainLayout)
        mEmojis = ArrayList()
        mEmojiPer = ta.getInteger(R.styleable.EmojiRainLayout_per, DEFAULT_PER)
        mDuration = ta.getInteger(R.styleable.EmojiRainLayout_duration, DEFAULT_DURATION)
        mDropAverageDuration = ta.getInteger(
            R.styleable.EmojiRainLayout_dropDuration,
            DEFAULT_DROP_DURATION
        )
        mDropFrequency = ta.getInteger(
            R.styleable.EmojiRainLayout_dropFrequency,
            DEFAULT_DROP_FREQUENCY
        )
        ta.recycle()
    }

    private fun startDropAnimationForSingleEmoji(emoji: ImageView?) {
        val translateAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0F,
            Animation.RELATIVE_TO_SELF, Randoms.floatAround(0, 5F),
            Animation.RELATIVE_TO_PARENT, 0F,
            Animation.ABSOLUTE, mWindowHeight.toFloat()
        )
        translateAnimation.duration = (mDropAverageDuration * Randoms.floatAround(
            1,
            RELATIVE_DROP_DURATION_OFFSET
        )).toLong()
        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                mEmojiPool?.release(emoji!!)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        emoji?.startAnimation(translateAnimation)
    }

    private val windowHeight: Int
        get() {
            val windowManager = context.applicationContext
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            return point.y
        }

    private fun initEmojisPool() {
        val emojiTypeCount = mEmojis!!.size
        check(emojiTypeCount != 0) { "There are no emojis" }
        clearDirtyEmojisInPool()
        val expectedMaxEmojiCountInScreen = (((1 + RELATIVE_DROP_DURATION_OFFSET)
                * mEmojiPer
                * mDropAverageDuration)
                / mDropFrequency.toFloat()).toInt()
        mEmojiPool = Pools.SynchronizedPool(expectedMaxEmojiCountInScreen)
        for (i in 0 until expectedMaxEmojiCountInScreen) {
            val emoji = generateEmoji(mEmojis!![i % emojiTypeCount])
            addView(emoji, 0)
            mEmojiPool?.release(emoji)
        }

    }

    private fun generateEmoji(emojiDrawable: Drawable?): ImageView {
        val emoji = ImageView(context)
        emoji.setImageDrawable(emojiDrawable)
        val width = (EMOJI_STANDARD_SIZE * (1.0 + Randoms.positiveGaussian())).toInt()
        val height = (EMOJI_STANDARD_SIZE * (1.0 + Randoms.positiveGaussian())).toInt()
        val params = LayoutParams(width, height)
        params.percentLayoutInfo!!.leftMarginPercent = Randoms.floatStandard()
        params.topMargin = -height
        params.leftMargin = (-0.5f * width).toInt()
        emoji.layoutParams = params
        emoji.elevation = 100f
        return emoji
    }

    private fun clearDirtyEmojisInPool() {
        if (mEmojiPool != null) {
            var dirtyEmoji: ImageView?
            while (mEmojiPool!!.acquire().also { dirtyEmoji = it } != null) removeView(dirtyEmoji)
        }
    }

    private fun dip2px(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }

    companion object {
        private var EMOJI_STANDARD_SIZE = 0
        private const val RELATIVE_DROP_DURATION_OFFSET = 0.25f
        private const val DEFAULT_PER = 6
        private const val DEFAULT_DURATION = 8000
        private const val DEFAULT_DROP_DURATION = 2400
        private const val DEFAULT_DROP_FREQUENCY = 500
    }

    init {
        EMOJI_STANDARD_SIZE = dip2px(36f)
    }

    init {
        if (!isInEditMode) init(context, attrs)
    }
}
