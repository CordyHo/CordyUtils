package com.cordyho.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

@SuppressLint("ClickableViewAccessibility")
class SwipeEnableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var isSwipeEnabled = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isSwipeEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isSwipeEnabled && super.onInterceptTouchEvent(event)
    }

    fun setSwipeEnabled(b: Boolean) {
        isSwipeEnabled = b
    }
}