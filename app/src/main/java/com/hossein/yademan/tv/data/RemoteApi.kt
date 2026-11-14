package com.hossein.yademan.tv.data

import android.os.Build
import com.hossein.yademan.tv.core.TimeFmt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * ============================================================================
 *  فایل: data/RemoteApi.kt
 *  بخش: کلاینت HTTP سرور «کافه‌زاره» (بدون هیچ وابستگی اضافه)
 *
 *  نکته: طبق الزام پروژه، وابستگی‌ها فقط همان لیست مصوب هستند؛
 *  به‌جای Retrofit از HttpURLConnection + org.json استفاده شده تا APK سبک بماند.
 * ============================================================================
 */

/** نتیجهٔ هر فراخوانی شبکه */
sealed class ApiResult<out T> {
    data class Ok<T>(val value: T) : ApiResult<T>()

    /** خطای HTTP با کد وضعیت (۴۰۱ = توکن منقضی) */
    data class Http(val code: Int, val body: String, val message: String) : ApiResult<Nothing>()

    /** خطای شبکه/آفلاین */
    data class Network(val error: Throwable) : ApiResult<Nothing>()
}

/** پاسخ tv/pair/start */
data class PairStart(
    val pairId: String,
    val code: String,
    val nonce: String,
    val expiresIn: Int,
    val pollInterval: Int
)

/** وضعیت جفت‌سازی */
sealed class PairStatus {
    data object Pending : PairStatus()
    data class Approved(val token: String, val deviceId: String) : PairStatus()
    data object Expired : PairStatus()
    data object Denied : PairStatus()
    data class Unknown(val status: String) : PairStatus()
}

/** نتیجهٔ ورود دستی */
data class AuthSession(val token: String, val deviceId: String, val userName: String)

/** پاسخ sync/pull */
data class PullPage(
    val records: List<Record>,
    val lastSync: String?,
    val hasMore: Boolean,
    val readOnly: Boolean
)

object RemoteApi {

    /** آدرس پایهٔ وردپرس (همان دامنهٔ سند طراحی) */
    const val BASE = "https://kafezare.ir/wp-json/lifeplanner/v1/"

    const val APP_VERSION = "1.4.0"

    private const val TIMEOUT_CONNECT = 12_000
    private const val TIMEOUT_READ = 18_000

    // ------------------------------------------------------------ جفت‌سازی
    suspend fun pairStart(deviceName: String, deviceModel: String): ApiResult<PairStart> =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("device_name", deviceName)
                .put("device_model", deviceModel)
                .put("app_version", APP_VERSION)

