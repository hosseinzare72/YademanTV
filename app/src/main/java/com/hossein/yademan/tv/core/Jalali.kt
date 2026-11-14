package com.hossein.yademan.tv.core

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * ============================================================================
 *  فایل: core/Jalali.kt
 *  بخش: تقویم جلالی + اختلاف روز تا سررسید (بدون هیچ کتابخانهٔ خارجی)
 * ============================================================================
 */
data class JDate(val year: Int, val month: Int, val day: Int) {

    fun format(): String = "${day.toFa()} ${MONTHS[month - 1]} ${year.toFa()}"

    fun short(): String = "${day.toFa()} ${MONTHS[month - 1]}"

    companion object {
        val MONTHS = listOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )
    }
}

/** تبدیل میلادی → جلالی (الگوریتم استاندارد jalaali) */
fun toJalali(gy: Int, gm: Int, gd: Int): JDate {
    val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    val gy2 = if (gm > 2) gy + 1 else gy
    var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
            ((gy2 + 399) / 400) + gd + gDaysInMonth[gm - 1]
    var jy = -1595 + (33 * (days / 12053))
    days %= 12053
    jy += 4 * (days / 1461)
    days %= 1461
    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }
    return if (days < 186) {
        JDate(jy, 1 + days / 31, 1 + days % 31)
    } else {
        JDate(jy, 7 + (days - 186) / 30, 1 + (days - 186) % 30)
    }
}

fun LocalDate.toJalali(): JDate = toJalali(year, monthValue, dayOfMonth)

/** شنبه … جمعه */
fun LocalDate.faWeekday(): String = when (dayOfWeek.value) {
    6 -> "شنبه"
    7 -> "یکشنبه"
    1 -> "دوشنبه"
    2 -> "سه‌شنبه"
    3 -> "چهارشنبه"
    4 -> "پنجشنبه"
    else -> "جمعه"
}

/** «دوشنبه ۱۸ آبان ۱۴۰۵» */
fun LocalDate.faFull(): String = "${faWeekday()} ${toJalali().format()}"

/** تعداد روز باقی‌مانده (منفی = گذشته) */
fun daysUntil(due: LocalDate, today: LocalDate = LocalDate.now()): Long =
    ChronoUnit.DAYS.between(today, due)

/** متن وضعیت زمان‌بندی */
fun daysLabel(days: Long): String = when {
    days == 0L -> "امروز"
    days > 0 -> "${days.toFa()} روز مانده"
    else -> "${(-days).toFa()} روز گذشته"
}

// ---------------------------------------------------------------------------
//  تبدیل معکوس: جلالی → میلادی (الگوریتم استاندارد jalaali)
//  لازم برای محاسبهٔ «وقوع ماهانه» از ابتدای ماه شمسی
// ---------------------------------------------------------------------------
private fun isLeapGregorian(y: Int): Boolean = (y % 4 == 0 && y % 100 != 0) || y % 400 == 0

/** جلالی → میلادی (سال، ماه، روز) */
fun toGregorian(jyIn: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
    var jy = jyIn + 1595
    var days = -355668 + (365 * jy) + ((jy / 33) * 8) + (((jy % 33) + 3) / 4) + jd +
            (if (jm < 7) (jm - 1) * 31 else ((jm - 7) * 30) + 186)

    var gy = 400 * (days / 146097)
    days %= 146097
    if (days > 36524) {
        gy += 100 * (--days / 36524)
        days %= 36524
        if (days >= 365) days++
    }
    gy += 4 * (days / 1461)
    days %= 1461
    if (days > 365) {
        gy += (days - 1) / 365
        days = (days - 1) % 365
    }

    var gd = days + 1
    val monthLens = intArrayOf(
        0, 31,
        if (isLeapGregorian(gy)) 29 else 28,
        31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    )
    var gm = 1
    while (gm <= 12 && gd > monthLens[gm]) {
        gd -= monthLens[gm]
        gm++
    }
    return Triple(gy, gm, gd)
}

/** جلالی → LocalDate */
fun jalaliToLocalDate(jy: Int, jm: Int, jd: Int): LocalDate {
    val (gy, gm, gd) = toGregorian(jy, jm, jd)
    return LocalDate.of(gy, gm, gd)
}

/** جلالی (سال، ماه، روز، ساعت، دقیقه) → میلی‌ثانیه در منطقهٔ زمانی محلی */
fun jalaliToMillis(
    jy: Int, jm: Int, jd: Int,
    hour: Int = 0, minute: Int = 0,
    zone: java.time.ZoneId = java.time.ZoneId.systemDefault()
): Long = jalaliToLocalDate(jy, jm, jd)
    .atTime(hour, minute)
    .atZone(zone)
    .toInstant()
    .toEpochMilli()

/** میلی‌ثانیه → تاریخ جلالی */
fun millisToJalali(
    millis: Long,
    zone: java.time.ZoneId = java.time.ZoneId.systemDefault()
): JDate = java.time.Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().toJalali()

/** «۱۸ آبان ۱۴۰۵» از میلی‌ثانیه */
fun millisToFaDate(
    millis: Long,
    zone: java.time.ZoneId = java.time.ZoneId.systemDefault()
): String = millisToJalali(millis, zone).format()

/** «۱۸ آبان» از میلی‌ثانیه */
fun millisToFaShort(
    millis: Long,
    zone: java.time.ZoneId = java.time.ZoneId.systemDefault()
): String = millisToJalali(millis, zone).short()

/** ابتدای ماه شمسیِ شامل این لحظه (میلی‌ثانیه) */
fun jalaliMonthStart(
    millis: Long,
    zone: java.time.ZoneId = java.time.ZoneId.systemDefault()
): Long {
    val j = millisToJalali(millis, zone)
    return jalaliToMillis(j.year, j.month, 1, 0, 0, zone)
}

/**
 * جمعِ تعداد ماه شمسی بین دو لحظه:
 * (۱۴۰۵/۰۲ → ۱۴۰۵/۰۹) = ۷
 */
fun jalaliMonthsBetween(fromMillis: Long, toMillis: Long): Int {
    val a = millisToJalali(fromMillis)
    val b = millisToJalali(toMillis)
    return (b.year - a.year) * 12 + (b.month - a.month)
}

/** افزودن n ماه شمسی به یک لحظه (روز حفظ می‌شود، طول ماه رعایت می‌شود) */
fun jalaliPlusMonths(millis: Long, months: Int): Long {
    val j = millisToJalali(millis)
    var year = j.year
    var month = j.month + months
    while (month > 12) { month -= 12; year++ }
    while (month < 1) { month += 12; year-- }
    val maxDay = if (month <= 6) 31 else if (month <= 11) 30 else 29
    val day = j.day.coerceAtMost(maxDay)
    val zone = java.time.ZoneId.systemDefault()
    val time = java.time.Instant.ofEpochMilli(millis).atZone(zone)
    return jalaliToMillis(year, month, day, time.hour, time.minute, zone)
}
