package com.hossein.yademan.tv.core

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * ============================================================================
 *  فایل: core/TimeFormats.kt
 *  بخش: سه فرمت زمانی سرور + تبدیل ارقام فارسی (طبق بخش «تله‌های حیاتی»)
 *
 *   ۱) last_sync / updated_at : MySQL UTC      «2026-11-13 15:31:56»
 *   ۲) due_at / end_at        : ISO-8601 UTC   «2026-11-21T06:30:00Z»
 *   ۳) done_dates             : روز میلادی محلی «2026-11-21»
 * ============================================================================
 */
object TimeFmt {

    /** «2026-11-13 15:31:56» (UTC) → میلی‌ثانیه */
    fun parseMysqlUtc(raw: String?): Long? {
        val s = raw?.toAsciiDigits()?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return runCatching {
            LocalDateTime.parse(s.replace(' ', 'T'))
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        }.getOrNull()
    }

    /** «2026-11-21T06:30:00Z» یا «+03:30» یا بدون منطقه → میلی‌ثانیه */
    fun parseIsoUtc(raw: String?): Long? {
        val s = raw?.toAsciiDigits()?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return runCatching { OffsetDateTime.parse(s).toInstant().toEpochMilli() }.getOrNull()
            ?: runCatching { Instant.parse(s).toEpochMilli() }.getOrNull()
            ?: runCatching {
                LocalDateTime.parse(s.replace(' ', 'T')).toInstant(ZoneOffset.UTC).toEpochMilli()
            }.getOrNull()
    }

    /** هر سه فرمت را امتحان می‌کند (برای فیلدهایی که نوعشان قطعی نیست) */
    fun parseAny(raw: String?): Long? = parseIsoUtc(raw) ?: parseMysqlUtc(raw)

    /** کلید روز محلی «2026-11-21» برای مقایسه با done_dates */
    fun dayKey(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().toString()

    /** «2026-11-21» → میلی‌ثانیهٔ شروع همان روز (منطقهٔ محلی) */
    fun parseDayStart(raw: String?): Long? {
        val s = raw?.toAsciiDigits()?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return runCatching {
            LocalDate.parse(s).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrNull()
    }

    /** ابتدای روز محلی به وقت میلی‌ثانیه */
    fun startOfDay(millis: Long, zone: ZoneId = ZoneId.systemDefault()): Long =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
            .atStartOfDay(zone).toInstant().toEpochMilli()

    fun epochDay(millis: Long, zone: ZoneId = ZoneId.systemDefault()): Long =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().toEpochDay()

    /** شمارهٔ روز هفتهٔ ایرانی (شنبه = ۰) */
    fun iranWeekday(millis: Long, zone: ZoneId = ZoneId.systemDefault()): Int {
        val dow = Instant.ofEpochMilli(millis).atZone(zone).dayOfWeek.value // Mon=1..Sun=7
        return (dow + 1) % 7 // Sat=0
    }

    /** «2026-11-13 15:31:56» → همان رشته برای ارسال به سرور */
    fun asMysqlUtc(millis: Long): String =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC)
            .toString().replace('T', ' ').take(19)

    fun nowUtcSeconds(): Long = System.currentTimeMillis() / 1000

    fun zone(): ZoneId = ZoneId.systemDefault()

    fun toZoned(millis: Long): ZonedDateTime = Instant.ofEpochMilli(millis).atZone(zone())
}

/** ارقام فارسی/عربی → لاتین (برای پارس امن مقادیر متا) */
fun String.toAsciiDigits(): String = buildString(length) {
    for (ch in this@toAsciiDigits) {
        append(
            when (ch) {
                in '۰'..'۹' -> '0' + (ch - '۰')
                in '٠'..'٩' -> '0' + (ch - '٠')
                else -> ch
            }
        )
    }
}
