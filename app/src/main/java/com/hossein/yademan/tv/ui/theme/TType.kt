package com.hossein.yademan.tv.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.R

/**
 * ============================================================================
 *  فایل: ui/theme/TType.kt
 *  بخش: فونت و تایپوگرافی تلویزیون
 *  فونت: Vazirmatn UI-FD (ارقام فارسی) — res/font/vazirmatn_*.ttf
 *  مقیاس: بوم ۱۹۲۰px با density 320 → sp ≈ px ÷ 2 (طبق جدول بخش ۴ سند)
 * ============================================================================
 */
val Vazir = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
    Font(R.font.vazirmatn_extrabold, FontWeight.ExtraBold),
    Font(R.font.vazirmatn_black, FontWeight.Black),
)

object TText {
    val display = TextStyle(fontFamily = Vazir, fontSize = 43.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
    val h1 = TextStyle(fontFamily = Vazir, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp)
    val h2 = TextStyle(fontFamily = Vazir, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
    val h2s = TextStyle(fontFamily = Vazir, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    val h3 = TextStyle(fontFamily = Vazir, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    val title = TextStyle(fontFamily = Vazir, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    val titleS = TextStyle(fontFamily = Vazir, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val bodyL = TextStyle(fontFamily = Vazir, fontSize = 11.sp, fontWeight = FontWeight.Normal)
    val body = TextStyle(fontFamily = Vazir, fontSize = 9.5.sp, fontWeight = FontWeight.Normal)
    val bodyS = TextStyle(fontFamily = Vazir, fontSize = 8.5.sp, fontWeight = FontWeight.Medium)
    val label = TextStyle(fontFamily = Vazir, fontSize = 8.25.sp, fontWeight = FontWeight.SemiBold)
    val caption = TextStyle(fontFamily = Vazir, fontSize = 7.5.sp, fontWeight = FontWeight.Normal)
    val micro = TextStyle(fontFamily = Vazir, fontSize = 6.75.sp, fontWeight = FontWeight.Medium)
    val nano = TextStyle(fontFamily = Vazir, fontSize = 6.25.sp, fontWeight = FontWeight.SemiBold)

    // مبالغ
    val amountHero = TextStyle(fontFamily = Vazir, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    val amountRow = TextStyle(fontFamily = Vazir, fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold)
    val amountInst = TextStyle(fontFamily = Vazir, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    val amountCheque = TextStyle(fontFamily = Vazir, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
    val amountRent = TextStyle(fontFamily = Vazir, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)

    // دکمه‌ها
    val button = TextStyle(fontFamily = Vazir, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val buttonSm = TextStyle(fontFamily = Vazir, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)

    // ساعت و تاریخ
    val clock = TextStyle(fontFamily = Vazir, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    val clockDate = TextStyle(fontFamily = Vazir, fontSize = 8.sp, fontWeight = FontWeight.Normal)
}

private val lh = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.None)

/** نگاشت تایپوگرافی Compose به مقیاس تلویزیون */
val TTypography = Typography(
    displayLarge = TText.display,
    headlineLarge = TText.h1,
    titleLarge = TText.h3,
    bodyLarge = TText.bodyL.copy(lineHeight = 17.sp, lineHeightStyle = lh),
    bodyMedium = TText.body.copy(lineHeight = 15.sp, lineHeightStyle = lh),
    labelLarge = TText.button,
    labelSmall = TText.nano,
)
