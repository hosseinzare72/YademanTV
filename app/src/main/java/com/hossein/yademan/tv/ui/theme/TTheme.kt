package com.hossein.yademan.tv.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * ============================================================================
 *  فایل: ui/theme/TTheme.kt
 *  بخش: تم سراسری تلویزیون + پس‌زمینهٔ ۴ لایه (RTL اجباری)
 * ============================================================================
 */

private val TScheme = darkColorScheme(
    primary = TColor.Gold,
    onPrimary = TColor.OnGold,
    secondary = TColor.GoldInk,
    background = TColor.BgTop,
    onBackground = TColor.Ink,
    surface = TColor.Card,
    onSurface = TColor.Ink,
    surfaceVariant = TColor.Card2,
    error = TColor.Red,
)

@Composable
fun YademanTvTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = TScheme,
            typography = TTypography,
            content = content
        )
    }
}

/**
 * پس‌زمینهٔ ۴ لایه (بخش ۶ سند):
 *  ۱) گرادیان پایه + هالهٔ طلایی بالا-راست + هالهٔ آبی پایین-چپ
 *  ۲) الگوی نقطه‌ای ۳۶px با ماسک
 *  ۳) وینیت
 *  ۴) محتوا
 */
@Composable
fun TvBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(TColor.BgTop, TColor.BgMid, TColor.BgBottom))
            )
    ) {
        // ۱) نورهای محیطی
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x1AE8B84B), Color.Transparent),
                        center = Offset(0.86f * 960f, -0.12f * 540f),
                        radius = 620f
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x134CC3FF), Color.Transparent),
                        center = Offset(0.02f * 960f, 1.08f * 540f),
                        radius = 520f
                    )
                )
        )

        // ۲) الگوی نقطه‌ای محو‌شونده به سمت راست-بالا
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    val step = 18.dp.toPx()   // ۳۶px
                    val r = 0.5.dp.toPx()
                    var y = 0f
                    while (y < size.height) {
                        var x = 0f
                        while (x < size.width) {
                            val dx = (size.width - x) / size.width
                            val dy = y / size.height
                            val fade = (1f - ((dx * dx * 0.85f + dy * dy * 1.15f))).coerceIn(0f, 1f)
                            if (fade > 0.06f) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.055f * fade * 0.75f),
                                    radius = r,
                                    center = Offset(x, y)
                                )
                            }
                            x += step
                        }
                        y += step
                    }
                }
        )

        // ۳) وینیت
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color(0x8C000000)),
                        center = Offset(480f, 216f),
                        radius = 760f
                    )
                )
        )

        // ۴) محتوا
        content()
    }
}
