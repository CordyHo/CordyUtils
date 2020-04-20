package com.cordyho.utils

import android.view.Gravity
import android.widget.Toast

object ToastUtil {

    private var oldMsg: Any? = null
    private var time: Long = 0

    fun showToast(msg: Any?, shortOrLong: Int = 0) {
        if (msg != oldMsg) {
            Toast.makeText(CordyUtils.application, msg?.toString(), shortOrLong).show()
            time = System.currentTimeMillis()
        } else {
            if (System.currentTimeMillis() - time > 1500) {
                Toast.makeText(CordyUtils.application, msg?.toString(), shortOrLong).show()
                time = System.currentTimeMillis()
            }
        }
        oldMsg = msg
    }

    fun showToastCenter(msg: Any?, shortOrLong: Int = 0) {
        val toast = Toast.makeText(CordyUtils.application, msg?.toString(), shortOrLong)
        toast.setGravity(Gravity.CENTER, 0, 0)
        if (msg != oldMsg) {
            toast.show()
            time = System.currentTimeMillis()
        } else {
            if (System.currentTimeMillis() - time > 1500) {
                toast.show()
                time = System.currentTimeMillis()
            }
        }
        oldMsg = msg
    }
}