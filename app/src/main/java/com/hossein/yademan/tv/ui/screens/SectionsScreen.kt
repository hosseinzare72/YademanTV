package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.hossein.yademan.tv.data.SectionType
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.components.SectionTile
import com.hossein.yademan.tv.ui.components.TopBar
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.data.DataHub
import com.hossein.yademan.tv.ui.components.rememberFirstFocus

/**
 * ============================================================================
 *  فایل: ui/screens/SectionsScreen.kt
 *  صفحهٔ ۰۴ — همهٔ بخش‌ها (اسکرین 04-sections.png)
 *  گرید ۶ ستون × ۳ ردیف از ۱۸ سرویس؛ OK روی هر تایل صفحهٔ همان سرویس را باز می‌کند
 * ============================================================================
 */
@Composable
fun SectionsScreen(onOpen: (SectionType) -> Unit) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        TopBar(
            title = "همهٔ بخش‌ها",
            subtitle = {
                Text(
                    buildAnnotatedString {
                        append("۱۸ سرویس · ")
                        withStyle(SpanStyle(color = TColor.Gold, fontWeight = FontWeight.SemiBold)) {
                            append("${DataHub.ACTIVE_REMINDERS} یادآوری فعال")
                        }
                        append(" · یکی را با OK باز کن")
                    },
                    style = TText.body,
                    color = TColor.Muted,
                    maxLines = 1
                )
            }
        )

        Spacer(Modifier.height(10.dp))

        val firstFocus = rememberFirstFocus()

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            itemsIndexed(DataHub.sections, key = { _, c -> c.type.name }) { i, card ->
                SectionTile(
                    card = card,
                    onClick = { onOpen(card.type) },
                    modifier = Modifier.fillMaxWidth(),
                    // فوکوس اول روی تایل «اقساط» (اولین کارت)
                    focusRequester = if (i == 0) firstFocus else null
                )
            }
        }

        Spacer(Modifier.height(7.dp))
        RemoteHint(
            "◀▶▲▼" to "جابه‌جایی بین بخش‌ها",
            "OK" to "باز کردن بخش",
            "Back" to "خانه"
        )
    }
}
