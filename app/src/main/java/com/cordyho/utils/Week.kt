package com.cordyho.utils

/**
 *
 * 星期
 *
 * @author 段
 */
enum class Week(var chineseName: String, var engName: String, var shortName: String, var number: Int) {
    MONDAY("星期一", "Monday", "Mon.", 1), TUESDAY("星期二", "Tuesday", "Tues.", 2), WEDNESDAY(
            "星期三", "Wednesday", "Wed.", 3),
    THURSDAY("星期四", "Thursday",
            "Thur.", 4),
    FRIDAY("星期五", "Friday", "Fri.", 5), SATURDAY("星期六",
            "Saturday", "Sat.", 6),
    SUNDAY("星期日", "Sunday", "Sun.", 7);

}