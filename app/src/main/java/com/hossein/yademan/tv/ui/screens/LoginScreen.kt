package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hossein.yademan.tv.data.DataHub
import com.hossein.yademan.tv.data.PairState
import com.hossein.yademan.tv.data.qrPayload
import com.hossein.yademan.tv.ui.components.ErrorLine
import com.hossein.yademan.tv.ui.components.FocusSurface
import com.hossein.yademan.tv.ui.components.GhostButton
import com.hossein.yademan.tv.ui.components.PrimaryButton
import com.hossein.yademan.tv.ui.components.QrCard
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px

/**
 * ============================================================================
 *  فایل: ui/screens/LoginScreen.kt
 *  صفحهٔ ۰۲ — ورود به حساب (اسکرین 02-login.png)
 *
 *  چیدمان دقیقاً مثل طراحی: ستون فرم (1fr) + کارت QR (۳۳۰dp)
 *  رفتار جدید و حرفه‌ای:
 *    • QR واقعی از tv/pair/start با کد ۶ رقمی و شمارش معکوس ۳۰۰ ثانیه
 *    • وضعیت‌های pending / approved / expired / denied / error
 *    • ورود دستی با هشدار «گوشی شما از حساب خارج می‌شود»
 *  فوکوس اول این صفحه: فیلد «نام کاربری» (جدول بخش ۱۴ سند)
 * ============================================================================
 */
@Composable
fun LoginScreen(
    pairState: PairState,
    username: String,
    password: String,
    errorText: String?,
    busy: Boolean,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onSubmit: () -> Unit,
    onRetryPair: () -> Unit
) {
    var confirmManual by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp, vertical = 17.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ============================================================ فرم
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = px(60), end = px(24)),
            verticalArrangement = Arrangement.Center
        ) {
            // ------------------------------------------- خط برند
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .size(px(66))
                        .clip(RoundedCornerShape(px(22)))
                        .background(TBrush.mark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.NotificationsActive,
                        null,
                        tint = Color(0xFF141006),
                        modifier = Modifier.size(px(34))
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("یادمان", style = TText.h3, color = TColor.Ink)
                    Text("دستیار یادآوری خانواده", style = TText.micro, color = TColor.Muted2)
                }
            }

            Spacer(Modifier.height(26.dp))

            // ------------------------------------------------ تیتر
            Text("خوش برگشتی، ${DataHub.USER_NAME.substringBefore(' ')}", style = TText.h2, color = TColor.Ink)
            Spacer(Modifier.height(5.dp))
            Text(
                "برای دیدن یادآوری‌ها، اقساط و چک‌ها روی تلویزیون، وارد حساب یادمان شو.",
                style = TText.body,
                color = TColor.Muted,
                modifier = Modifier.fillMaxWidth(0.92f)
            )

            Spacer(Modifier.height(20.dp))

            // ----------------------------------------------- فیلدها
            val firstFocus = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                runCatching { firstFocus.requestFocus() }
            }

            TvField(
                label = "نام کاربری",
                value = username,
                onValueChange = onUsername,
                icon = Icons.Filled.Person,
                focusRequester = firstFocus
            )
            Spacer(Modifier.height(16.dp))
            TvField(
                label = "رمز عبور",
                value = password,
                onValueChange = onPassword,
                icon = Icons.Filled.Lock,
                masked = true,
                imeAction = ImeAction.Done
            )

            Spacer(Modifier.height(9.dp))

            // --------------------------------------------- ردیف لینک‌ها
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(Icons.Filled.Key, null, tint = TColor.Muted2, modifier = Modifier.size(8.dp))
                    Text(
                        "رمز عبور را فراموش کردی؟",
                        style = TText.micro.copy(fontSize = 8.25.sp),
                        color = TColor.Muted
                    )
                }
                Text("·", style = TText.micro, color = TColor.Faint)
                Text(
                    "ساخت حساب جدید از موبایل",
                    style = TText.micro.copy(fontSize = 8.25.sp),
                    color = TColor.Muted
                )
            }

            Spacer(Modifier.height(14.dp))

            // --------------------------------------------- دکمهٔ اصلی
            PrimaryButton(
                text = if (busy) "در حال ورود…" else "ورود به حساب",
                onClick = { if (!busy) confirmManual = true },
                modifier = Modifier.fillMaxWidth(0.86f)
            )

            if (errorText != null) {
                Spacer(Modifier.height(7.dp))
                ErrorLine(errorText, Modifier.fillMaxWidth(0.86f))
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "این دستگاه فقط نمایش‌دهنده است؛ ورود روی تلویزیون تغییرات حساب موبایل را نشان می‌دهد و چیزی را تغییر نمی‌دهد.",
                style = TText.micro.copy(fontSize = 7.5.sp),
                color = TColor.Muted2,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        // ============================================================ QR
        Box(
            Modifier
                .width(px(660))
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            // وضعیت جفت‌سازی → محتوای کارت
            val waiting = pairState as? PairState.Waiting
            val payload = waiting?.let { qrPayload(it.pairId, it.code, it.nonce) }

            QrCard(
                title = "ورود سریع با موبایل",
                subtitle = when (pairState) {
                    is PairState.Waiting -> "کد را با دوربین اپ یادمان اسکن کن"
                    is PairState.Starting -> "در حال دریافت کد جفت‌سازی…"
                    is PairState.Expired -> "کد منقضی شد — کد تازه بگیر"
                    is PairState.Denied -> "درخواست از سمت گوشی رد شد"
                    is PairState.Error -> "اتصال برقرار نشد — دوباره تلاش کن"
                    is PairState.Approved -> "تأیید شد؛ در حال ورود…"
                    PairState.Idle -> "برای شروع، اپ یادمان را روی گوشی باز کن"
                },
                steps = listOf(
                    "اپ یادمان را روی گوشی باز کن",
                    "از منو، «اتصال به تلویزیون» را بزن",
                    "کد بالا را اسکن کن — تمام!"
                ),
                modifier = Modifier.fillMaxWidth(),
                payload = payload,
                code = waiting?.code,
                secondsLeft = waiting?.remainingSeconds,
                qrSize = 240.dp
            )

            // دکمهٔ «کد جدید» فقط وقتی کد منقضی/رد شده است
            if (pairState is PairState.Expired || pairState is PairState.Denied || pairState is PairState.Error) {
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
                    GhostButton("دریافت کد جدید", onRetryPair, small = true)
                }
            }
        }
    }

    // ----------------------------------------- هشدار ورود دستی (قاعدهٔ سند)
    if (confirmManual) {
        ManualLoginWarning(
            onDismiss = { confirmManual = false },
            onConfirm = {
                confirmManual = false
                onSubmit()
            }
        )
    }
}

