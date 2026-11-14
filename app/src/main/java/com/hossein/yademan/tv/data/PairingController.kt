package com.hossein.yademan.tv.data

import android.content.Context
import kotlinx.coroutines.delay

/**
 * ============================================================================
 *  فایل: data/PairingController.kt
 *  بخش: ماشین حالت جفت‌سازی تلویزیون (TV Pairing)
 *
 *  جریان: start → QR + کد ۶ رقمی → poll هر ۳ ثانیه → approved/expired/denied
 *  محتوای QR: {"v":1,"h":"kafezare.ir","p":"<pair_id>","c":"<code>","n":"<nonce>"}
 * ============================================================================
 */
sealed interface PairState {
    data object Idle : PairState
    data object Starting : PairState

    /** در انتظار تأیید از موبایل — کد ۶ رقمی و شمارش معکوس */
    data class Waiting(
        val pairId: String,
        val code: String,
        val nonce: String,
        val expiresAtMs: Long,
        val remainingSeconds: Int
    ) : PairState

    data class Approved(val token: String, val deviceId: String) : PairState
    data object Expired : PairState
    data object Denied : PairState
    data class Error(val message: String) : PairState
}

/** محتوای دقیق QR (بدون هیچ فاصله‌ای) */
fun qrPayload(pairId: String, code: String, nonce: String): String =
    "{\"v\":1,\"h\":\"kafezare.ir\",\"p\":\"$pairId\",\"c\":\"$code\",\"n\":\"$nonce\"}"

class PairingController(private val ctx: Context) {

    /** شروع جفت‌سازی؛ خروجی: وضعیت اولیه */
    suspend fun start(deviceName: String, deviceModel: String): PairState {
        return when (val res = RemoteApi.pairStart(deviceName, deviceModel)) {
            is ApiResult.Network -> PairState.Error("اتصال به سرور برقرار نشد — دوباره تلاش کن")
            is ApiResult.Http -> PairState.Error(res.message)
            is ApiResult.Ok -> {
                val s = res.value
                TvStore.savePair(ctx, s.pairId, s.nonce, s.code)
                PairState.Waiting(
                    pairId = s.pairId,
                    code = s.code,
                    nonce = s.nonce,
                    expiresAtMs = System.currentTimeMillis() + s.expiresIn * 1000L,
                    remainingSeconds = s.expiresIn
                )
            }
        }
    }

    /**
     * حلقهٔ poll. تا رسیدن به approved/expired/denied ادامه می‌دهد.
     * [onTick] برای به‌روزرسانی شمارش معکوس صدا زده می‌شود.
     */
    suspend fun pollUntilDone(
        pairId: String,
        nonce: String,
        expiresAtMs: Long,
        intervalSeconds: Int = 3,
        onTick: (Int) -> Unit
    ): PairState {
        while (true) {
            val remaining = ((expiresAtMs - System.currentTimeMillis()) / 1000).toInt()
            onTick(remaining.coerceAtLeast(0))
            if (remaining <= 0) return PairState.Expired

            delay(intervalSeconds * 1000L)

            when (val res = RemoteApi.pairPoll(pairId, nonce)) {
                is ApiResult.Network -> {
                    // خطای موقت شبکه: تا پایان مهلت دوباره تلاش می‌کنیم
                    continue
                }
                is ApiResult.Http -> {
                    if (res.code == 401 || res.code == 410) return PairState.Expired
                    return PairState.Error(res.message)
                }
                is ApiResult.Ok -> when (val st = res.value) {
                    is PairStatus.Pending -> Unit
                    is PairStatus.Approved -> {
                        // توکن را همان لحظه ذخیره کن
                        TvStore.saveToken(ctx, st.token)
                        if (st.deviceId.isNotBlank()) TvStore.saveDeviceId(ctx, st.deviceId)
                        return PairState.Approved(st.token, st.deviceId)
                    }
                    is PairStatus.Expired -> return PairState.Expired
                    is PairStatus.Denied -> return PairState.Denied
                    is PairStatus.Unknown -> Unit
                }
            }
        }
    }

    /** نام پیش‌فرض این دستگاه (از Build می‌خوانیم و ذخیره می‌کنیم) */
    fun defaultDeviceName(): String = TvStore.deviceName(ctx)

    fun deviceModel(): String = runCatching {
        "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}".trim()
    }.getOrDefault("Android TV")

    /** آخرین pair ذخیره‌شده — اگر اپ وسط جفت‌سازی بسته شده باشد */
    fun savedPair(): PairState? {
        val p = TvStore.lastPair(ctx) ?: return null
        return PairState.Waiting(
            pairId = p.first,
            code = p.third,
            nonce = p.second,
            expiresAtMs = System.currentTimeMillis(),
            remainingSeconds = 0
        )
    }
}
