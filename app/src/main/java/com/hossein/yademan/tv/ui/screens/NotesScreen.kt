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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hossein.yademan.tv.data.Note
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.NoteCard
import com.hossein.yademan.tv.ui.components.NotePreview
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px

/**
 * ============================================================================
 *  فایل: ui/screens/NotesScreen.kt
 *  صفحهٔ ۰۸ — یادداشت‌ها (اسکرین 08-notes.png)
 *  ستون راست ۶۴۰px: لیست ۴ یادداشت | ستون چپ: پنل پیش‌نمایش چک‌لیست
 * ============================================================================
 */
@Composable
fun NotesScreen(
    notes: List<Note>,
    onBack: () -> Unit,
    onToggleItem: (String, String) -> Unit
) {
    var selectedId by remember { mutableStateOf(notes.first().id) }
    val selected = notes.firstOrNull { it.id == selectedId } ?: notes.first()

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        PageHead(
            title = "یادداشت‌ها",
            subtitle = "۴ یادداشت · ۹ مورد انجام‌نشده",
            onBack = onBack,
            trailing = { Chip("خرید هفتگی", TTone.GOLD, small = true) }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ----------------------------------- ستون راست: لیست یادداشت‌ها
            Column(
                Modifier
                    .width(px(640))
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { selectedId = note.id },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ------------------------------- ستون چپ: پنل پیش‌نمایش
            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("پیش‌نمایش چک‌لیست", style = TText.titleS, color = TColor.Ink3)
                    Spacer(Modifier.weight(1f))
                    Text(
                        "با OK آیتم انجام‌شده می‌شود",
                        style = TText.micro,
                        color = TColor.Muted2
                    )
                }
                NotePreview(
                    note = selected,
                    onToggle = { itemId -> onToggleItem(selected.id, itemId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        RemoteHint("▲▼" to "انتخاب یادداشت", "OK" to "تیک آیتم", "Back" to "بازگشت")
    }
}
