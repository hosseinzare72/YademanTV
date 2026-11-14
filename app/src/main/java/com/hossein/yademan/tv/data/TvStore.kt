package com.hossein.yademan.tv.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * ============================================================================
 *  فایل: data/TvStore.kt
 *  بخش: ذخیرهٔ امن توکن تلویزیون (EncryptedSharedPreferences)
 *
 *  قاعدهٔ حیاتی: توکن جفت‌سازی فقط یک‌بار تحویل داده می‌شود؛ بلافاصله ذخیره می‌شود.
 *  اگر Keystore دستگاه مشکل داشت، به‌صورت خودکار به SharedPreferences معمولی
 *  برمی‌گردیم تا اپ کرش نکند (fallback).
 * ============================================================================
 */
object TvStore {

    private const val PREF_NAME = "yademan_tv_secure"
    private const val PREF_FALLBACK = "yademan_tv_plain"

    private const val KEY_TOKEN = "tv_token"
    private const val KEY_DEVICE_ID = "tv_device_id"
    private const val KEY_DEVICE_NAME = "tv_device_name"
    private const val KEY_LAST_SYNC = "last_sync"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_LAST_CODES = "last_pair"

    @Volatile
    private var prefs: SharedPreferences? = null

    private fun prefs(ctx: Context): SharedPreferences {
        prefs?.let { return it }
        synchronized(this) {
            prefs?.let { return it }
            val p = runCatching {
                val key = MasterKey.Builder(ctx.applicationContext)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    ctx.applicationContext,
                    PREF_NAME,
                    key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                ) as SharedPreferences
            }.getOrElse {
                ctx.applicationContext.getSharedPreferences(PREF_FALLBACK, Context.MODE_PRIVATE)
            }
            prefs = p
            return p
        }
    }

    // ------------------------------------------------------------ توکن
    fun token(ctx: Context): String? = prefs(ctx).getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

    fun saveToken(ctx: Context, token: String) {
        prefs(ctx).edit().putString(KEY_TOKEN, token).apply()
    }

    fun clearToken(ctx: Context) {
        prefs(ctx).edit().remove(KEY_TOKEN).remove(KEY_LAST_SYNC).apply()
    }

    fun hasToken(ctx: Context): Boolean = token(ctx) != null

    // ------------------------------------------------------------ دستگاه
    fun deviceId(ctx: Context): String =
        prefs(ctx).getString(KEY_DEVICE_ID, null)?.takeIf { it.isNotBlank() } ?: ""

    fun saveDeviceId(ctx: Context, id: String) {
        prefs(ctx).edit().putString(KEY_DEVICE_ID, id).apply()
    }

    fun deviceName(ctx: Context): String =
        prefs(ctx).getString(KEY_DEVICE_NAME, null)?.takeIf { it.isNotBlank() } ?: "تلویزیون نشیمن"

    fun saveDeviceName(ctx: Context, name: String) {
        prefs(ctx).edit().putString(KEY_DEVICE_NAME, name).apply()
    }

    // ----------------------------------------------------------- last_sync
    fun lastSync(ctx: Context): String? =
        prefs(ctx).getString(KEY_LAST_SYNC, null)?.takeIf { it.isNotBlank() }

    fun saveLastSync(ctx: Context, value: String?) {
        prefs(ctx).edit().putString(KEY_LAST_SYNC, value ?: "").apply()
    }

    // ------------------------------------------------------------ کاربر
    fun userName(ctx: Context): String =
        prefs(ctx).getString(KEY_USER_NAME, null)?.takeIf { it.isNotBlank() } ?: "زهرا کاظمی"

    fun saveUserName(ctx: Context, name: String) {
        prefs(ctx).edit().putString(KEY_USER_NAME, name).apply()
    }

    /** آخرین pair_id/code برای اینکه اگر اپ بسته شد، QR از دست نرود */
    fun savePair(ctx: Context, pairId: String, nonce: String, code: String) {
        prefs(ctx).edit()
            .putString(KEY_LAST_CODES, "$pairId|$nonce|$code")
            .apply()
    }

    fun lastPair(ctx: Context): Triple<String, String, String>? {
        val raw = prefs(ctx).getString(KEY_LAST_CODES, null) ?: return null
        val parts = raw.split('|')
        if (parts.size != 3 || parts[0].isBlank()) return null
        return Triple(parts[0], parts[1], parts[2])
    }

    /** خروج کامل: توکن و شناسه‌ها پاک می‌شوند، نام دستگاه می‌ماند */
    fun logout(ctx: Context) {
        prefs(ctx).edit()
            .remove(KEY_TOKEN)
            .remove(KEY_DEVICE_ID)
            .remove(KEY_LAST_SYNC)
            .remove(KEY_LAST_CODES)
            .apply()
    }
}
