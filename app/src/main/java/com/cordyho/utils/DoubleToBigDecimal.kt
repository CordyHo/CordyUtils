package com.cordyho.utils

import java.math.BigDecimal
import java.math.RoundingMode

object DoubleToBigDecimal {

    fun double2Str(value: Double?): String {
        return value?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble().toString() }
                ?: "0"
    }
}