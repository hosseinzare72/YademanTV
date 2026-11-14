package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hossein.yademan.tv.data.Medicine
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.MedicineCard
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText

/**
 * ============================================================================
 *  فایل: ui/screens/MedicinesScreen.kt
 *  صفحهٔ ۰۹ — داروها (اسکرین 09-medicines.png)
 *  گرید ۳ ستون × ۲ ردیف؛ هر کارت ۳ نوبت قابل لمس با OK
 * ============================================================================
 */
@Composable
fun MedicinesScreen(
    medicines: List<Medicine>,
    onBack: () -> Unit,
    onDose: (String, Int) -> Unit
) {
    // تقسیم ۶ دارو به دو ردیف ۳ تایی
    val rows = medicines.chunked(3)

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        PageHead(
            title = "داروها",
            subtitle = "${medicines.size} داروی فعال · ۲ نوبت باقی‌ماندهٔ امروز",
            onBack = onBack,
            trailing = {
                Chip("نوبت بعدی: ۲۲:۰۰", TTone.GOLD, small = true)
                Chip("۲ نوبت مانده", TTone.BLUE, small = true)
            }
        )

        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rows.forEach { rowItems ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { m ->
                        MedicineCard(
                            m = m,
                            onDose = { index -> onDose(m.id, index) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    }
                    // اگر ردیف کمتر از ۳ آیتم داشت، فاصله‌ها حفظ شود
                    repeat(3 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        RemoteHint(
            "▲▼◀▶" to "جابه‌جایی",
            "OK" to "تیک نوبت",
            "Back" to "بازگشت"
        )
    }
}
