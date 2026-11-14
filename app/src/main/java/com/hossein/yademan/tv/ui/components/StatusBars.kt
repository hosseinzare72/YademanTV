package com.hossein.yademan.tv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px

/**
 * ============================================================================
 *  فایل: ui/components/StatusBars.kt
 *  بخش: نوار «آفلاین» + دیالوگ «نشست منقضی» (بخش ۱۱ سند)
 *
 *  این دو، حالت‌های سراسری‌اند و روی همهٔ ۱۱ صفحه نمایش داده می‌شوند؛
 *  برای همین در لایهٔ ناوبری (YademanTvApp) رندر می‌شوند، نه داخل صفحه‌ها.
 * ============================================================================
 */

/** نوار زرد بالای صفحه وقتی سرور در دسترس نیست — دادهٔ قبلی نمایش داده می‌شود */
@Composable
fun OfflineBar(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = TD.side, vertical = 4.dp)
                .clip(RoundedCornerShape(TD.rPill))
                .background(Color(0x1FFFB84D))
                .border(1.dp, Color(0x4DFFB84D), RoundedCornerShape(TD.rPill))
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Filled.CloudOff,
                null,
                tint = TColor.Amber,
                modifier = Modifier.size(11.dp)
            )
            Text(
                "اتصال به سرور قطع است — آخرین دادهٔ ذخیره‌شده نمایش داده می‌شود",
                style = TText.micro,
                color = TColor.Amber,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box(Modifier.weight(1f))
            Text(
                "تلاش خودکار هر ۱۵ دقیقه",
                style = TText.micro.copy(fontSize = 6.75.sp),
                color = TColor.Muted2
            )
        }
    }
}

/**
 * دیالوگ «نشست منقضی شده است» — فقط یک دکمه: ورود دوباره.
 * طبق سند، تلویزیون نباید با خطای نامفهوم بماند.
 */
@Composable
fun SessionExpiredDialog(
    visible: Boolean,
    onLogin: () -> Unit
) {
    if (!visible) return
    Dialog(onDismissRequest = { }) {
        Column(
            Modifier
                .width(px(560))
                .clip(RoundedCornerShape(TD.rLg))
                .background(TColor.Card)
                .border(1.dp, TColor.LineStrong, RoundedCornerShape(TD.rLg))
                .padding(horizontal = 20.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    Modifier
                        .size(px(56))
                        .clip(RoundedCornerShape(TD.rXs))
                        .background(Color(0x1FFFB84D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Lock,
                        null,
                        tint = TColor.Amber,
                        modifier = Modifier.size(px(28))
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("نشست شما منقضی شده است", style = TText.titleS, color = TColor.Ink)
                    Text(
                        "برای دیدن یادآوری‌ها، این تلویزیون را دوباره به حساب وصل کن.",
                        style = TText.micro,
                        color = TColor.Muted2
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                PrimaryButton("ورود دوباره", onLogin, small = true)
            }
        }
    }
}

/** نوار کوچک وضعیت همگام‌سازی — برای سربرگ صفحه‌های اصلی */
@Composable
fun SyncBadge(text: String, syncing: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(TD.rPill))
            .background(TColor.Card)
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rPill))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            Modifier
                .size(5.dp)
                .clip(RoundedCornerShape(TD.rPill))
                .background(if (syncing) TColor.Gold else TColor.Green)
        )
        Text(
            if (syncing) "در حال همگام‌سازی…" else text,
            style = TText.micro.copy(fontSize = 7.sp),
            color = TColor.Muted
        )
    }
}

/** ارتفاع نوار بالا در چیدمان (برای padding صفحات) */
val StatusBarHeight = 26.dp

/** کمک‌کنندهٔ خالی برای همراستایی */
@Composable
fun Gap(modifier: Modifier = Modifier) {
    Box(modifier.height(0.dp))
}
