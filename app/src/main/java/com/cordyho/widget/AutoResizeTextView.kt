package com.cordyho.widget


import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.RectF
import android.os.Build
import android.text.Layout.Alignment
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.SparseIntArray
import android.util.TypedValue

class AutoResizeTextView : androidx.appcompat.widget.AppCompatTextView {

    private val mTextRect = RectF()

    private var mAvailableSpaceRect: RectF? = null

    private var mTextCachedSizes: SparseIntArray? = null

    private var mPaint: TextPaint? = null

    private var mMaxTextSize: Float = 0.toFloat()

    private var mSpacingMult = 1.0f

    private var mSpacingAdd = 0.0f

    private var mMinTextSize = 20f

    private var mWidthLimit: Int = 0
    private var mMaxLines: Int = 0

    private var mEnableSizeCache = true
    private var mInitializedDimens: Boolean = false

    private val mSizeTester = object : SizeTester {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int {
            mPaint!!.textSize = suggestedSize.toFloat()
            val text = text.toString()
            val singleline = maxLines == 1
            if (singleline) {
                mTextRect.bottom = mPaint!!.fontSpacing
                mTextRect.right = mPaint!!.measureText(text)
            } else {
                val layout = StaticLayout(
                    text, mPaint,
                    mWidthLimit, Alignment.ALIGN_NORMAL, mSpacingMult,
                    mSpacingAdd, true
                )

                if (maxLines != NO_LINE_LIMIT && layout.lineCount > maxLines) {
                    return 1
                }
                mTextRect.bottom = layout.height.toFloat()
                var maxWidth = -1
                for (i in 0 until layout.lineCount) {
                    if (maxWidth < layout.getLineWidth(i)) {
                        maxWidth = layout.getLineWidth(i).toInt()
                    }
                }
                mTextRect.right = maxWidth.toFloat()
            }

            mTextRect.offsetTo(0f, 0f)
            return if (availableSpace.contains(mTextRect)) {
                -1
            } else {
                1
            }
        }
    }

    private interface SizeTester {
        fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int
    }

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initialize()
    }

    private fun initialize() {
        mPaint = TextPaint(paint)
        mMaxTextSize = textSize
        mAvailableSpaceRect = RectF()
        mTextCachedSizes = SparseIntArray()
        if (mMaxLines == 0) {
            mMaxLines = NO_LINE_LIMIT
        }
    }

    override fun setTextSize(size: Float) {
        mMaxTextSize = size
        mTextCachedSizes!!.clear()
        adjustTextSize()
    }

    override fun setMaxLines(maxlines: Int) {
        super.setMaxLines(maxlines)
        mMaxLines = maxlines
        adjustTextSize()
    }

    override fun getMaxLines(): Int {
        return mMaxLines
    }

    override fun setSingleLine() {
        super.setSingleLine()
        mMaxLines = 1
        adjustTextSize()
    }

    override fun setSingleLine(singleLine: Boolean) {
        super.setSingleLine(singleLine)
        mMaxLines = if (singleLine) {
            1
        } else {
            NO_LINE_LIMIT
        }
        adjustTextSize()
    }

    override fun setLines(lines: Int) {
        super.setLines(lines)
        mMaxLines = lines
        adjustTextSize()
    }

    override fun setTextSize(unit: Int, size: Float) {
        val c = context
        val r: Resources

        r = if (c == null)
            Resources.getSystem()
        else
            c.resources
        mMaxTextSize = TypedValue.applyDimension(
            unit, size,
            r.displayMetrics
        )
        mTextCachedSizes!!.clear()
        adjustTextSize()
    }

    override fun setLineSpacing(add: Float, mult: Float) {
        super.setLineSpacing(add, mult)
        mSpacingMult = mult
        mSpacingAdd = add
    }

    fun setMinTextSize(minTextSize: Float) {
        mMinTextSize = minTextSize
        adjustTextSize()
    }

    private fun adjustTextSize() {
        if (!mInitializedDimens) {
            return
        }
        val startSize = mMinTextSize.toInt()
        val heightLimit = (measuredHeight - compoundPaddingBottom
                - compoundPaddingTop)
        mWidthLimit = (measuredWidth - compoundPaddingLeft
                - compoundPaddingRight)
        mAvailableSpaceRect!!.right = mWidthLimit.toFloat()
        mAvailableSpaceRect!!.bottom = heightLimit.toFloat()
        super.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            efficientTextSizeSearch(
                startSize, mMaxTextSize.toInt(),
                mSizeTester, mAvailableSpaceRect!!
            ).toFloat()
        )
    }

    fun enableSizeCache(enable: Boolean) {
        mEnableSizeCache = enable
        mTextCachedSizes!!.clear()
        adjustTextSize()
    }

    private fun efficientTextSizeSearch(
        start: Int, end: Int,
        sizeTester: SizeTester, availableSpace: RectF
    ): Int {
        if (!mEnableSizeCache) {
            return binarySearch(start, end, sizeTester, availableSpace)
        }
        val key = text.toString().length
        var size = mTextCachedSizes!!.get(key)
        if (size != 0) {
            return size
        }
        size = binarySearch(start, end, sizeTester, availableSpace)
        mTextCachedSizes!!.put(key, size)
        return size
    }

    override fun onTextChanged(
        text: CharSequence, start: Int,
        before: Int, after: Int
    ) {
        super.onTextChanged(text, start, before, after)
        adjustTextSize()
    }

    override fun onSizeChanged(
        width: Int, height: Int, oldwidth: Int,
        oldheight: Int
    ) {
        mInitializedDimens = true
        mTextCachedSizes!!.clear()
        super.onSizeChanged(width, height, oldwidth, oldheight)
        if (width != oldwidth || height != oldheight) {
            adjustTextSize()
        }
    }

    companion object {

        private const val NO_LINE_LIMIT = -1

        private fun binarySearch(
            start: Int, end: Int, sizeTester: SizeTester,
            availableSpace: RectF
        ): Int {
            var lastBest = start
            var lo = start
            var hi = end - 1
            var mid: Int
            while (lo <= hi) {
                mid = (lo + hi).ushr(1)
                val midValCmp = sizeTester.onTestSize(mid, availableSpace)
                when {
                    midValCmp < 0 -> {
                        lastBest = lo
                        lo = mid + 1
                    }
                    midValCmp > 0 -> {
                        hi = mid - 1
                        lastBest = hi
                    }
                    else -> return mid
                }
            }
            return lastBest
        }
    }
}