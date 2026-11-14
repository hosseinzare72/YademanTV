package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.data.Bill
import com.hossein.yademan.tv.data.Rent
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.components.BillRowView
import com.hossein.yademan.tv.ui.components.BillTotalRow
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.QrCard
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.components.RentCard
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px
import com.hossein.yademan.tv.data.DataHub

/**
 * ============================================================================
 *  فایل: ui/screens/BillsRentScreen.kt
 *  صفحهٔ ۱۰ — قبوض و اجاره (اسکرین 10-bills-rent.png)
 *  ستون چپ: کارت اجاره (۷۲۰px) | ستون راست: ۴ قبض + مجموع + کارت QR پرداخت
 * ============================================================================
 */
@Composable
fun BillsRentScreen(
    rent: Rent,
    bills: List<Bill>,
    onBack: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        PageHead(
            title = "قبوض و اجاره",
            subtitle = "۴ قبض این ماه · سررسید نزدیک: امروز",
            onBack = onBack,
            trailing = {
                Chip("اجاره: امروز", TTone.GOLD, small = true)
                Chip("۱۴۰۵", TTone.NEUTRAL, small = true)
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ------------------------------------ ستون چپ: کارت اجاره
            RentCard(
                r = rent,
                onClick = { },
                modifier = Modifier
                    .width(px(720))
                    .fillMaxSize()
            )

            // ------------------------------- ستون راست: قبوض + مجموع + QR
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("قبوض این ماه", style = TText.titleS, color = TColor.Ink3)
                    Spacer(Modifier.weight(1f))
                    Text("۴ قبض · آنلاین پرداخت نمی‌شود", style = TText.micro, color = TColor.Muted2)
                }

                bills.forEach { b ->
                    BillRowView(bill = b, onClick = { }, modifier = Modifier.fillMaxWidth())
                }

                BillTotalRow(DataHub.BILLS_TOTAL, Modifier.fillMaxWidth())

                Spacer(Modifier.weight(1f))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QrCard(
                        title = "پرداخت قبوض از موبایل",
                        subtitle = "کد را با گوشی اسکن کن",
                        steps = listOf("اپ یادمان را باز کن", "قبوض را پرداخت کن"),
                        smallQr = true,
                        modifier = Modifier.width(px(330))
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("تلویزیون فقط نمایش می‌دهد", style = TText.bodyS, color = TColor.Ink3)
                        Text(
                            "کد را با گوشی اسکن کن تا قبض را همان‌جا پرداخت کنی.",
                            style = TText.micro,
                            color = TColor.Muted2
                        )
                        Spacer(Modifier.height(2.dp))
                        Chip("مجموع ${money(DataHub.BILLS_TOTAL)} تومان", TTone.GOLD, small = true)
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        RemoteHint("◀▶" to "اجاره / قبوض", "OK" to "جزئیات", "Back" to "بازگشت")
    }
}
