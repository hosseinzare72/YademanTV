package com.hossein.yademan.tv.ui.theme

import androidx.compose.ui.unit.dp

/**
 * ============================================================================
 *  فایل: ui/theme/TDimens.kt
 *  بخش: اندازه‌ها، فاصله‌ها و شعاع گوشه‌ها — بوم ۱۹۲۰×۱۰۸۰ با density 320
 *  قاعدهٔ تبدیل: dp = px ÷ 2  →  fun px(v:Int) = (v * 0.5f).dp
 * ============================================================================
 */

/** تبدیل px بوم طراحی به dp */
fun px(v: Int) = (v * 0.5f).dp

object TD {
    // ------------------------------------------------- فاصله‌های اصلی
    val railW = 62.dp          // ۱۲۴px
    val side = 28.dp           // ۵۶px (راست)
    val sideL = 26.dp          // ۵۲px (چپ)
    val top = 17.dp            // ۳۴px
    val bottom = 34.dp         // ۶۸px (نوار ریموت)
    val gap2 = 1.dp
    val gap4 = 2.dp
    val gap6 = 3.dp
    val gap8 = 4.dp
    val gap10 = 5.dp
    val gap12 = 6.dp
    val gap14 = 7.dp
    val gap16 = 8.dp
    val gap18 = 9.dp
    val gap20 = 10.dp
    val gap22 = 11.dp
    val gap24 = 12.dp
    val gap26 = 13.dp
    val gap28 = 14.dp
    val gap30 = 15.dp
    val gap34 = 17.dp
    val gap38 = 19.dp
    val gap44 = 22.dp
    val gap56 = 28.dp

    // ------------------------------------------------------- شعاع‌ها
    val rXxl = 16.dp           // ۳۲px — کارت QR
    val rXl = 14.dp            // ۲۸px — کارت اجاره
    val rLg = 13.dp            // ۲۶px — کارت بزرگ
    val rLg2 = 12.dp           // ۲۴px — کارت لیست/تایل
    val rMd = 10.dp            // ۲۰px — ردیف/پنل
    val rSm = 9.dp             // ۱۸px — ردیف لیست
    val rXs = 8.dp             // ۱۶px — آیکون‌تایل/فیلد
    val rTiny = 6.dp           // ۱۲px — نوار ماه
    val rMini = 4.5.dp         // ۹px  — کلید ریموت
    val rPill = 999.dp

    // ------------------------------------------------------ اجزا
    val icoMd = 31.dp          // ۶۲px آیکون‌تایل
    val icoLg = 40.dp          // ۸۰px
    val icoSm = 24.dp          // ۴۸px
    val icoTile = 33.dp        // ۶۶px تایل بخش
    val railBrand = 30.dp      // ۶۰px
    val avatar = 29.dp         // ۵۸px
    val chipH = 19.dp          // ۳۸px
    val chipSmallH = 15.dp     // ۳۰px
    val tabH = 26.dp           // ۵۲px
    val btnH = 40.dp           // ۸۰px
    val btnSmH = 29.dp         // ۵۸px
    val fieldH = 42.dp         // ۸۴px
    val barH = 4.dp            // ۸px
    val barSmallH = 3.dp       // ۶px
    val ring = 150.dp          // ۳۰۰px
    val ringStroke = 9.dp      // ۱۸px
    val qr = 150.dp            // ۳۰۰px
    val qrSm = 59.dp           // ۱۱۸px
    val billH = 42.dp          // ۸۴px
    val keyHintH = 15.dp       // ۳۰px
    val keyHintMinW = 17.dp    // ۳۴px
}

/** شکل‌های آماده */
val RPill = androidx.compose.foundation.shape.RoundedCornerShape(TD.rPill)
val RCardLg = androidx.compose.foundation.shape.RoundedCornerShape(TD.rLg)
val RCard = androidx.compose.foundation.shape.RoundedCornerShape(TD.rLg2)
val RRow = androidx.compose.foundation.shape.RoundedCornerShape(TD.rSm)
val RField = androidx.compose.foundation.shape.RoundedCornerShape(TD.rMd)
