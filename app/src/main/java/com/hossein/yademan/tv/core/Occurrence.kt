package com.hossein.yademan.tv.core

import com.hossein.yademan.tv.data.Record
import java.time.ZoneId

/**
 * ============================================================================
 *  فایل: core/Occurrence.kt
 *  بخش: منطق دادهٔ سرور — نوع سرویس، وقوع جاری، شمارهٔ قسط، وضعیت پرداخت
 *
 *  قانون طلایی: برای اقساط/چک/قبض، «وقوع جاری» = اولین وقوع از ابتدای ماه
 *  شمسی جاری به بعد (نه due_at خام). شمارهٔ قسط هم از occurrenceIndex می‌آید،
 *  نه paidCount+1.
 * ============================================================================
 */

/** جدول Aliasها — «bill» ← «bills»، «charge» ← «building» و … */
val ALIASES: Map<String, String> = mapOf(
    "bill" to "bills",
    "bills" to "bills",
    "utility" to "bills",
    "utilities" to "bills",
    "charge" to "building",
    "building" to "building",
    "building_charge" to "building",
    "rent" to "rent",
    "rents" to "rent",
    "installment" to "installment",
    "installments" to "installment",
    "loan" to "installment",
    "cheque" to "check",
    "cheques" to "check",
    "check" to "check",
    "checks" to "check",
    "medicine" to "medicine",
    "medicines" to "medicine",
    "pill" to "medicine",
    "note" to "note",
    "notes" to "note",
    "shopping" to "shopping",
    "shop" to "shopping",
    "cart" to "shopping",
    "meeting" to "meeting",
    "meetings" to "meeting",
    "appointment" to "meeting",
    "subscription" to "subscription",
    "subscriptions" to "subscription",
    "trust" to "trust",
    "deposit" to "trust",
    "travel" to "travel",
    "trip" to "travel",
    "insurance" to "insurance",
    "car" to "car",
    "car_service" to "car",
    "salary" to "salary",
    "pocket" to "pocket",
    "birthday" to "birthday",
    "birthdays" to "birthday",
    "anniversary" to "anniversary",
    "anniversaries" to "anniversary"
)

/** شناسه‌های دستهٔ سرور → نوع سرویس */
private val CATEGORY_MAP: Map<String, String> = mapOf(
    "cat_installment" to "installment",
    "cat_check" to "check",
    "cat_cheque" to "check",
    "cat_rent" to "rent",
    "cat_bill" to "bills",
    "cat_bills" to "bills",
    "cat_medicine" to "medicine",
    "cat_note" to "note",
    "cat_shopping" to "shopping",
    "cat_meeting" to "meeting",
    "cat_subscription" to "subscription",
    "cat_trust" to "trust",
    "cat_building" to "building",
    "cat_travel" to "travel",
    "cat_insurance" to "insurance",
    "cat_car" to "car",
    "cat_salary" to "salary",
    "cat_pocket" to "pocket",
    "cat_birthday" to "birthday",
    "cat_anniversary" to "anniversary"
)

/** نرمال‌سازی یک نام نوع خام */
fun canonical(raw: String?): String? {
    val s = raw?.trim()?.lowercase()?.replace('-', '_')?.replace(' ', '_')
        ?.takeIf { it.isNotEmpty() } ?: return null
    return ALIASES[s] ?: s
}

/**
 * زنجیرهٔ تعیین نوع: meta.type → meta.hub → category_id → "other"
 */
fun resolveType(r: Record): String {
    val metaType = canonical(r.meta.str("type"))
    if (metaType != null && metaType != "other") return metaType

    val hubType = canonical(r.meta.str("hub"))
    if (hubType != null && hubType != "other") return hubType

    val fromCat = canonical(r.categoryId)?.let { CATEGORY_MAP[it] ?: CATEGORY_MAP["cat_$it"] }
    if (fromCat != null) return fromCat

    return "other"
}

// ---------------------------------------------------------------------------
//  محاسبهٔ وقوع‌ها
// ---------------------------------------------------------------------------

private fun stepMillis(rule: String, interval: Int, from: Long): Long = when (rule) {
    "daily" -> from + interval * 86_400_000L
    "weekly" -> from + interval * 7 * 86_400_000L
    "monthly" -> jalaliPlusMonths(from, interval)
    "yearly" -> jalaliPlusMonths(from, interval * 12)
    else -> Long.MAX_VALUE
}

/**
 * اولین وقوع بعد از [from] (یا مساوی آن، اگر skipDone=false).
 * وقوع‌های انجام‌شده با done_dates مقایسه می‌شوند و در حالت skipDone رد می‌شوند.
 */
fun nextOccurrence(
    r: Record,
    from: Long,
    skipDone: Boolean = false,
    maxSteps: Int = 240
): Long? {
    val base = r.dueAt ?: return null
    if (r.repeatRule == "none") {
        return if (!skipDone || !isPaid(r, base)) base.takeIf { it >= startOfDayMs(from) } else null
    }
    var occ = if (startOfDayMs(base) >= startOfDayMs(from)) base else {
        // جلو رفتن از base تا رسیدن به from
        var cur = base
        var guard = 0
        while (startOfDayMs(cur) < startOfDayMs(from) && guard++ < maxSteps) {
            val nx = stepMillis(r.repeatRule, r.repeatInterval, cur)
            if (nx == Long.MAX_VALUE) return null
            cur = nx
        }
        cur
    }
    var guard = 0
    while (guard++ < maxSteps) {
        val paid = isPaid(r, occ)
        if (!paid || !skipDone) return occ
        val nx = stepMillis(r.repeatRule, r.repeatInterval, occ)
        if (nx == Long.MAX_VALUE) return null
        occ = nx
    }
    return null
}

