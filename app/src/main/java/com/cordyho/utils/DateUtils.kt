package com.cordyho.utils

import android.annotation.SuppressLint
import android.util.Log
import com.cordyho.utils.DateStyle.Companion.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class DateUtils {

    companion object {
        private val weekDays = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

        fun getWeekOfDate(year: Int, month: Int, monthDay: Int): String {  //得到某日星期几
            val cal = Calendar.getInstance()
            cal.clear()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month - 1)  //Calendar对象默认一月为0
            cal.set(Calendar.DAY_OF_MONTH, monthDay)
            val w = cal.get(Calendar.DAY_OF_WEEK) - 1
            return weekDays[w]
        }

        fun getLengthOfMonth(year: Int, whichMonth: Int): Int {  // 得到某个月的天数
            val cal = Calendar.getInstance()
            cal.clear()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, whichMonth - 1)  //Calendar对象默认一月为0
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH)  //得到当月实际天数
        }

        fun formatTimeStamp(dataFormat: String, timeStamp: Long): String {
            var stamp = timeStamp
            if (stamp == 0L) {
                return ""
            }
            stamp *= 1000
            var result = ""
            val format = SimpleDateFormat(dataFormat)
            result = format.format(Date(stamp))
            return result
        }
    }

    private val threadLocal = ThreadLocal<SimpleDateFormat>()

    private val `object` = Any()

    /**
     * 获取SimpleDateFormat
     *
     * @param pattern
     * 日期格式
     * @return SimpleDateFormat对象
     * @throws RuntimeException
     * 异常：非法日期格式
     */
    @Throws(RuntimeException::class)
    private fun getDateFormat(pattern: String): SimpleDateFormat {
        var dateFormat: SimpleDateFormat? = threadLocal.get()
        if (dateFormat == null) {
            synchronized(`object`) {
                if (dateFormat == null) {
                    dateFormat = SimpleDateFormat(pattern)
                    dateFormat!!.isLenient = false
                    threadLocal.set(dateFormat)
                }
            }
        }
        dateFormat!!.applyPattern(pattern)
        return dateFormat as SimpleDateFormat
    }

    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date
     * 日期
     * @param dateType
     * 日期格式
     * @return 数值
     */
    private fun getInteger(date: Date?, dateType: Int): Int {
        var num = 0
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
            num = calendar.get(dateType)
        }
        return num
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date
     * 日期字符串
     * @param dateType
     * 类型
     * @param amount
     * 数值
     * @return 计算后日期字符串
     */
    private fun addInteger(date: String, dateType: Int, amount: Int): String? {
        var dateString: String? = null
        val dateStyle = getDateStyle(date)
        if (dateStyle != null) {
            var myDate = StringToDate(date, dateStyle)
            myDate = addInteger(myDate, dateType, amount)
            dateString = DateToString(myDate, dateStyle)
        }
        return dateString
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date
     * 日期
     * @param dateType
     * 类型
     * @param amount
     * 数值
     * @return 计算后日期
     */
    private fun addInteger(date: Date?, dateType: Int, amount: Int): Date? {
        var myDate: Date? = null
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(dateType, amount)
            myDate = calendar.time
        }
        return myDate
    }

    /**
     * 获取精确的日期
     *
     * @param timestamps
     * 时间long集合
     * @return 日期
     */
    private fun getAccurateDate(timestamps: List<Long>?): Date? {
        var date: Date? = null
        var timestamp: Long = 0
        val map = HashMap<Long, LongArray>()
        val absoluteValues = ArrayList<Long>()

        if (timestamps != null && timestamps.size > 0) {
            if (timestamps.size > 1) {
                for (i in timestamps.indices) {
                    for (j in i + 1 until timestamps.size) {
                        val absoluteValue = Math.abs(timestamps[i] - timestamps[j])
                        absoluteValues.add(absoluteValue)
                        val timestampTmp = longArrayOf(timestamps[i], timestamps[j])
                        map[absoluteValue] = timestampTmp
                    }
                }

                // 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的。此时minAbsoluteValue为0
                // 因此不能将minAbsoluteValue取默认值0
                var minAbsoluteValue: Long = -1
                if (!absoluteValues.isEmpty()) {
                    minAbsoluteValue = absoluteValues[0]
                    for (i in 1 until absoluteValues.size) {
                        if (minAbsoluteValue > absoluteValues[i]) {
                            minAbsoluteValue = absoluteValues[i]
                        }
                    }
                }

                if (minAbsoluteValue.toInt() != -1) {
                    val timestampsLastTmp = map[minAbsoluteValue]

                    val dateOne = timestampsLastTmp!![0]
                    val dateTwo = timestampsLastTmp[1]
                    if (absoluteValues.size > 1) {
                        timestamp = if (Math.abs(dateOne) > Math.abs(dateTwo))
                            dateOne
                        else
                            dateTwo
                    }
                }
            } else {
                timestamp = timestamps[0]
            }
        }

        if (timestamp != 0L) {
            date = Date(timestamp)
        }
        return date
    }

    /**
     * 判断字符串是否为日期字符串
     *
     * @param date
     * 日期字符串
     * @return true or false
     */
    fun isDate(date: String?): Boolean {
        var isDate = false
        if (date != null) {
            if (getDateStyle(date) != null) {
                isDate = true
            }
        }
        return isDate
    }

    /**
     * 获取日期字符串的日期风格。失敗返回null。
     *
     * @param date
     * 日期字符串
     * @return 日期风格
     */
    fun getDateStyle(date: String?): DateStyle? {
        var dateStyle: DateStyle? = null
        val map = HashMap<Long, DateStyle>()
        val timestamps = ArrayList<Long>()
        for (style in DateStyle.values()) {
            if (style.isShowOnly) {
                continue
            }
            var dateTmp: Date? = null
            if (date != null) {
                try {
                    val pos = ParsePosition(0)
                    dateTmp = getDateFormat(style.value).parse(date, pos)
                    if (pos.index !== date.length) {
                        dateTmp = null
                    }
                } catch (e: Exception) {
                }

            }
            if (dateTmp != null) {
                timestamps.add(dateTmp.time)
                map[dateTmp.time] = style
            }
        }
        val accurateDate = getAccurateDate(timestamps)
        if (accurateDate != null) {
            dateStyle = map[accurateDate.time]
        }
        return dateStyle
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date
     * 日期字符串
     * @return 日期
     */
    fun StringToDate(date: String): Date? {
        val dateStyle = getDateStyle(date)
        return StringToDate(date, dateStyle)
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date
     * 日期字符串
     * @param pattern
     * 日期格式
     * @return 日期
     */
    fun StringToDate(date: String?, pattern: String): Date? {
        var myDate: Date? = null
        if (date != null) {
            try {
                myDate = getDateFormat(pattern).parse(date)
            } catch (e: Exception) {
            }

        }
        return myDate
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date
     * 日期字符串
     * @param dateStyle
     * 日期风格
     * @return 日期
     */
    fun StringToDate(date: String, dateStyle: DateStyle?): Date? {
        var myDate: Date? = null
        if (dateStyle != null) {
            myDate = StringToDate(date, dateStyle.value)
        }
        return myDate
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date
     * 日期
     * @param pattern
     * 日期格式
     * @return 日期字符串
     */
    fun DateToString(date: Date?, pattern: String): String? {
        var dateString: String? = null
        if (date != null) {
            try {
                dateString = getDateFormat(pattern).format(date)
            } catch (e: Exception) {
            }

        }
        return dateString
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date
     * 日期
     * @param dateStyle
     * 日期风格
     * @return 日期字符串
     */
    fun DateToString(date: Date?, dateStyle: DateStyle?): String? {
        var dateString: String? = null
        if (dateStyle != null) {
            dateString = DateToString(date, dateStyle.value)
        }
        return dateString
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date
     * 旧日期字符串
     * @param newPattern
     * 新日期格式
     * @return 新日期字符串
     */
    fun StringToString(date: String, newPattern: String): String? {
        val oldDateStyle = getDateStyle(date)
        return StringToString(date, oldDateStyle, newPattern)
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date
     * 旧日期字符串
     * @param newDateStyle
     * 新日期风格
     * @return 新日期字符串
     */
    fun StringToString(date: String, newDateStyle: DateStyle): String? {
        val oldDateStyle = getDateStyle(date)
        return StringToString(date, oldDateStyle, newDateStyle)
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date
     * 旧日期字符串
     * @param olddPattern
     * 旧日期格式
     * @param newPattern
     * 新日期格式
     * @return 新日期字符串
     */
    fun StringToString(date: String, olddPattern: String,
                       newPattern: String): String? {
        return DateToString(StringToDate(date, olddPattern), newPattern)
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date
     * 旧日期字符串
     * @param olddDteStyle
     * 旧日期风格
     * @param newParttern
     * 新日期格式
     * @return 新日期字符串
     */
    fun StringToString(date: String, olddDteStyle: DateStyle?,
                       newParttern: String): String? {
        var dateString: String? = null
        if (olddDteStyle != null) {
            dateString = StringToString(date, olddDteStyle.value,
                    newParttern)
        }
        return dateString
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date
     * 旧日期字符串
     * @param olddPattern
     * 旧日期格式
     * @param newDateStyle
     * 新日期风格
     * @return 新日期字符串
     */
    fun StringToString(date: String, olddPattern: String,
                       newDateStyle: DateStyle?): String? {
        var dateString: String? = null
        if (newDateStyle != null) {
            dateString = StringToString(date, olddPattern,
                    newDateStyle.value)
        }
        return dateString
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date
     * 旧日期字符串
     * @param olddDteStyle
     * 旧日期风格
     * @param newDateStyle
     * 新日期风格
     * @return 新日期字符串
     */
    fun StringToString(date: String, olddDteStyle: DateStyle?,
                       newDateStyle: DateStyle?): String? {
        var dateString: String? = null
        if (olddDteStyle != null && newDateStyle != null) {
            dateString = StringToString(date, olddDteStyle.value,
                    newDateStyle.value)
        }
        return dateString
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date
     * 日期
     * @param yearAmount
     * 增加数量。可为负数
     * @return 增加年份后的日期字符串
     */
    fun addYear(date: String, yearAmount: Int): String {
        return this.addInteger(date, Calendar.YEAR, yearAmount)!!
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date
     * 日期
     * @param yearAmount
     * 增加数量。可为负数
     * @return 增加年份后的日期
     */
    fun addYear(date: Date, yearAmount: Int): Date {
        return this.addInteger(date, Calendar.YEAR, yearAmount)!!
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date
     * 日期
     * @param monthAmount
     * 增加数量。可为负数
     * @return 增加月份后的日期字符串
     */
    fun addMonth(date: String, monthAmount: Int): String {
        return this.addInteger(date, Calendar.MONTH, monthAmount)!!
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date
     * 日期
     * @param monthAmount
     * 增加数量。可为负数
     * @return 增加月份后的日期
     */
    fun addMonth(date: Date, monthAmount: Int): Date {
        return this.addInteger(date, Calendar.MONTH, monthAmount)!!
    }

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date
     * 日期字符串
     * @param dayAmount
     * 增加数量。可为负数
     * @return 增加天数后的日期字符串
     */
    fun addDay(date: String, dayAmount: Int): String {
        return this.addInteger(date, Calendar.DATE, dayAmount)!!
    }

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date
     * 日期
     * @param dayAmount
     * 增加数量。可为负数
     * @return 增加天数后的日期
     */
    fun addDay(date: Date, dayAmount: Int): Date {
        return this.addInteger(date, Calendar.DATE, dayAmount)!!
    }

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date
     * 日期字符串
     * @param hourAmount
     * 增加数量。可为负数
     * @return 增加小时后的日期字符串
     */
    fun addHour(date: String, hourAmount: Int): String {
        return this.addInteger(date, Calendar.HOUR_OF_DAY, hourAmount)!!
    }

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date
     * 日期
     * @param hourAmount
     * 增加数量。可为负数
     * @return 增加小时后的日期
     */
    fun addHour(date: Date, hourAmount: Int): Date {
        return this.addInteger(date, Calendar.HOUR_OF_DAY, hourAmount)!!
    }

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date
     * 日期字符串
     * @param minuteAmount
     * 增加数量。可为负数
     * @return 增加分钟后的日期字符串
     */
    fun addMinute(date: String, minuteAmount: Int): String {
        return this.addInteger(date, Calendar.MINUTE, minuteAmount)!!
    }

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date
     * 日期
     * @param dayAmount
     * 增加数量。可为负数
     * @return 增加分钟后的日期
     */
    fun addMinute(date: Date, minuteAmount: Int): Date {
        return this.addInteger(date, Calendar.MINUTE, minuteAmount)!!
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date
     * 日期字符串
     * @param dayAmount
     * 增加数量。可为负数
     * @return 增加秒钟后的日期字符串
     */
    fun addSecond(date: String, secondAmount: Int): String {
        return this.addInteger(date, Calendar.SECOND, secondAmount)!!
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date
     * 日期
     * @param dayAmount
     * 增加数量。可为负数
     * @return 增加秒钟后的日期
     */
    fun addSecond(date: Date, secondAmount: Int): Date {
        return this.addInteger(date, Calendar.SECOND, secondAmount)!!
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date
     * 日期字符串
     * @return 年份
     */
    fun getYear(date: String): Int {
        return getYear(StringToDate(date))
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date
     * 日期
     * @return 年份
     */
    fun getYear(date: Date?): Int {
        return getInteger(date, Calendar.YEAR)
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date
     * 日期字符串
     * @return 月份
     */
    fun getMonth(date: String): Int {
        return getMonth(StringToDate(date))
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date
     * 日期
     * @return 月份
     */
    fun getMonth(date: Date?): Int {
        return getInteger(date, Calendar.MONTH) + 1
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date
     * 日期字符串
     * @return 天
     */
    fun getDay(date: String): Int {
        return getDay(StringToDate(date))
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date
     * 日期
     * @return 天
     */
    fun getDay(date: Date?): Int {
        return getInteger(date, Calendar.DATE)
    }

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date
     * 日期字符串
     * @return 小时
     */
    fun getHour(date: String): Int {
        return getHour(StringToDate(date))
    }

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date
     * 日期
     * @return 小时
     */
    fun getHour(date: Date?): Int {
        return getInteger(date, Calendar.HOUR_OF_DAY)
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date
     * 日期字符串
     * @return 分钟
     */
    fun getMinute(date: String): Int {
        return getMinute(StringToDate(date))
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date
     * 日期
     * @return 分钟
     */
    fun getMinute(date: Date?): Int {
        return getInteger(date, Calendar.MINUTE)
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date
     * 日期字符串
     * @return 秒钟
     */
    fun getSecond(date: String): Int {
        return getSecond(StringToDate(date))
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date
     * 日期
     * @return 秒钟
     */
    fun getSecond(date: Date?): Int {
        return getInteger(date, Calendar.SECOND)
    }

    /**
     * 获取日期 。默认yyyy-MM-dd格式。失败返回null。
     *
     * @param date
     * 日期字符串
     * @return 日期
     */
    fun getDate(date: String): String? {
        return StringToString(date, DateStyle.YYYY_MM_DD)
    }

    /**
     * 获取日期。默认yyyy-MM-dd格式。失败返回null。
     *
     * @param date
     * 日期
     * @return 日期
     */
    fun getDate(date: Date): String? {
        return DateToString(date, DateStyle.YYYY_MM_DD)
    }

    /**
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
     *
     * @param date
     * 日期字符串
     * @return 时间
     */
    fun getTime(date: String): String? {
        return StringToString(date, DateStyle.HH_MM_SS)
    }

    /**
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
     *
     * @param date
     * 日期
     * @return 时间
     */
    fun getTime(date: Date): String? {
        return DateToString(date, DateStyle.HH_MM_SS)
    }

    /**
     * 获取日期的时间。默认yyyy-MM-dd HH:mm:ss格式。失败返回null。
     *
     * @param date
     * 日期字符串
     * @return 时间
     */
    fun getDateTime(date: String): String? {
        return StringToString(date, DateStyle.YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * 获取日期的时间。默认yyyy-MM-dd HH:mm:ss格式。失败返回null。
     *
     * @param date
     * 日期
     * @return 时间
     */
    fun getDateTime(date: Date): String? {
        return DateToString(date, DateStyle.YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date
     * 日期字符串
     * @return 星期
     */
    fun getWeek(date: String): Week? {
        var week: Week? = null
        val dateStyle = getDateStyle(date)
        if (dateStyle != null) {
            val myDate = StringToDate(date, dateStyle)
            week = getWeek(myDate)
        }
        return week
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date
     * 日期
     * @return 星期
     */
    fun getWeek(date: Date?): Week? {
        var week: Week? = null
        val calendar = Calendar.getInstance()
        calendar.time = date
        val weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1
        when (weekNumber) {
            0 -> week = Week.SUNDAY
            1 -> week = Week.MONDAY
            2 -> week = Week.TUESDAY
            3 -> week = Week.WEDNESDAY
            4 -> week = Week.THURSDAY
            5 -> week = Week.FRIDAY
            6 -> week = Week.SATURDAY
        }
        return week
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param date
     * 日期字符串
     * @param otherDate
     * 另一个日期字符串
     * @return 相差天数。如果失败则返回-1
     */
    fun getIntervalDays(date: String, otherDate: String): Int {
        return getIntervalDays(StringToDate(date), StringToDate(otherDate))
    }

    /**
     * @param date
     * 日期
     * @param otherDate
     * 另一个日期
     * @return 相差天数。如果失败则返回-1
     */
    fun getIntervalDays(date: Date?, otherDate: Date?): Int {
        var num = -1
        val dateTmp = StringToDate(this.getDate(date!!)!!,
                DateStyle.YYYY_MM_DD)
        val otherDateTmp = StringToDate(this.getDate(otherDate!!)!!,
                DateStyle.YYYY_MM_DD)
        if (dateTmp != null && otherDateTmp != null) {
            val time = Math.abs(dateTmp.time - otherDateTmp.time)
            num = (time / (24 * 60 * 60 * 1000)).toInt()
        }
        return num
    }

    /**
     * 获取期间的年龄
     *
     * @param date
     * @param otherDate
     * @return
     *
     * 2014-12-2 下午06:45:02 段
     *
     * @return String
     */
    fun getAge(date: Date, otherDate: Date): String {
        val dis = getIntervalDays(Date(), otherDate)
        val year = dis / 365
        val month = dis % 365 / 30
        val day = dis % 365 % 31
        return ((if (year > 0) year.toString() + "岁" else "")
                + (if (month > 0) month.toString() + "个月" else "") + (day.toString() + "天"))
    }

    //时间戳转换日期格式字符串
    fun timeStamp2Date(time: Long, format: String?): String {
        var format = format
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(time * 1000))
    }

    //日期格式字符串转换时间戳
    fun date2TimeStamp(date: String, format: String): String {
        try {
            val sdf = SimpleDateFormat(format)
            return (sdf.parse(date).time / 1000).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getDateToString(milSecond: Long): Date {
        val pattern: String
        pattern = DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS
        val format = SimpleDateFormat(pattern)
        val d = format.format(milSecond)
        val date = format.parse(d)
        return date
    }

    //Date对象获取时间字符串
    fun getDateStr(date: Date, format: String?): String {
        var format = format
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val formatter = SimpleDateFormat(format)
        return formatter.format(date)
    }

    //日期字符串转换Date实体
    fun parseServerTime(serverTime: String, format: String?): Date? {
        var format = format
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val sdf = SimpleDateFormat(format, Locale.CHINESE)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8:00")
        var date: Date? = null
        try {
            date = sdf.parse(serverTime)
        } catch (e: Exception) {
            Log.e(e.toString(), "")
        }

        return date
    }

    /**
     * 获得两个时间相差距离多少天多少小时多少分多少秒
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    fun getDistanceTime(one: Date, two: Date): LongArray {
        var day: Long = 0
        var hour: Long = 0
        var min: Long = 0
        var sec: Long = 0
        try {

            val time1 = one.time
            val time2 = two.time
            val diff: Long
            if (time1 < time2) {
                diff = time2 - time1
            } else {
                diff = time1 - time2
            }
            day = diff / (24 * 60 * 60 * 1000)
            hour = diff / (60 * 60 * 1000) - day * 24
            min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
            sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return longArrayOf(day, hour, min, sec)
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：{天, 时, 分, 秒}
     */
    fun getDistanceTime(str1: String, str2: String): LongArray {
        val df = SimpleDateFormat(DateStyle.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS)
        val one: Date
        val two: Date
        var day: Long = 0
        var hour: Long = 0
        var min: Long = 0
        var sec: Long = 0
        try {
            one = df.parse(str1)
            two = df.parse(str2)
            val time1 = one.time
            val time2 = two.time
            val diff: Long
            if (time1 < time2) {
                diff = time2 - time1
            } else {
                diff = time1 - time2
            }
            day = diff / (24 * 60 * 60 * 1000)
            hour = diff / (60 * 60 * 1000) - day * 24
            min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
            sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return longArrayOf(day, hour, min, sec)
    }
}