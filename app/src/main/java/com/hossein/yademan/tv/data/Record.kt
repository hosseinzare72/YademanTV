package com.hossein.yademan.tv.data

import com.hossein.yademan.tv.core.TimeFmt
import com.hossein.yademan.tv.core.resolveType
import com.hossein.yademan.tv.core.toAsciiDigits
import org.json.JSONArray
import org.json.JSONObject

/**
 * ============================================================================
 *  فایل: data/Record.kt
 *  بخش: مدل رکورد سرور + پارس امن meta_json
 *
 *  نکته‌های حیاتی که اینجا حل شده‌اند:
 *   • meta_json هم رشته است هم آبجکت (هر دو هندل می‌شوند)
 *   • کلیدهای متا ممکن است فاصله داشته باشند (trim می‌شوند)
 *   • ارقام فارسی داخل متا با toAsciiDigits به لاتین تبدیل می‌شوند
 *   • سه فرمت زمانی مختلف (MySQL UTC / ISO-8601 / روز محلی)
 * ============================================================================
 */

/** نتیجهٔ پارس‌شدهٔ meta_json با کلیدهای trim‌شده */
class Meta private constructor(private val map: Map<String, Any?>) {

    fun str(key: String): String? {
        val v = map[key] ?: return null
        val s = when (v) {
            is String -> v
            is Number, is Boolean -> v.toString()
            else -> null
        } ?: return null
        return s.toAsciiDigits().trim().ifEmpty { null }
    }

    fun long(key: String): Long? = str(key)?.let { s ->
        s.filter { it.isDigit() || it == '-' || it == '.' }.toLongOrNull()
    }

    fun bool(key: String): Boolean? = when (val s = str(key)) {
        "1", "true", "yes", "on" -> true
        "0", "false", "no", "off" -> false
        else -> null
    }

    /** فهرست روزهای انجام‌شده (done_dates) */
    fun stringSet(key: String): Set<String> {
        val v = map[key] ?: return emptySet()
        val out = LinkedHashSet<String>()
        when (v) {
            is JSONArray -> for (i in 0 until v.length()) {
                v.optString(i).takeIf { it.isNotBlank() }?.let { out.add(it.toAsciiDigits().trim()) }
            }
            is JSONObject -> v.keys().forEach { k -> out.add(k.toAsciiDigits().trim()) }
            is String -> v.split(',', ';')
                .map { it.trim().toAsciiDigits() }
                .filter { it.isNotEmpty() }
                .forEach { out.add(it) }
        }
        return out
    }

    fun has(key: String): Boolean = map.containsKey(key)

    companion object {
        val EMPTY = Meta(emptyMap())

        /**
         * meta_json ممکن است رشتهٔ JSON، آبجکت JSON، یا خالی باشد.
         * کلیدها trim می‌شوند تا « hub » هم پیدا شود.
         */
        fun parse(raw: Any?): Meta {
            val obj: JSONObject = when (raw) {
                null, JSONObject.NULL -> return EMPTY
                is JSONObject -> raw
                is String -> {
                    val t = raw.trim()
                    if (t.isEmpty() || t == "null" || t == "{}") return EMPTY
                    runCatching { JSONObject(t) }.getOrElse { return EMPTY }
                }
                else -> runCatching { JSONObject(raw.toString()) }.getOrElse { return EMPTY }
            }
            val map = LinkedHashMap<String, Any?>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k.trim().lowercase()] = obj.opt(k)
            }
            return Meta(map)
        }
    }
}

/**
 * یک رکورد از سرور (نمایشی، همان چیزی که sync/pull برمی‌گرداند).
 * همهٔ فیلدهای ممکن با try پارس می‌شوند تا یک رکورد خراب، کل لیست را نشکند.
 */
data class Record(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Long,
    val dueAt: Long?,
    val endAt: Long?,
    val repeatRule: String,        // none | daily | weekly | monthly | yearly
    val repeatInterval: Int,
    val isDone: Boolean,
    val categoryId: String?,
    val doneDates: Set<String>,
    val updatedAt: Long?,
    val meta: Meta,
    val raw: JSONObject
) {
    /** نوع سرویس نهایی (زنجیرهٔ resolve در core/Occurrence.kt) */
    val serviceType: String get() = resolveType(this)

    /** شناسهٔ فنی برای نمایش در پنل حساب */
    val shortId: String get() = id.takeLast(6)

    companion object {
        /** ساخت Record از یک JSONObject سرور؛ در صورت خرابی null */
        fun from(o: JSONObject): Record? = runCatching {
            val id = (o.optString("id").ifBlank { o.optString("record_id") })
                .ifBlank { o.optString("uuid") }
                .ifBlank { "rec-" + (o.toString().hashCode().toUInt().toString(16)) }

            val meta = Meta.parse(
                o.opt("meta_json") ?: o.opt("meta") ?: o.opt("metaJson")
            )

            // عنوان: title یا meta.title یا name
            val title = o.optString("title").ifBlank {
                meta.str("title") ?: o.optString("name").ifBlank { "بدون عنوان" }
            }

            val subtitle = o.optString("subtitle").ifBlank {
                meta.str("subtitle") ?: meta.str("note") ?: ""
            }

            // مبلغ: amount یا meta.amount (ممکن است فارسی باشد)
            val amount = o.optLong("amount", 0L).takeIf { it != 0L }
                ?: meta.long("amount")
                ?: meta.long("price")
                ?: 0L

            val repeatRule = (o.optString("repeat_rule").ifBlank { meta.str("repeat") ?: "none" }).lowercase()
            val repeatInterval = (o.opt("repeat_interval") as? Number)?.toInt()
                ?: meta.long("interval")?.toInt()
                ?: 1

            val isDone = o.optBoolean("is_done", false) || (meta.bool("is_done") ?: false)

            Record(
                id = id,
                title = title,
                subtitle = subtitle,
                amount = amount,
                dueAt = TimeFmt.parseAny(o.optString("due_at").ifBlank { meta.str("due_at") }),
                endAt = TimeFmt.parseAny(o.optString("end_at").ifBlank { meta.str("end_at") }),
                repeatRule = repeatRule,
                repeatInterval = repeatInterval.coerceAtLeast(1),
                isDone = isDone,
                categoryId = o.optString("category_id").ifBlank { meta.str("category_id") },
                doneDates = meta.stringSet("done_dates"),
                updatedAt = TimeFmt.parseAny(o.optString("updated_at")),
                meta = meta,
                raw = o
            )
        }.getOrNull()

        fun listFrom(arr: JSONArray?): List<Record> {
            if (arr == null) return emptyList()
            val out = ArrayList<Record>(arr.length())
            for (i in 0 until arr.length()) {
                arr.optJSONObject(i)?.let { from(it)?.let(out::add) }
            }
            return out
        }
    }
}