/** آخرین وقوع پیش از [now] (برای رکوردهای عقب‌افتاده) */
fun lastOccurrence(r: Record, now: Long = System.currentTimeMillis()): Long? {
    val base = r.dueAt ?: return null
    if (r.repeatRule == "none") return base
    var cur = base
    var last = if (startOfDayMs(cur) <= startOfDayMs(now)) cur else null
    var guard = 0
    while (guard++ < 480) {
        val nx = stepMillis(r.repeatRule, r.repeatInterval, cur)
        if (nx == Long.MAX_VALUE || startOfDayMs(nx) > startOfDayMs(now)) break
        cur = nx
        last = cur
    }
    return last
}

/**
 * «وقوع جاری» — قانون طلایی:
 * برای اقساط/چک/قبض = اولین وقوع از ابتدای ماه شمسی جاری به بعد.
 */
fun currentOccurrence(r: Record, now: Long = System.currentTimeMillis()): Long? {
    val monthStart = jalaliMonthStart(now)
    return nextOccurrence(r, monthStart, skipDone = false)
        ?: lastOccurrence(r, now)
        ?: r.dueAt
}

/**
 * شمارهٔ قسط برای یک وقوع مشخص (۱-پایه).
 * فرمول paidCount+1 اشتباه است؛ این تابع از فاصلهٔ واقعی استفاده می‌کند.
 */
fun occurrenceIndex(r: Record, occ: Long): Int {
    val base = r.dueAt ?: return 1
    if (occ < startOfDayMs(base)) return 1
    val interval = r.repeatInterval.coerceAtLeast(1)
    return when (r.repeatRule) {
        "daily" -> ((epochDayMs(occ) - epochDayMs(base)) / interval).toInt() + 1
        "monthly", "yearly" -> {
            val months = jalaliMonthsBetween(base, occ)
            val step = if (r.repeatRule == "yearly") 12 * interval else interval
            months / step + 1
        }
        else -> 1
    }.coerceAtLeast(1)
}

/** مجموع تعداد وقوع تا تاریخ پایان (برای «۱ از ۲۴») */
fun totalOccurrences(r: Record): Int {
    // رکورد یک‌باره همیشه یک وقوع دارد (حتی اگر end_at نداشته باشد)
    if (r.repeatRule == "none") return 1
    val base = r.dueAt ?: return 1
    val end = r.endAt ?: return r.meta.long("total")?.toInt() ?: 24
    return when (r.repeatRule) {
        "monthly" -> (jalaliMonthsBetween(base, end) / r.repeatInterval.coerceAtLeast(1)) + 1
        "yearly" -> (jalaliMonthsBetween(base, end) / (12 * r.repeatInterval.coerceAtLeast(1))) + 1
        "weekly" -> ((epochDayMs(end) - epochDayMs(base)) / (7L * r.repeatInterval.coerceAtLeast(1))).toInt() + 1
        "daily" -> ((epochDayMs(end) - epochDayMs(base)) / r.repeatInterval.coerceAtLeast(1)).toInt() + 1
        else -> 1
    }.coerceIn(1, 600)
}

/**
 * وضعیت پرداخت:
 *  • تکرارشونده → done_dates اولویت دارد (is_done سرور نادیده گرفته می‌شود)
 *  • یک‌باره      → is_done
 */
fun isPaid(r: Record, occ: Long): Boolean {
    if (r.repeatRule == "none") return r.isDone
    val key = dayKeyOf(occ)
    if (r.doneDates.contains(key)) return true
    // بعضی سرورها done_dates را با فرمت ISO کامل یا با ارقام فارسی می‌فرستند
    return r.doneDates.any { it.startsWith(key) }
}

/** تعداد وقوع‌های انجام‌شده تا این لحظه (برای پیشرفت و شمارهٔ قسط) */
fun paidCount(r: Record, now: Long = System.currentTimeMillis()): Int {
    if (r.repeatRule == "none") return if (r.isDone) 1 else 0
    if (r.doneDates.isEmpty()) return 0
    return r.doneDates.count { it <= dayKeyOf(now) }
}

/** پیشرفت ۰..۱ بر اساس شمارهٔ قسط (نه تعداد پرداخت) */
fun progressOf(r: Record, occ: Long): Float {
    val total = totalOccurrences(r).coerceAtLeast(1)
    return (occurrenceIndex(r, occ).toFloat() / total).coerceIn(0f, 1f)
}

/** روزهای باقی‌مانده تا وقوع (منفی = گذشته) */
fun daysLeft(occ: Long, now: Long = System.currentTimeMillis()): Long =
    epochDayMs(occ) - epochDayMs(now)

// ---------------------------------------------------------------- کمکی‌ها
private fun startOfDayMs(m: Long): Long = TimeFmt.startOfDay(m)
private fun epochDayMs(m: Long): Long = TimeFmt.epochDay(m)
private fun dayKeyOf(m: Long, zone: ZoneId = ZoneId.systemDefault()): String = TimeFmt.dayKey(m, zone)
