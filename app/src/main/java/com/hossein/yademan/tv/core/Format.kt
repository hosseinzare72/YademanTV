package com.hossein.yademan.tv.core

import kotlin.math.roundToInt

/**
 * ============================================================================
 *  فایل: core/Format.kt
 *  بخش: اعداد فارسی، جداکنندهٔ هزارگان، مبلغ و درصد
 * ============================================================================
 */
private const val FA_DIGITS = "۰۱۲۳۴۵۶۷۸۹"

fun String.toFa(): String = buildString(length) {
    for (ch in this@toFa) {
        append(if (ch in '0'..'9') FA_DIGITS[ch - '0'] else ch)
    }
}

fun Int.toFa(): String = toString().toFa()
fun Long.toFa(): String = toString().toFa()

fun String.toEnDigits(): String = buildString(length) {
    for (ch in this@toEnDigits) {
        val i = FA_DIGITS.indexOf(ch)
        append(if (i >= 0) ('0' + i) else ch)
    }
}

/** ۷۰۷۵۰۰۰ → «۷,۰۷۵,۰۰۰» */
fun Long.grouped(): String {
    val s = toString()
    val sb = StringBuilder()
    var count = 0
    for (i in s.lastIndex downTo 0) {
        sb.append(s[i])
        count++
        if (count % 3 == 0 && i != 0) sb.append(',')
    }
    return sb.reverse().toString()
}

/** مبلغ با ارقام فارسی */
fun money(value: Long): String = value.grouped().toFa()

/** «۷,۰۷۵,۰۰۰ تومان» */
fun moneyWithUnit(value: Long): String = "${money(value)} تومان"

/** ۰.۰۴ → «۴٪» */
fun percent(fraction: Float): String = "${(fraction * 100).roundToInt().toFa()}٪"

/** «۳ از ۷» */
fun ratio(done: Int, total: Int): String = "${done.toFa()} از ${total.toFa()}"
