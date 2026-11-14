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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.IcoSize
import com.hossein.yademan.tv.ui.components.IconTile
import com.hossein.yademan.tv.ui.components.InstallmentRowView
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.components.SectionHead
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.data.DataHub
import com.hossein.yademan.tv.ui.components.rememberFirstFocus

/**
 * ============================================================================
 *  فایل: ui/screens/InstallmentsScreen.kt
 *  صفحهٔ ۰۵ — اقساط (اسکرین 05-installments.png)
 *  سه کارت خلاصه + لیست ۴ قسط با گرید 1fr / 340px / 300px
 * ============================================================================
 */
@Composable
fun InstallmentsScreen(
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        PageHead(
            title = "اقساط",
            subtitle = "۴ قسط فعال · مجموع ${money(DataHub.INST_TOTAL_ACTIVE)} تومان",
            onBack = onBack,
            trailing = { Chip("آبان ۱۴۰۵", TTone.GOLD, small = true) }
        )

        // ------------------------------------------------ سه کارت خلاصه
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(
                icon = Icons.Filled.AccountBalanceWallet,
                color = TColor.Gold,
                label = "قسط این ماه",
                value = money(DataHub.INST_THIS_MONTH),
                unit = "تومان",
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                icon = Icons.Filled.CheckCircle,
                color = TColor.Green,
                label = "پرداخت‌شده تا امروز",
                value = "${DataHub.INST_PAID_COUNT}",
                unit = "قسط",
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                icon = Icons.Filled.Payments,
                color = TColor.Purple,
                label = "باقی‌ماندهٔ کل",
                value = money(DataHub.INST_REMAINING),
                unit = "تومان",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        SectionHead(
            title = "همهٔ اقساط",
            subtitle = "مرتب‌شده بر اساس نزدیک‌ترین سررسید"
        )

        Spacer(Modifier.height(6.dp))

        val firstFocus = rememberFirstFocus()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(DataHub.installments, key = { _, i -> i.id }) { idx, inst ->
                InstallmentRowView(
                    inst = inst,
                    onClick = { onOpen(inst.id) },
                    modifier = Modifier.fillMaxWidth(),
                    // فوکوس اول روی اولین کارت قسط
                    focusRequester = if (idx == 0) firstFocus else null
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        RemoteHint(
            "▲▼" to "انتخاب قسط",
            "OK" to "جزئیات",
            "Back" to "بازگشت"
        )
    }
}

/** کارت خلاصهٔ بالای صفحهٔ اقساط */
@Composable
private fun SummaryCard(
    icon: ImageVector,
    color: Color,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clip(RoundedCornerShape(TD.rMd))
            .background(TColor.Card.copy(alpha = 0.7f))
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rMd))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        IconTile(color, icon, IcoSize.SM)
        Column(horizontalAlignment = Alignment.End) {
            Text(label, style = TText.micro, color = TColor.Muted2, maxLines = 1)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = TText.titleS, color = color)
                Spacer(Modifier.width(2.dp))
                Text(unit, style = TText.nano, color = TColor.Muted2)
            }
        }
    }
}
