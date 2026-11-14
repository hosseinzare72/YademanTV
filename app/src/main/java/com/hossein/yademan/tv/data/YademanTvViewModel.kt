package com.hossein.yademan.tv.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hossein.yademan.tv.core.TimeFmt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ============================================================================
 *  فایل: data/YademanTvViewModel.kt
 *  بخش: نگهدارندهٔ وضعیت اپ تلویزیون
 *
 *  وظایف:
 *   ۱) اسپلش: بررسی توکن ذخیره‌شده + اولین همگام‌سازی
 *   ۲) ورود: جفت‌سازی با QR (پیش‌فرض) یا ورود دستی (با هشدار)
 *   ۳) همگام‌سازی: هر ۱۵ دقیقه + باز شدن اپ + برگشت از standby + دکمهٔ دستی
 *   ۴) نشست منقضی (۴۰۱) → پاک شدن توکن → بازگشت به صفحهٔ ورود
 *  قاعدهٔ طلایی TV: هیچ پرداخت/حذف/ویرایشی انجام نمی‌شود؛ فقط نمایش.
 * ============================================================================
 */
class YademanTvViewModel(app: Application) : AndroidViewModel(app) {

    /** مرحلهٔ فعلی اپ */
    enum class Stage { SPLASH, LOGIN, MAIN }

    private val ctx get() = getApplication<Application>()
    private val engine = SyncEngine(ctx)
    private val pairing = PairingController(ctx)

    var stage: Stage by mutableStateOf(Stage.SPLASH)
        private set

    // ------------------------------------------------------------ ورود
    /** وضعیت جفت‌سازی QR */
    var pairState: PairState by mutableStateOf(PairState.Idle)
        private set

    /** false = ورود دستی باز است (پیش‌فرض: QR) */
    var manualMode: Boolean by mutableStateOf(false)
        private set

    var username: String by mutableStateOf(TvStore.userName(ctx))
    var password: String by mutableStateOf("")
    var errorText: String? by mutableStateOf(null)
        private set

    var busy: Boolean by mutableStateOf(false)
        private set

    // ------------------------------------------------------- همگام‌سازی
    var syncState: SyncState by mutableStateOf(SyncState.Idle)
        private set

    var lastSyncLabel: String by mutableStateOf(engine.lastSyncLabel)
        private set

    /** دیالوگ «نشست منقضی شده است» */
    var sessionExpired: Boolean by mutableStateOf(false)
        private set

    val offline: Boolean get() = syncState is SyncState.Offline

    private var pollJob: Job? = null
    private var autoJob: Job? = null

    // =====================================================================
    //  اسپلش
    // =====================================================================
    /**
     * اجرای یک‌باره در شروع اپ:
     *  • دست‌کم ۱٬۷۰۰ms اسپلش (همان طراحی)
     *  • اگر توکن داشتیم: auth/verify → MAIN، وگرنه LOGIN
     *  • اولین همگام‌سازی
     */
    fun boot() {
        if (booted) return
        booted = true
        viewModelScope.launch {
            val started = System.currentTimeMillis()
            val token = TvStore.token(ctx)

            val target: Stage = if (token == null) {
                Stage.LOGIN
            } else {
                when (val v = RemoteApi.verify(token, TvStore.deviceId(ctx))) {
                    is ApiResult.Ok -> if (v.value) Stage.MAIN else {
                        TvStore.clearToken(ctx)
                        Stage.LOGIN
                    }
                    is ApiResult.Http -> if (v.code == 401) {
                        TvStore.clearToken(ctx)
                        Stage.LOGIN
                    } else Stage.MAIN      // خطای سرور: آفلاین ادامه می‌دهیم
                    is ApiResult.Network -> Stage.MAIN // آفلاین: با دادهٔ ذخیره‌شده ادامه
                }
            }

            // ظاهر اسپلش دست‌نخورده بماند
            val elapsed = System.currentTimeMillis() - started
            if (elapsed < MIN_SPLASH_MS) delay(MIN_SPLASH_MS - elapsed)

            stage = target
            DataHub.userName = TvStore.userName(ctx)
            DataHub.userId = TvStore.userName(ctx)
            DataHub.deviceName = TvStore.deviceName(ctx)
            DataHub.deviceShortId = TvStore.deviceId(ctx).takeIf { it.isNotBlank() }
                ?.takeLast(5)?.let { "tv-$it" } ?: "tv-7f2a"

            if (target == Stage.LOGIN) startPairing() else firstSync()
        }
    }

    private var booted = false

    // =====================================================================
    //  جفت‌سازی با QR
    // =====================================================================
    /** شروع/شروع دوبارهٔ جریان QR */
    fun startPairing() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            pairState = PairState.Starting
            val started = pairing.start(
                deviceName = TvStore.deviceName(ctx),
                deviceModel = pairing.deviceModel()
            )
            if (started !is PairState.Waiting) {
                pairState = started
                return@launch
            }
            pairState = started

            val result = pairing.pollUntilDone(
                pairId = started.pairId,
                nonce = started.nonce,
                expiresAtMs = started.expiresAtMs,
                intervalSeconds = 3
            ) { remaining ->
                (pairState as? PairState.Waiting)?.let {
                    pairState = it.copy(remainingSeconds = remaining)
                }
            }

