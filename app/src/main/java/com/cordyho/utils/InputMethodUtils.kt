package com.cordyho.utils

import android.content.Context
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object InputMethodUtils {

    fun showInputMethod(editText: EditText) {
        val inputManager = CordyUtils.application?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    fun closeInputMethod(editText: EditText) {
        val imm = CordyUtils.application?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
    }

    fun showInputMethodForDialog(editText: EditText?) {
        Handler().postDelayed({
            val inputManager = CordyUtils.application?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputManager?.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
        }, 100)
    }
}