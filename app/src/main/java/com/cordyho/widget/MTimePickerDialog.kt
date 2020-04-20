package com.cordyho.widget

import android.app.TimePickerDialog
import android.content.Context
import java.util.*

class MTimePickerDialog(private val context: Context, private val listener: MOnTimeSetListener) {

    init {
        show() //直接实例化，不需要运行show()方法
    }

    private fun show() {
        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener
        { _, p1, p2 ->
            val min = if (p1 <= 9)
                "0$p1"
            else
                "$p1"
            val sec = if (p2 <= 9)
                "0$p2"
            else
                "$p2"
            listener.onTimePick("$min:$sec")
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true)
                .show()
    }

    interface MOnTimeSetListener {
        fun onTimePick(result: String)
    }
}