            when (result) {
                is PairState.Approved -> {
                    TvStore.saveToken(ctx, result.token)
                    if (result.deviceId.isNotBlank()) TvStore.saveDeviceId(ctx, result.deviceId)
                    pairState = result
                    stage = Stage.MAIN
                    firstSync()
                }
                else -> pairState = result
            }
        }
    }

    /** ورود دستی — با هشدار «گوشی شما خارج می‌شود» */
    fun chooseManual(on: Boolean) {
        manualMode = on
        errorText = null
        if (on) pollJob?.cancel() else startPairing()
    }

    fun onUsername(v: String) { username = v; errorText = null }
    fun onPassword(v: String) { password = v; errorText = null }

    fun submitManual() {
        errorText = when {
            username.isBlank() -> "نام کاربری را وارد کن"
            password.length < 6 -> "رمز عبور باید دست‌کم ۶ نویسه باشد"
            else -> null
        }
        val current = pairState
        if (current is PairState.Waiting) {
            // QR فعال است؛ کاربر صریحاً ورود دستی را انتخاب کرده
        }
        if (errorText != null) return

        busy = true
        viewModelScope.launch {
            when (val res = RemoteApi.loginManual(
                username = username,
                password = password,
                deviceName = TvStore.deviceName(ctx),
                deviceModel = pairing.deviceModel()
            )) {
                is ApiResult.Ok -> {
                    TvStore.saveToken(ctx, res.value.token)
                    if (res.value.deviceId.isNotBlank()) TvStore.saveDeviceId(ctx, res.value.deviceId)
                    if (res.value.userName.isNotBlank()) TvStore.saveUserName(ctx, res.value.userName)
                    DataHub.userName = res.value.userName.ifBlank { username }
                    DataHub.userId = username
                    password = ""
                    stage = Stage.MAIN
                    firstSync()
                }
                is ApiResult.Http -> errorText = res.message
                is ApiResult.Network -> errorText = "اتصال به سرور برقرار نشد"
            }
            busy = false
        }
    }

    // =====================================================================
    //  همگام‌سازی
    // =====================================================================
    /** اولین همگام‌سازی بعد از ورود + روشن کردن حلقهٔ ۱۵ دقیقه‌ای */
    private fun firstSync() {
        viewModelScope.launch {
            runSync()
            startAutoSync()
        }
    }

    private fun startAutoSync() {
        autoJob?.cancel()
        autoJob = viewModelScope.launch {
            while (true) {
                delay(SyncEngine.AUTO_INTERVAL_MS)
                if (TvStore.hasToken(ctx)) runSync()
            }
        }
    }

    /** دکمهٔ «همگام‌سازی» یا برگشت از standby */
    fun syncNow() {
        viewModelScope.launch { runSync() }
    }

    private suspend fun runSync() {
        if (!TvStore.hasToken(ctx)) return
        syncState = SyncState.Syncing
        engine.syncOnce()
        syncState = engine.state
        publish()
    }

    /** انتقال دادهٔ سرور به لایهٔ نمایش */
    private fun publish() {
        DataHub.records = engine.records
        DataHub.lastSyncLabel = engine.lastSyncLabel
        DataHub.offline = engine.state is SyncState.Offline
        DataHub.readOnly = true
        DataHub.syncNotesFromRecords()
        lastSyncLabel = engine.lastSyncLabel

        (engine.state as? SyncState.AuthError)?.let {
            sessionExpired = true
        }
    }

    /** کاربر «ورود دوباره» را در دیالوگ انقضا زد */
    fun acknowledgeExpiry() {
        sessionExpired = false
        logout()
    }

    // =====================================================================
    //  خروج
    // =====================================================================
    fun logout() {
        pollJob?.cancel()
        autoJob?.cancel()
        TvStore.logout(ctx)
        engine.clear()
        DataHub.clear()
        syncState = SyncState.Idle
        pairState = PairState.Idle
        password = ""
        manualMode = false
        stage = Stage.LOGIN
        startPairing()
    }

    /** خروج بدون پاک‌کردن توکن (بستن اپ) */
    fun onAppPause() { /* حلقهٔ خودکار در ViewModel زنده می‌ماند */ }

    /** متن «آخرین همگام‌سازی» برای صفحهٔ حساب */
    fun lastSyncHuman(): String {
        val raw = TvStore.lastSync(ctx) ?: return "هنوز همگام‌سازی نشده"
        val ms = TimeFmt.parseAny(raw) ?: return "هنوز همگام‌سازی نشده"
        val mins = ((System.currentTimeMillis() - ms) / 60_000L).coerceAtLeast(0)
        return when {
            mins < 1 -> "همین حالا"
            mins < 60 -> "${mins}دقیقه پیش"
            else -> "${mins / 60}ساعت پیش"
        }
    }

    companion object {
        /** حداقل زمان نمایش اسپلش (همان ۱٬۷۰۰ms طراحی) */
        const val MIN_SPLASH_MS = 1_700L
    }
}
