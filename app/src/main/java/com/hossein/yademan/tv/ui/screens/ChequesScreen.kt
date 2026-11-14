package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.components.ChequeCard
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.GhostButton
import com.hossein.yademan.tv.ui.components.HDivider
import com.hossein.yademan.tv.ui.components.IcoSize
import com.hossein.yademan.tv.ui.components.IconTile
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px
import com.hossein.yademan.tv.data.DataHub

/**
 * ============================================================================
 *  فایل: ui/screens/ChequesScreen.kt
 *  صفحهٔ ۰۷ — چک‌ها (اسکرین 07-cheques.png)
 *  گرید ۲×۲: سه چک + کارت خلاصهٔ طلایی
 * ============================================================================
 */
@Composable
fun ChequesScreen(onBack: () -> Unit, onSync: () -> Unit) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        PageHead(
            title = "چک‌ها",
            subtitle = "۳ چک در جریان · ${money(DataHub.CHEQUES_TOTAL)} تومان",
            onBack = onBack,
            trailing = { Chip("آبان ۱۴۰۵", TTone.PURPLE, small = true) }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ------------------------------------ ستون راست: دو چک اول
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ChequeCard(
                    c = DataHub.cheques[0],
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                ChequeCard(
                    c = DataHub.cheques[1],
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            // ---------------------------- ستون چپ: چک سوم + کارت خلاصه
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ChequeCard(
                    c = DataHub.cheques[2],
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                // کارت خلاصهٔ چک‌ها (طلایی)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(TD.rLg))
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color0Card, Color0Card2)
                            )
                        )
                        .border(1.dp, TColor.GoldLine, RoundedCornerShape(TD.rLg))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(TColor.Gold, Icons.Filled.CheckCircle, IcoSize.MD)
                        Spacer(Modifier.width(7.dp))
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("خلاصهٔ چک‌ها", style = TText.bodyS, color = TColor.Ink)
                            Text("۶ ماه گذشته · حساب دونفره", style = TText.micro, color = TColor.Muted2)
                        }
                        Chip("اشتراک ویژه", TTone.GOLD, small = true)
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(money(DataHub.CHEQUES_TOTAL), style = TText.amountInst, color = TColor.Gold)
                        Spacer(Modifier.width(2.dp))
                        Text("تومان", style = TText.micro, color = TColor.Muted2)
                    }

                    HDivider()

                    Row {
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("نزدیک‌ترین سررسید", style = TText.nano.copy(fontSize = 6.sp), color = TColor.Muted2)
                            Text("۲۵ آبان", style = TText.micro.copy(fontSize = 7.5.sp), color = TColor.Ink2)
                        }
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("پاس‌شده در ۶ ماه", style = TText.nano.copy(fontSize = 6.sp), color = TColor.Muted2)
                            Text("۱۸ فقره", style = TText.micro.copy(fontSize = 7.5.sp), color = TColor.GreenInk)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Chip("دونفره با مریم", TTone.GREEN, icon = Icons.Filled.Group, small = true)
                        Spacer(Modifier.weight(1f))
                        GhostButton("به‌روزرسانی", onSync, icon = Icons.Filled.Refresh, small = true)
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        RemoteHint("◀▶▲▼" to "جابه‌جایی", "OK" to "جزئیات چک", "Back" to "بازگشت")
    }
}

/** رنگ‌های پایهٔ کارت خلاصه */
private val Color0Card = androidx.compose.ui.graphics.Color(0xFF1A1A22)
private val Color0Card2 = androidx.compose.ui.graphics.Color(0xFF121722)
