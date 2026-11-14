package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.data.SectionType
import com.hossein.yademan.tv.ui.components.HeroCard
import com.hossein.yademan.tv.ui.components.IconTile
import com.hossein.yademan.tv.ui.components.IcoSize
import com.hossein.yademan.tv.ui.components.MetricStrip
import com.hossein.yademan.tv.ui.components.OverdueCard
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.components.SectionHead
import com.hossein.yademan.tv.ui.components.TopBar
import com.hossein.yademan.tv.ui.components.TvTab
import com.hossein.yademan.tv.ui.components.UpcomingRowView
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px
import com.hossein.yademan.tv.data.DataHub
import com.hossein.yademan.tv.ui.components.rememberFirstFocus

/**
 * ============================================================================
 *  فایل: ui/screens/HomeScreen.kt
 *  صفحهٔ ۰۳ — یادآوری‌های امروز (اسکرین 03-home-today.png)
 *  ساختار: TopBar → تب‌ها → سه کارت امروز → دو پنل (پیش‌رو / عقب‌افتاده)
 *  این صفحه اولین صفحهٔ بعد از ورود است (طبق تصمیم طراحی)
 * ============================================================================
 */
@Composable
fun HomeScreen(
    onOpenSection: (SectionType) -> Unit,
    onOpenInstallments: () -> Unit,
    onOpenCheques: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        // --------------------------------------------------------- TopBar
        TopBar(
            title = "یادآوری‌ها",
            subtitle = {
                Text(
                    buildAnnotatedString {
                        append(DataHub.TODAY_LINE + " · ")
                        withStyle(SpanStyle(color = TColor.Gold, fontWeight = FontWeight.SemiBold)) {
                            append("${DataHub.TODAY_COUNT} مورد برای امروز")
                        }
                    },
                    style = TText.body,
                    color = TColor.Muted,
                    maxLines = 1
                )
            }
        )

        Spacer(Modifier.height(9.dp))

        // ---------------------------------------------------------- تب‌ها
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            val firstFocus = rememberFirstFocus()
            TvTab(
                "امروز", "${DataHub.TODAY_COUNT}", active = true, onClick = { },
                focusRequester = firstFocus
            )
            TvTab("پیش‌رو", "${DataHub.UPCOMING_COUNT}", active = false, onClick = { })
            TvTab("عقب‌افتاده", "${DataHub.OVERDUE_COUNT}", active = false, onClick = { })
            Spacer(Modifier.weight(1f))
            Text("آخرین همگام‌سازی ${DataHub.lastSyncLabel}", style = TText.micro, color = TColor.Muted2)
        }

        Spacer(Modifier.height(9.dp))

        // ------------------------------------------------ سه کارت امروز
        Row(
            Modifier
                .fillMaxWidth()
                .height(px(231)),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DataHub.heroItems.forEach { item ->
                HeroCard(
                    item = item,
                    onClick = {
                        // اگر دادهٔ واقعی است، مقصد از نوع همان رکورد می‌آید
                        val real = DataHub.typeForTitle(item.title)
                        when {
                            real != null -> onOpenSection(real)
                            item.title == "قسط وام باجت" -> onOpenInstallments()
                            item.title == "اجاره خانه" -> onOpenSection(SectionType.RENT)
                            else -> onOpenSection(SectionType.MEETING)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(9.dp))

        // ------------------------------------- پنل‌های پیش‌رو و عقب‌افتاده
        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------------- پنل عقب‌افتاده + میان‌برها + متریک
            Column(
                Modifier
                    .width(px(300)),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                OverdueCard(
                    item = DataHub.overdue,
                    onClick = { onOpenSection(SectionType.BUILDING) },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(TD.rMd))
                        .background(TColor.Card.copy(alpha = 0.55f))
                        .border(1.dp, TColor.Line, RoundedCornerShape(TD.rMd))
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("میان‌بر بخش‌ها", style = TText.micro, color = TColor.Muted2)
                    // گرید ۴×۲ از آیکون‌های کوچک
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DataHub.shortcuts.chunked(4).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                row.forEach { sc ->
                                    Column(
                                        Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        IconTile(sc.type.color, sc.type.icon, IcoSize.SM)
                                        Text(
                                            sc.type.title,
                                            style = TText.nano.copy(fontSize = 5.75.sp),
                                            color = TColor.Muted2,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                MetricStrip(DataHub.homeMetrics, Modifier.fillMaxWidth())
            }

            // ---------------- پنل پیش‌رو — ۱۴ روز آینده
            Column(
                Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                SectionHead(
                    title = "پیش‌رو — ۱۴ روز آینده",
                    subtitle = "۵ مورد نزدیک",
                    trailing = { Text("همهٔ ۵ مورد", style = TText.micro, color = TColor.GoldInk) }
                )
                DataHub.upcoming.forEach { row ->
                    UpcomingRowView(
                        row = row,
                        onClick = {
                            when (row.title) {
                                "چک شهر لوازم خانگی" -> onOpenCheques()
                                "قسط وام خودرو" -> onOpenInstallments()
                                "قبض برق" -> onOpenSection(SectionType.BILLS)
                                else -> onOpenSection(SectionType.ANNIVERSARY)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(5.dp))
        RemoteHint(
            "▲▼" to "جابه‌جایی",
            "◀▶" to "تب‌ها",
            "OK" to "باز کردن",
            "Back" to "خروج"
        )
    }
}
