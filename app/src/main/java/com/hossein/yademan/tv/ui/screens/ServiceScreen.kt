package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.data.SectionType
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.EmptyState
import com.hossein.yademan.tv.ui.components.IcoSize
import com.hossein.yademan.tv.ui.components.IconTile
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.components.SectionTile
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.data.DataHub

/**
 * ============================================================================
 *  فایل: ui/screens/ServiceScreen.kt
 *  صفحهٔ عمومی — ۱۱ سرویس باقی‌مانده از ۱۸ سرویس
 *  (اشتراک‌ها، قرار و جلسه، لیست خرید، امانت‌ها، شارژ ساختمان، مسافرت،
 *   بیمه‌ها، سرویس خودرو، حقوق، پول توجیبی، تولدها، سالگردها و بخش‌های سفارشی)
 *  چون در TV همه‌چیز فقط‌خواندنی است، این صفحه خلاصه و ردیف‌های همان سرویس
 *  را نشان می‌دهد و بقیه را به اپ موبایل ارجاع می‌دهد.
 * ============================================================================
 */
@Composable
fun ServiceScreen(
    type: SectionType,
    onBack: () -> Unit
) {
    val card = DataHub.sections.firstOrNull { it.type == type }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        PageHead(
            title = type.title,
            subtitle = card?.subtitle ?: "این بخش آماده است",
            onBack = onBack,
            trailing = {
                if (card != null) Chip("${card.count} مورد", card.tone, small = true)
                Chip("فقط‌خواندنی", TTone.NEUTRAL, small = true)
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --------------------------------- خلاصهٔ سرویس (ستون راست)
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text("خلاصهٔ بخش", style = TText.titleS, color = TColor.Ink3)
                if (card != null) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        items(DataHub.sections.take(6), key = { it.type.name }) { c ->
                            SectionTile(card = c, onClick = { })
                        }
                    }
                } else {
                    EmptyState(
                        icon = type.icon,
                        title = "هنوز داده‌ای برای این بخش نیست",
                        body = "به‌محض افزودن اولین یادآوری، این بخش پر می‌شود."
                    )
                }
            }

            // -------------------------- راهنمای «مدیریت از موبایل» (ستون چپ)
            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text("این بخش چه می‌کند؟", style = TText.titleS, color = TColor.Ink3)
                ServiceInfo(type)
                Spacer(Modifier.weight(1f))
                Text(
                    "در تلویزیون همه‌چیز فقط‌خواندنی است؛ برای افزودن، ویرایش یا پرداخت، اپ موبایل یادمان را باز کن.",
                    style = TText.micro,
                    color = TColor.Muted2
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        RemoteHint("▲▼" to "جابه‌جایی", "OK" to "انتخاب", "Back" to "بازگشت")
    }
}

/** توضیح کوتاه هر سرویس */
@Composable
private fun ServiceInfo(type: SectionType) {
    val body = when (type) {
        SectionType.SUBSCRIPTION -> "اشتراک‌های ماهانه مثل نتفلیکس و اسپاتیفای؛ سررسید بعدی ۳ آذر."
        SectionType.MEETING -> "قرارها و جلسه‌های هفته؛ یک مورد امروز ساعت ۱۷:۳۰."
        SectionType.SHOPPING -> "فهرست‌های خرید خانواده؛ ۲ فهرست با ۲ مورد باز."
        SectionType.TRUST -> "سند و امانت‌های سپرده‌شده؛ یک مورد نزد آقای رضایی."
        SectionType.BUILDING -> "شارژ ماهانهٔ ساختمان؛ یک مورد ۲ روز عقب‌افتاده."
        SectionType.TRAVEL -> "سفرهای پیش‌رو؛ کیش · ۳ آذر با چک‌لیست سفر."
        SectionType.INSURANCE -> "بیمهٔ بدنه، عمر و درمان؛ سه بیمه‌نامهٔ فعال."
        SectionType.CAR -> "سرویس دوره‌ای خودرو؛ ۱۰٬۰۰۰ کیلومتر کارکرد."
        SectionType.SALARY -> "حقوق ماهانه؛ پنجم هر ماه به حساب واریز می‌شود."
        SectionType.POCKET -> "پول توجیبی هفتگی؛ هر شنبه."
        SectionType.BIRTHDAY -> "تولدهای خانواده؛ بعدی: مریم ۲ آذر."
        SectionType.ANNIVERSARY -> "سالگردها؛ ۲۴ آبان · ۶ روز دیگر."
        else -> "این بخش در تلویزیون فقط نمایشی است."
    }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TD.rMd))
            .background(TColor.Card.copy(alpha = 0.5f))
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rMd))
            .padding(horizontal = 11.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconTile(type.color, type.icon, IcoSize.MD)
            Column(horizontalAlignment = Alignment.End) {
                Text(type.title, style = TText.bodyS, color = TColor.Ink2)
                Text(body, style = TText.micro, color = TColor.Muted2)
            }
        }
    }
}
