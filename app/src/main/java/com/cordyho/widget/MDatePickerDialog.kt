package com.cordyho.widget

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class MDatePickerDialog(private val context: Context, private val listener: MOnDateSetListener) {

    init {
        show() //直接实例化，不需要运行show()方法
    }

    private fun show(minDate: Long = 0, maxDate: Long = 0) {
        val dialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { picker, _, _, _ ->
            listener.onDatePick("${picker.year}-${picker.month + 1}-${picker.dayOfMonth}")
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE)) //设置初始时间为今天
        if (minDate >= 0)
            dialog.datePicker.minDate = minDate
        if (maxDate >= 0)
            dialog.datePicker.maxDate = maxDate
        dialog.show()
    }

    interface MOnDateSetListener {
        fun onDatePick(result: String)
    }
}