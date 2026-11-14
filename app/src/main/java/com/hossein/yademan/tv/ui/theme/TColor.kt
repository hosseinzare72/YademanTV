package com.hossein.yademan.tv.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * ============================================================================
 *  فایل: ui/theme/TColor.kt
 *  بخش: پالت رنگ کامل نسخهٔ تلویزیون (هم‌رنگ اسکرین‌شات‌های TV)
 *  منبع: YADEMAN-TV-HANDOFF.md بخش ۳
 * ============================================================================
 */
object TColor {
    // ---------------------------------------------------------- پس‌زمینه
    val Bg = Color(0xFF05070A)          // پشت بوم
    val BgBase = Color(0xFF0A0D13)
    val BgDeep = Color(0xFF0B0E14)
    val BgTop = Color(0xFF0C1018)       // بالای گرادیان
    val BgMid = Color(0xFF090C12)       // ۵۵٪
    val BgBottom = Color(0xFF080B10)    // پایین

    // ------------------------------------------------------------- سطوح
    val Panel = Color(0xFF111722)       // ریل و پنل
    val Card = Color(0xFF141A26)
    val Card2 = Color(0xFF18202F)
    val Card3 = Color(0xFF1C2537)
    val Input = Color(0xFF121927)
    val CardTop = Color(0xFF161D2B)
    val CardBottom = Color(0xFF121826)

    // --------------------------------------------------------- خطوط
    val Line = Color(0x11FFFFFF)        // 6.5%
    val LineStrong = Color(0x1FFFFFFF)  // 12%
    val LineSoft = Color(0x0BFFFFFF)    // 4.5%
    val Track = Color(0xFF212A3B)
    val RingTrack = Color(0xFF1E2738)

    // ------------------------------------------------------ برند طلایی
    val Gold = Color(0xFFE8B84B)
    val GoldDeep = Color(0xFFDDAF4B)
    val GoldLight = Color(0xFFF7DE9B)
    val GoldDark = Color(0xFFB98A2A)
    val GoldMid = Color(0xFFC2912F)
    val GoldInk = Color(0xFFF0C868)
    val OnGold = Color(0xFF1A1206)
    val GoldSoft = Color(0x1AE8B84B)    // ۱۰٪
    val GoldLine = Color(0x42E8B84B)    // ۲۶٪

    // ---------------------------------------------------------- متن
    val Ink = Color(0xFFF3F5F9)
    val Ink2 = Color(0xFFE4E9F2)
    val Ink3 = Color(0xFFC3CBD9)
    val Muted = Color(0xFF98A2B6)
    val Muted2 = Color(0xFF687389)
    val Hint = Color(0xFF5E687C)
    val Faint = Color(0xFF4E5767)

    // ------------------------------------------------------ وضعیت‌ها
    val Green = Color(0xFF3ED598)
    val GreenInk = Color(0xFF5FE0AC)
    val Red = Color(0xFFFF6B81)
    val RedInk = Color(0xFFFF8D9C)
    val Blue = Color(0xFF4CC3FF)
    val BlueInk = Color(0xFF7BD3FF)
    val Purple = Color(0xFF9D8CFF)
    val PurpleInk = Color(0xFFB7ABFF)
    val Pink = Color(0xFFFF7A9E)
    val Orange = Color(0xFFFF8F5C)
    val Amber = Color(0xFFFFB84D)
    val Trust = Color(0xFF7FB5FF)

    /** رنگ سرویس‌ها — طبق جدول بخش ۳.۵ سند */
    fun service(type: String): Color = when (type) {
        "installment" -> Gold
        "check" -> Purple
        "rent" -> Blue
        "subscription" -> Orange
        "meeting" -> Blue
        "medicine" -> Pink
        "shopping" -> Green
        "note" -> Amber
        "trust" -> Trust
        "bills" -> Green
        "building" -> Red
        "travel" -> Gold
        "insurance" -> Blue
        "car" -> Amber
        "salary" -> Gold
        "pocket" -> Gold
        "birthday" -> Pink
        "anniversary" -> Red
        else -> Purple
    }
}

/** گرادیان‌های آمادهٔ برند (عیناً از استایل طراحی) */
object TBrush {
    /** دکمهٔ اصلی */
    val button = Brush.verticalGradient(listOf(Color(0xFFF3D07C), Color(0xFFE0AC44)))

    /** آواتار / مارک */
    val avatar = Brush.linearGradient(
        listOf(Color(0xFFF7DE9B), Color(0xFFE8B84B), Color(0xFFC2912F))
    )

    /** مارک بزرگ اسپلش */
    val mark = Brush.linearGradient(
        listOf(Color(0xFFFBEAB9), Color(0xFFE8B84B), Color(0xFFBC8B2B))
    )

    /** متن طلایی (روی عنوان) */
    val goldText = Brush.linearGradient(
        listOf(Color(0xFFFFF6DF), Color(0xFFF3D07C), Color(0xFFD9A33C), Color(0xFFB98A2A))
    )

    val card = Brush.verticalGradient(listOf(TColor.CardTop, TColor.CardBottom))
    val cardSoft = Brush.verticalGradient(listOf(Color(0xFF161D2C), Color(0xFF111725)))
    val tile = Brush.verticalGradient(listOf(Color(0xFF161D2C), Color(0xFF111725)))
    val rail = Brush.horizontalGradient(
        listOf(Color(0xEB10151F), Color(0xF5090C12))
    )

    /** نوار پیشرفت طلایی */
    val barGold = Brush.horizontalGradient(listOf(Color(0xFFD9A33C), Color(0xFFF7DE9B)))

    fun bar(color: Color) = Brush.horizontalGradient(
        listOf(color.copy(alpha = 0.72f), color)
    )

    /** هالهٔ نور از بالای کارت فوکوس‌شده */
    val focusGlow = Brush.radialGradient(
        listOf(Color(0x1AE8B84B), Color.Transparent)
    )
}

/** سطوح ملایم رنگ سرویس */
fun Color.softFill(): Color = copy(alpha = 0.12f)
fun Color.softLine(): Color = copy(alpha = 0.26f)