            when (val res = post("tv/pair/start", body)) {
                is ApiResult.Network -> res
                is ApiResult.Http -> res
                is ApiResult.Ok -> {
                    val o = res.value
                    if (!o.optBoolean("ok", true)) {
                        ApiResult.Http(200, o.toString(), o.optString("message", "خطای سرور"))
                    } else {
                        val pairId = o.optString("pair_id")
                        val code = o.optString("code")
                        val nonce = o.optString("nonce")
                        if (pairId.isBlank() || code.isBlank()) {
                            ApiResult.Http(200, o.toString(), "پاسخ سرور ناقص است")
                        } else {
                            ApiResult.Ok(
                                PairStart(
                                    pairId = pairId,
                                    code = code,
                                    nonce = nonce,
                                    expiresIn = o.optInt("expires_in", 300),
                                    pollInterval = o.optInt("poll_interval", 3)
                                )
                            )
                        }
                    }
                }
            }
        }

    suspend fun pairPoll(pairId: String, nonce: String): ApiResult<PairStatus> =
        withContext(Dispatchers.IO) {
            val body = JSONObject().put("pair_id", pairId).put("nonce", nonce)
            when (val res = post("tv/pair/poll", body)) {
                is ApiResult.Network -> res
                is ApiResult.Http -> res
                is ApiResult.Ok -> {
                    val o = res.value
                    when (o.optString("status").lowercase()) {
                        "pending" -> ApiResult.Ok(PairStatus.Pending)
                        "approved" -> {
                            val token = o.optString("token")
                            val deviceId = o.optString(
                                "tv_device_id",
                                o.optString("device_id", "")
                            )
                            if (token.isBlank()) {
                                ApiResult.Http(200, o.toString(), "توکن دریافت نشد")
                            } else {
                                ApiResult.Ok(PairStatus.Approved(token, deviceId))
                            }
                        }
                        "expired" -> ApiResult.Ok(PairStatus.Expired)
                        "denied" -> ApiResult.Ok(PairStatus.Denied)
                        else -> ApiResult.Ok(PairStatus.Unknown(o.optString("status")))
                    }
                }
            }
        }


    // ------------------------------------------------ ورود دستی (استثنا)
    /**
     * ورود با نام کاربری/رمز — فقط وقتی کاربر خودش «ورود دستی» را انتخاب کند.
     * هشدار: این کار نشست گوشی را خارج می‌کند؛ در UI قبل از فراخوانی هشدار داده می‌شود.
     */
    suspend fun loginManual(
        username: String,
        password: String,
        deviceName: String,
        deviceModel: String
    ): ApiResult<AuthSession> = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("username", username.trim())
            .put("password", password)
            .put("device_name", deviceName)
            .put("device_model", deviceModel)
            .put("app_version", APP_VERSION)
            .put("platform", "tv")

        when (val res = post("auth/login", body)) {
            is ApiResult.Network -> res
            is ApiResult.Http -> res
            is ApiResult.Ok -> {
                val o = res.value
                val token = o.optString("token")
                if (token.isBlank()) {
                    ApiResult.Http(200, o.toString(), o.optString("message", "نام کاربری یا رمز درست نیست"))
                } else {
                    ApiResult.Ok(
                        AuthSession(
                            token = token,
                            deviceId = o.optString(
                                "tv_device_id",
                                o.optString("device_id", "")
                            ),
                            userName = o.optString("name").ifBlank {
                                o.optString("display_name").ifBlank { username.trim() }
                            }
                        )
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------- سینک
    /**
     * یک صفحهٔ sync/pull. حلقهٔ has_more در SyncEngine زده می‌شود.
     */
    suspend fun pull(token: String, deviceId: String, lastSync: String?): ApiResult<PullPage> =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("token", token)
                .put("device_id", deviceId)
                .put("last_sync", lastSync ?: "")

            when (val res = post("sync/pull", body)) {
                is ApiResult.Network -> res
                is ApiResult.Http -> res
                is ApiResult.Ok -> {
                    val o = res.value
                    val arr: JSONArray? = o.optJSONArray("records")
                        ?: o.optJSONObject("data")?.optJSONArray("records")
                    ApiResult.Ok(
                        PullPage(
                            records = Record.listFrom(arr),
                            lastSync = o.optString("last_sync").ifBlank {
                                o.optJSONObject("data")?.optString("last_sync")
                            },
                            hasMore = o.optBoolean("has_more", false),
                            readOnly = o.optBoolean("read_only", true)
                        )
                    )
                }
            }
        }

    /** تأیید اعتبار توکن (برای اسپلش) */
    suspend fun verify(token: String, deviceId: String): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            val body = JSONObject().put("token", token).put("device_id", deviceId)
            when (val res = post("auth/verify", body)) {
                is ApiResult.Network -> res
                is ApiResult.Http -> if (res.code == 401) ApiResult.Ok(false) else res
                is ApiResult.Ok -> ApiResult.Ok(res.value.optBoolean("ok", true))
            }
        }

    // ------------------------------------------------------------ کمکی
    private fun post(path: String, payload: JSONObject): ApiResult<JSONObject> {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(BASE + path)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = TIMEOUT_CONNECT
                readTimeout = TIMEOUT_READ
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "YademanTV/${APP_VERSION} (Android ${Build.VERSION.SDK_INT})")
            }

            OutputStreamWriter(conn.outputStream, StandardCharsets.UTF_8).use {
                it.write(payload.toString())
                it.flush()
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.let { s ->
                BufferedReader(InputStreamReader(s, StandardCharsets.UTF_8)).use { it.readText() }
            }.orEmpty()

            if (code !in 200..299) {
                val msg = runCatching { JSONObject(text).optString("message") }.getOrNull()
                    .orEmpty().ifBlank {
                        when (code) {
                            401 -> "نشست شما منقضی شده است"
                            404 -> "سرویس در دسترس نیست"
                            in 500..599 -> "سرور با خطا مواجه شد"
                            else -> "خطای ارتباط با سرور ($code)"
                        }
                    }
                return ApiResult.Http(code, text, msg)
            }

            val json = text.trim()
            if (json.isEmpty()) return ApiResult.Http(code, "", "پاسخ خالی از سرور")
            ApiResult.Ok(JSONObject(json))
        } catch (t: Throwable) {
            ApiResult.Network(t)
        } finally {
            runCatching { conn?.disconnect() }
        }
    }

    /** ساعت UTC فعلی به فرمت MySQL — برای نمایش در پنل همگام‌سازی */
    fun nowMySql(): String = TimeFmt.asMysqlUtc(System.currentTimeMillis())
}
