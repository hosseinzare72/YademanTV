package com.hossein.yademan.tv.data

import android.content.Context
import com.hossein.yademan.tv.core.TimeFmt
import com.hossein.yademan.tv.core.millisToFaShort
import com.hossein.yademan.tv.core.toFa
import kotlinx.coroutines.delay

/**
 * ============================================================================
 *  فایل: data/SyncEngine.kt
 *  بخش: همگام‌سازی افزایشی با سرور
 *
 *  الزامات سند:
 *   • هر ۱۵ دقیقه + باز شدن اپ + برگشت از standby + دکمهٔ دستی
 *   • حلقهٔ has_more تا false
 *   • حالت‌های Offline / 401 (توکن منقضی) / خطای عمومی
 * ============================================================================
 */
sealed interface SyncState {
    data object Idle : SyncState
    data object Syncing : SyncState
    data class Ok(val count: Int, val at: Long) : SyncState
    data class Offline(val lastKnown: Long?) : SyncState
    data class AuthError(val message: String) : SyncState
    data class Failed(val message: String) : SyncState
}

class SyncEngine(private val ctx: Context) {

    companion object {
        /** فاصلهٔ همگام‌سازی خودکار */
        const val AUTO_INTERVAL_MS = 15 * 60 * 1000L
    }

    var state: SyncState = SyncState.Idle
        private set

    var records: List<Record> = emptyList()
        private set

    var lastSyncRaw: String? = TvStore.lastSync(ctx)
        private set

    /** متن فارسی «آخرین همگام‌سازی» */
    val lastSyncLabel: String
        get() {
            val raw = lastSyncRaw ?: return "هنوز همگام‌سازی نشده"
            val millis = TimeFmt.parseAny(raw) ?: return "هنوز همگام‌سازی نشده"
            val mins = ((System.currentTimeMillis() - millis) / 60_000L).coerceAtLeast(0)
            return when {
                mins < 1 -> "همین حالا"
                mins < 60 -> "${mins.toFa()} دقیقه پیش"
                mins < 60 * 24 -> "${(mins / 60).toFa()} ساعت پیش"
                else -> millisToFaShort(millis)
            }
        }

    private var lastKnown: Long? =
        TvStore.lastSync(ctx)?.let { TimeFmt.parseAny(it) }

    /**
     * یک دور کامل همگام‌سازی با حلقهٔ has_more.
     * خروجی true اگر داده‌های تازه داشته باشیم.
     */
    suspend fun syncOnce(): Boolean {
        val token = TvStore.token(ctx) ?: run {
            state = SyncState.AuthError("دستگاه جفت نشده است")
            return false
        }
        val deviceId = TvStore.deviceId(ctx)

        state = SyncState.Syncing

        val acc = ArrayList<Record>()
        var cursor = lastSyncRaw
        var hasMore = true
        var guard = 0
        var latestSync: String? = cursor

        while (hasMore && guard++ < 20) {
            when (val res = RemoteApi.pull(token, deviceId, cursor)) {
                is ApiResult.Network -> {
                    state = SyncState.Offline(lastKnown)
                    return false
                }
                is ApiResult.Http -> {
                    if (res.code == 401) {
                        TvStore.clearToken(ctx)
                        state = SyncState.AuthError("نشست شما منقضی شده است")
                    } else {
                        state = SyncState.Failed(res.message)
                    }
                    return false
                }
                is ApiResult.Ok -> {
                    val page = res.value
                    acc.addAll(page.records)
                    latestSync = page.lastSync?.takeIf { it.isNotBlank() } ?: latestSync
                    cursor = latestSync
                    hasMore = page.hasMore
                    if (!hasMore) break
                    delay(120) // مکث کوتاه بین صفحه‌ها
                }
            }
        }

        // ادغام با رکوردهای قبلی (بر اساس id؛ جدیدتر برنده است)
        val merged = LinkedHashMap<String, Record>(records.size + acc.size)
        records.forEach { merged[it.id] = it }
        acc.forEach { merged[it.id] = it }
        records = merged.values.toList()

        latestSync?.let {
            lastSyncRaw = it
            TvStore.saveLastSync(ctx, it)
            lastKnown = TimeFmt.parseAny(it) ?: System.currentTimeMillis()
        }

        state = SyncState.Ok(records.size, System.currentTimeMillis())
        return acc.isNotEmpty()
    }

    /** حلقهٔ همگام‌سازی خودکار (در scope اپ اجرا می‌شود) */
    suspend fun autoLoop(onAfterSync: suspend () -> Unit = {}) {
        while (true) {
            delay(AUTO_INTERVAL_MS)
            if (TvStore.hasToken(ctx)) {
                syncOnce()
                onAfterSync()
            }
        }
    }

    fun clear() {
        records = emptyList()
        lastSyncRaw = null
        state = SyncState.Idle
    }
}