/**
 * دیالوگ هشدار: ورود دستی روی تلویزیون، نشست گوشی را خارج می‌کند.
 * توصیهٔ همیشگی: از QR استفاده کن.
 */
@Composable
private fun ManualLoginWarning(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .width(px(600))
                .clip(RoundedCornerShape(TD.rLg))
                .background(TColor.Card)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text("ورود دستی روی تلویزیون", style = TText.titleS, color = TColor.Ink)
            Text(
                "با ورود دستی، نشست حساب روی گوشی شما خارج می‌شود و باید دوباره روی گوشی وارد شوی. " +
                        "برای تلویزیون همیشه «ورود سریع با موبایل» (QR) پیشنهاد می‌شود.",
                style = TText.micro,
                color = TColor.Muted
            )
            Text(
                "این دستگاه فقط‌خواندنی می‌ماند؛ هیچ پرداختی از تلویزیون انجام نمی‌شود.",
                style = TText.micro.copy(fontSize = 7.5.sp),
                color = TColor.Muted2
            )
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                PrimaryButton("می‌دانم، وارد شو", onConfirm, small = true)
                GhostButton("انصراف", onDismiss, small = true)
            }
        }
    }
}

/**
 * فیلد ورودی تلویزیون — ارتفاع ۴۲dp (۸۴px)، برچسب بالای فیلد،
 * فوکوس: حلقهٔ طلایی + هالهٔ طلایی (بخش ۱۳.۲ سند)
 * این نسخه با BasicTextField است تا تایپ واقعی با کیبورد تلویزیون کار کند.
 */
@Composable
private fun TvField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    masked: Boolean = false,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            style = TText.micro.copy(fontSize = 7.sp),
            color = TColor.Muted2
        )
        FocusSurface(
            onClick = { runCatching { focusRequester?.requestFocus() } },
            modifier = Modifier.fillMaxWidth(0.86f),
            radius = TD.rMd,
            baseColor = TColor.Input,
            borderColor = TColor.Line,
            liftOnFocus = 0.dp,
            focusRequester = focusRequester
        ) { focused ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(TD.fieldH)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    cursorBrush = SolidColor(TColor.Gold),
                    textStyle = TText.bodyS.copy(
                        fontSize = 11.5.sp,
                        color = TColor.Ink2,
                        textAlign = TextAlign.End
                    ),
                    visualTransformation = if (masked) PasswordVisualTransformation('•')
                    else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (masked) KeyboardType.Password else KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    icon,
                    null,
                    tint = if (focused) TColor.Gold else TColor.Muted2,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
