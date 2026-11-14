package com.hossein.yademan.tv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.core.toAsciiDigits
import com.hossein.yademan.tv.core.toFa
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.core.ratio
import com.hossein.yademan.tv.data.Bill
import com.hossein.yademan.tv.data.Cheque
import com.hossein.yademan.tv.data.DoseState
import com.hossein.yademan.tv.data.HeroItem
import com.hossein.yademan.tv.data.Installment
import com.hossein.yademan.tv.data.Medicine
import com.hossein.yademan.tv.data.Note
import com.hossein.yademan.tv.data.OverdueItem
import com.hossein.yademan.tv.data.Rent
import com.hossein.yademan.tv.data.SectionCard
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.data.UpcomingRow
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px

/**
 * ============================================================================
 *  فایل: ui/components/Cards.kt
 *  بخش: کارت‌های تخصصی تلویزیون
 *        HeroCard · UpcomingRow · OverdueCard · SectionTile · InstallmentRow
 *        ChequeCard · NoteCard · NotePreview · MedicineCard · RentCard
 *        BillRow · AccountPanel · QrCard
 * ============================================================================
 */

/** ردیف برچسب بالای نوار پیشرفت */
@Composable
private fun BarLabelRow(left: String, right: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(right, style = TText.micro, color = TColor.Muted2)
        Spacer(Modifier.weight(1f))
        Text(left, style = TText.micro.copy(fontSize = 6.75.sp), color = Color(0xFFD8DEE9))
    }
}

/**
 * ۹.۷ کارت هیرو خانه — حداقل ارتفاع ۱۰۵dp (۲۱۰px)
 */
@Composable
fun HeroCard(
    item: HeroItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rLg,
        baseBackground = TBrush.card
    ) { focused ->
        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 13.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(item.title, style = TText.title, color = TColor.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.subtitle, style = TText.caption, color = TColor.Muted2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(8.dp))
                IconTile(item.color, item.icon, IcoSize.MD)
            }

            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                BarLabelRow(item.progressLabel, item.percent)
                ProgressBar(
                    fraction = item.fraction,
                    color = item.color,
                    brush = if (item.color == TColor.Gold) TBrush.barGold else TBrush.bar(item.color)
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Chip(item.chip, item.tone, small = true)
                Spacer(Modifier.weight(1f))
                if (item.amount > 0) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(money(item.amount), style = TText.amountRow, color = TColor.Gold)
                        Spacer(Modifier.width(2.dp))
                        Text("تومان", style = TText.micro, color = TColor.Muted2)
                    }
                } else {
                    Text(item.chip, style = TText.bodyS, color = TColor.PurpleInk)
                }
            }
        }
    }
}

/** ردیف «پیش‌رو» (۹.۸ RowItem) */
@Composable
fun UpcomingRowView(
    row: UpcomingRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rSm,
        baseColor = Color(0x05FFFFFF),
        borderColor = TColor.LineSoft
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconTile(row.color, row.icon, IcoSize.SM)
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(row.title, style = TText.bodyS, color = TColor.Ink2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (row.amount > 0) {
                    Text(money(row.amount), style = TText.micro.copy(fontSize = 7.sp), color = TColor.GoldInk)
                } else {
                    Text("بدون مبلغ", style = TText.micro, color = TColor.Muted2)
                }
            }
            Chip(row.chip, row.tone, small = true)
        }
    }
}

/** کارت «عقب‌افتاده» (قرمز ملایم) */
@Composable
fun OverdueCard(
    item: OverdueItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rMd,
        baseColor = Color(0x14FF6B81),
        borderColor = Color(0x38FF6B81)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            IconTile(TColor.Red, item.icon, IcoSize.SM)
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(item.title, style = TText.bodyS, color = TColor.RedInk, maxLines = 1)
                Text(item.meta, style = TText.micro, color = Color(0xFFB98A93), maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(money(item.amount), style = TText.titleS, color = TColor.RedInk)
                Text("تومان", style = TText.micro.copy(fontSize = 6.sp), color = Color(0xFFB98A93))
            }
        }
    }
}

/**
 * ۹.۹ تایل بخش (۶×۳ در صفحهٔ «همهٔ بخش‌ها»)
 */
@Composable
fun SectionTile(
    card: SectionCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null
) {
    val c = card.type.color
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        focusRequester = focusRequester,
        radius = px(24),
        baseBackground = TBrush.tile,
        borderColor = TColor.Line
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Chip("${card.count}", card.tone, small = true)
                Spacer(Modifier.weight(1f))
                IconTile(c, card.type.icon, IcoSize.TILE)
            }
            Text(
                card.type.title,
                style = TText.titleS,
                color = TColor.Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                card.subtitle,
                style = TText.micro,
                color = TColor.Muted2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // «خط بعدی»
            Row(
                Modifier
                    .clip(RoundedCornerShape(TD.rPill))
                    .background(Color.White.copy(alpha = 0.035f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Dot(c, 3.5.dp)
                Text(
                    card.nextLine,
                    style = TText.micro.copy(fontSize = 6.25.sp),
                    color = Color(0xFF96A2B6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(1.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressBar(
                    fraction = card.percent / 100f,
                    color = c,
                    height = TD.barSmallH,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                Text("${card.percent}٪", style = TText.micro.copy(fontSize = 6.25.sp), color = TColor.Muted2)
            }
        }
    }
}

/**
 * ۹.۱۰ کارت قسط — گرید 1fr / 340px / 300px
 */
@Composable
fun InstallmentRowView(
    inst: Installment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rLg2,
        baseBackground = TBrush.card,
        focusRequester = focusRequester
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // سمت راست: آیکون + عنوان + زیرنویس
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                IconTile(inst.color, inst.icon, IcoSize.MD)
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(inst.title, style = TText.title, color = TColor.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(inst.bankLine, style = TText.caption, color = TColor.Muted2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // وسط: قسط X از Y + نوار
            Column(
                Modifier
                    .width(px(340))
                    .padding(horizontal = 11.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                BarLabelRow(ratio(inst.index, inst.total), inst.percentText)
                ProgressBar(inst.fraction, inst.color, brush = TBrush.bar(inst.color))
            }

            // چپ: مبلغ + چیپ وضعیت
            Column(
                Modifier.width(px(300)),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(money(inst.amount), style = TText.amountInst, color = TColor.Gold)
                    Spacer(Modifier.width(2.dp))
                    Text("تومان", style = TText.micro, color = TColor.Muted2)
                }
                Chip(inst.status, inst.tone, small = true)
            }
        }
    }
}

/**
 * ۹.۱۱ کارت چک — با بافت ژیلوش و لهجهٔ رنگی
 */
@Composable
fun ChequeCard(
    c: Cheque,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rLg,
        baseBackground = TBrush.card,
        borderColor = c.accent.copy(alpha = 0.18f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(c.accent, Icons.Filled.Receipt, IcoSize.MD)
                Spacer(Modifier.width(7.dp))
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(c.title, style = TText.bodyS, color = TColor.Ink2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${c.bank} · ${c.branch}", style = TText.micro, color = TColor.Muted2, maxLines = 1)
                }
                Chip(c.chip, c.tone, small = true)
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(money(c.amount), style = TText.amountCheque, color = TColor.Ink)
                        Spacer(Modifier.width(2.dp))
                        Text("تومان", style = TText.micro, color = TColor.Muted2)
                    }
                    Text(c.planLine, style = TText.micro, color = TColor.Muted2, maxLines = 1)
                }
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text("شمارهٔ سری", style = TText.micro.copy(fontSize = 6.sp), color = TColor.Faint)
                    Text(
                        c.serial,
                        style = TText.bodyS.copy(fontSize = 7.5.sp),
                        color = TColor.Muted,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(c.progressText, style = TText.micro, color = TColor.Muted2)
                    ProgressBar(
                        fraction = c.fraction,
                        color = c.accent,
                        height = TD.barSmallH,
                        brush = TBrush.bar(c.accent),
                        modifier = Modifier.width(px(180))
                    )
                }
                Chip(c.owner, TTone.NEUTRAL, icon = Icons.Filled.Group, small = true)
            }
        }
    }
}

/**
 * ۹.۱۲ کارت یادداشت
 */
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = px(22),
        baseBackground = TBrush.cardSoft
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 11.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            IconTile(TColor.Amber, Icons.Filled.Receipt, IcoSize.MD)
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(note.title, style = TText.titleS, color = TColor.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(note.meta, style = TText.micro, color = TColor.Muted2)
                    if (note.sharedWith != null) {
                        Chip("مشترک با ${note.sharedWith}", TTone.NEUTRAL, small = true)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Chip(ratio(note.done, note.total), TTone.GOLD, small = true)
                ProgressBar(
                    fraction = note.progress,
                    color = TColor.Gold,
                    height = 2.5.dp,
                    modifier = Modifier.width(px(120))
                )
            }
        }
    }
}

/**
 * پنل پیش‌نمایش چک‌لیست (خطوط دفتری + آیتم‌های قابل تیک)
 */
@Composable
fun NotePreview(
    note: Note,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .clip(RoundedCornerShape(TD.rLg))
            .background(TBrush.cardSoft)
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rLg))
            .padding(horizontal = 17.dp, vertical = 15.dp)
    ) {
        // هدر پنل
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(note.title, style = TText.h3, color = TColor.Ink)
                Text(
                    "ساخت امروز ۱۹:۱۰ · وضعیت: در جریان",
                    style = TText.micro,
                    color = TColor.Muted2,
                    maxLines = 1
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Chip(ratio(note.done, note.total), TTone.GOLD, small = true)
                Chip("مشترک با ${note.sharedWith ?: "—"}", TTone.NEUTRAL, small = true)
            }
        }

        Spacer(Modifier.height(9.dp))

        // آیتم‌ها
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            note.items.forEach { item ->
                val selected = item.id == note.selectedItemId && !item.done
                FocusSurface(
                    onClick = { onToggle(item.id) },
                    modifier = Modifier.fillMaxWidth(),
                    radius = px(14),
                    baseColor = if (selected) Color(0x17E8B84B) else Color.White.copy(alpha = 0.016f),
                    borderColor = if (selected) Color(0x59E8B84B) else TColor.LineSoft
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        // چک‌باکس ۱۴dp (۲۸px)
                        Box(
                            Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(4.5.dp))
                                .background(
                                    if (item.done) {
                                        Brush.verticalGradient(listOf(TColor.GreenInk, Color(0xFF2FBE86)))
                                    } else {
                                        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                                    }
                                )
                                .border(
                                    if (item.done) 0.dp else 0.9.dp,
                                    Color(0xFF33405A),
                                    RoundedCornerShape(4.5.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.done) {
                                Icon(Icons.Filled.Check, null, tint = Color(0xFF08301F), modifier = Modifier.size(7.5.dp))
                            }
                        }
                        Text(
                            item.text,
                            style = TText.bodyS.copy(fontSize = 10.5.sp),
                            color = if (item.done) Color(0xFF78849A) else TColor.Ink2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(9.dp))
        HDivider()

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("با OK مورد انتخاب‌شده را انجام‌شده کن", style = TText.micro, color = TColor.Hint)
            Spacer(Modifier.weight(1f))
            ProgressBar(
                fraction = note.progress,
                color = TColor.Gold,
                height = TD.barSmallH,
                modifier = Modifier.width(px(220))
            )
            Spacer(Modifier.width(5.dp))
            Chip("${(note.progress * 100).toInt()}٪", TTone.GOLD, small = true)
        }
    }
}

/**
 * ۹.۱۳ کارت دارو — ۳ نوبت کنار هم
 */
@Composable
fun MedicineCard(
    m: Medicine,
    onDose: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = { },
        modifier = modifier,
        radius = TD.rLg,
        baseBackground = TBrush.card
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(m.name, style = TText.title, color = TColor.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(m.doseText, style = TText.micro, color = TColor.Muted2, maxLines = 1)
                }
                Spacer(Modifier.width(7.dp))
                IconTile(m.color, Icons.Filled.Receipt, IcoSize.MD)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                m.doses.forEachIndexed { i, d ->
                    val (bg, fg, icon) = when (d.state) {
                        DoseState.TAKEN -> Triple(Color(0x173ED598), TColor.GreenInk, Icons.Filled.CheckCircle)
                        DoseState.NOW -> Triple(Color(0x1AE8B84B), TColor.Gold, Icons.Filled.AccessTime)
                        else -> Triple(Color.White.copy(alpha = 0.025f), TColor.Muted2, Icons.Filled.AccessTime)
                    }
                    FocusSurface(
                        onClick = { onDose(i) },
                        modifier = Modifier.weight(1f),
                        radius = TD.rXs,
                        baseColor = bg,
                        borderColor = TColor.LineSoft
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(icon, null, tint = fg, modifier = Modifier.size(9.5.dp))
                            Text(
                                d.time,
                                style = TText.micro.copy(fontSize = 7.75.sp),
                                color = fg
                            )
                            Text(d.slot, style = TText.nano.copy(fontSize = 6.25.sp), color = TColor.Muted2)
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(m.course, style = TText.micro, color = TColor.Muted2, maxLines = 1)
                Spacer(Modifier.width(5.dp))
                ProgressBar(
                    fraction = m.fraction,
                    color = m.color,
                    height = TD.barSmallH,
                    brush = TBrush.bar(m.color),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(5.dp))
                Chip(m.chip, m.tone, small = true)
            }
        }
    }
}

/**
 * ۹.۱۴ کارت اجاره — مبلغ بزرگ + مسیر ۱۲ ماهه
 */
@Composable
fun RentCard(
    r: Rent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rXl,
        baseBackground = Brush.verticalGradient(listOf(Color(0xFF15202E), Color(0xFF0F1622))),
        borderColor = TColor.Blue.copy(alpha = 0.20f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 19.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(r.title, style = TText.h2s, color = TColor.Ink)
                    Text(r.ownerLine, style = TText.micro, color = Color(0xFF9FB6C7), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconTile(TColor.Blue, Icons.Filled.Home, IcoSize.MD)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                r.chips.forEach { Chip(it, TTone.BLUE, small = true) }
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text("مبلغ این ماه", style = TText.micro, color = Color(0xFF9FB6C7))
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(money(r.amount), style = TText.amountRent, color = TColor.Ink)
                    Spacer(Modifier.width(3.dp))
                    Text("تومان", style = TText.bodyS, color = Color(0xFF9FB6C7))
                }
            }

            // سه کاشی
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RentKv("ودیعه", money(r.deposit), Modifier.weight(1f))
                RentKv("مدت قرارداد", "${r.months} ماه", Modifier.weight(1f))
                RentKv("وضعیت این ماه", "پرداخت‌نشده", Modifier.weight(1f), TColor.GoldInk)
            }

            // نوار ماه ۱۰/۱۲
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                BarLabelRow("ماه ${r.thisMonth} از ${r.months}", "${(r.thisMonth * 100 / r.months)}٪")
                ProgressBar(
                    fraction = r.thisMonth.toFloat() / r.months,
                    color = TColor.Blue,
                    brush = TBrush.bar(TColor.Blue)
                )
            }

            // مسیر ۱۲ ماهه
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                (1..r.months).forEach { month ->
                    val done = month <= r.paidMonths
                    val now = month == r.thisMonth
                    Box(
                        Modifier
                            .weight(1f)
                            .height(21.dp)
                            .clip(RoundedCornerShape(TD.rTiny))
                            .background(
                                when {
                                    now -> Color(0x2E4CC3FF)
                                    done -> Color(0x1A3ED598)
                                    else -> Color.White.copy(alpha = 0.03f)
                                }
                            )
                            .border(
                                if (now) 1.dp else 0.5.dp,
                                when {
                                    now -> TColor.Blue.copy(alpha = 0.5f)
                                    done -> Color(0x333ED598)
                                    else -> TColor.LineSoft
                                },
                                RoundedCornerShape(TD.rTiny)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$month",
                            style = TText.micro.copy(fontSize = 7.5.sp),
                            color = when {
                                now -> TColor.BlueInk
                                done -> TColor.GreenInk
                                else -> TColor.Faint
                            }
                        )
                    }
                }
            }

            Text(
                r.endNote,
                style = TText.micro,
                color = Color(0xFF9FB6C7),
                maxLines = 2
            )

            HDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(r.dueLine, style = TText.micro.copy(fontSize = 6.5.sp), color = TColor.Muted2)
                    Text(r.nextPay, style = TText.micro.copy(fontSize = 7.sp), color = TColor.Ink2)
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("پایان قرارداد", style = TText.micro.copy(fontSize = 6.5.sp), color = TColor.Muted2)
                    Text(r.endDate, style = TText.micro.copy(fontSize = 7.sp), color = TColor.Ink2)
                }
                Spacer(Modifier.weight(1f))
                Chip(r.status, TTone.GREEN, small = true)
            }
        }
    }
}

@Composable
private fun RentKv(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = TColor.Ink2) {
    Column(
        modifier
            .clip(RoundedCornerShape(TD.rXs))
            .background(Color.White.copy(alpha = 0.03f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(label, style = TText.micro.copy(fontSize = 6.5.sp), color = TColor.Muted2, maxLines = 1)
        Text(value, style = TText.micro.copy(fontSize = 7.5.sp), color = valueColor, maxLines = 1)
    }
}

/** ۹.۱۵ ردیف قبض — ارتفاع ۴۲dp (۸۴px) */
@Composable
fun BillRowView(
    bill: Bill,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rMd,
        baseColor = Color.White.copy(alpha = 0.018f),
        borderColor = TColor.LineSoft
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(TD.billH)
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            IconTile(bill.color, bill.icon, IcoSize.MD)
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(bill.title, style = TText.bodyS.copy(fontSize = 10.sp), color = TColor.Ink2, maxLines = 1)
                Text(bill.provider, style = TText.micro, color = TColor.Muted2, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(money(bill.amount), style = TText.bodyS.copy(fontSize = 10.5.sp), color = TColor.Ink)
                Spacer(Modifier.width(2.dp))
                Text("تومان", style = TText.micro.copy(fontSize = 6.sp), color = TColor.Muted2)
            }
            Spacer(Modifier.width(4.dp))
            Chip(
                bill.daysChip,
                if (bill.daysChip.contains("گذشته")) TTone.RED else TTone.NEUTRAL,
                small = true
            )
        }
    }
}

/** ردیف مجموع قبوض */
@Composable
fun BillTotalRow(total: Long, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TD.rMd))
            .background(Color(0x0DE8B84B))
            .border(1.dp, TColor.GoldLine, RoundedCornerShape(TD.rMd))
            .padding(horizontal = 11.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text("مجموع قبوض آبان", style = TText.bodyS.copy(fontSize = 9.sp), color = Color(0xFFF2E6C8))
            Text("بدون اجاره", style = TText.micro, color = Color(0xFFA99163))
        }
        Text(money(total), style = TText.titleS, color = TColor.GoldLight)
        Spacer(Modifier.width(2.dp))
        Text("تومان", style = TText.micro, color = Color(0xFFA99163))
    }
}

/**
 * ۹.۱۶ پنل حساب — قالب مشترک ۴ پنل
 */
@Composable
fun AccountPanel(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(TD.rLg))
            .background(TBrush.card)
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rLg))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = TColor.Gold, modifier = Modifier.size(9.dp))
            Spacer(Modifier.width(4.dp))
            Text(title, style = TText.bodyS.copy(fontSize = 8.75.sp), color = Color(0xFFB9C2D2))
        }
        HDivider()
        content()
    }
}

/**
 * ۹.۱۷ کارت QR — برای صفحهٔ ورود و قبوض
 */
@Composable
fun QrCard(
    title: String,
    subtitle: String,
    steps: List<String>,
    modifier: Modifier = Modifier,
    smallQr: Boolean = false,
    /** محتوای واقعی QR؛ اگر null باشد قاب نمونه (همان طراحی) نمایش داده می‌شود */
    payload: String? = null,
    /** کد ۶ رقمی که زیر QR با اندازهٔ ۳۲sp می‌آید */
    code: String? = null,
    /** ثانیه‌های باقی‌ماندهٔ اعتبار کد */
    secondsLeft: Int? = null,
    /** اندازهٔ قاب QR — طبق سند ۲۴۰dp */
    qrSize: Dp = TD.qr
) {
    Column(
        modifier
            .clip(RoundedCornerShape(TD.rXxl))
            .background(
                Brush.verticalGradient(listOf(Color(0x14E8B84B), Color(0xFF101623)))
            )
            .border(1.dp, TColor.GoldLine, RoundedCornerShape(TD.rXxl))
            .padding(horizontal = 20.dp, vertical = 17.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title, style = TText.titleS, color = TColor.Ink, textAlign = TextAlign.Center)
        Text(subtitle, style = TText.micro, color = TColor.Muted2, textAlign = TextAlign.Center)

        // قاب QR — اگر محتوای واقعی داریم با ZXing، وگرنه همان الگوی طراحی
        val frame = if (smallQr) TD.qrSm else qrSize
        if (payload != null) {
            RealQr(payload = payload, size = frame)
        } else {
            QrPlaceholder(frame)
        }

        // کد ۶ رقمی زیر QR (۳۲sp طبق سند) + شمارش معکوس
        if (code != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                code.toFaHyphenated(),
                style = TText.amountRent.copy(fontSize = 32.sp, letterSpacing = 5.sp),
                color = TColor.Ink
            )
            if (secondsLeft != null) {
                Spacer(Modifier.height(1.dp))
                Text(
                    "اعتبار کد: ${formatCountdown(secondsLeft)}",
                    style = TText.micro,
                    color = if (secondsLeft <= 30) TColor.Red else TColor.Muted2
                )
            }
        }

        Spacer(Modifier.height(2.dp))
        steps.forEachIndexed { i, step ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(
                    Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(TColor.GoldSoft)
                        .border(1.dp, TColor.GoldLine, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${i + 1}", style = TText.nano.copy(fontSize = 6.sp), color = TColor.GoldInk)
                }
                Text(step, style = TText.micro.copy(fontSize = 6.5.sp), color = TColor.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/** قاب QR شبیه‌سازی‌شده: مربع سفید + الگوی نقطه‌ای طلایی */
@Composable
private fun QrPlaceholder(size: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 6))
            .background(Color.White)
            .padding(size / 12),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(size - size / 6)) {
            val n = 9
            val cell = this.size.minDimension / n
            for (row in 0 until n) {
                for (col in 0 until n) {
                    val on = (row * 7 + col * 3 + row * col) % 5 < 2
                    if (on) {
                        drawRect(
                            color = Color(0xFF0B0E14),
                            topLeft = androidx.compose.ui.geometry.Offset(col * cell, row * cell),
                            size = androidx.compose.ui.geometry.Size(cell * 0.86f, cell * 0.86f)
                        )
                    }
                }
            }
            // سه گوشهٔ Finder
            listOf(0 to 0, 0 to n - 3, n - 3 to 0).forEach { (r, c) ->
                drawRect(
                    color = Color(0xFF0B0E14),
                    topLeft = androidx.compose.ui.geometry.Offset(c * cell, r * cell),
                    size = androidx.compose.ui.geometry.Size(cell * 3, cell * 3)
                )
                drawRect(
                    color = Color.White,
                    topLeft = androidx.compose.ui.geometry.Offset(c * cell + cell * 0.5f, r * cell + cell * 0.5f),
                    size = androidx.compose.ui.geometry.Size(cell * 2, cell * 2)
                )
                drawRect(
                    color = Color(0xFF0B0E14),
                    topLeft = androidx.compose.ui.geometry.Offset(c * cell + cell, r * cell + cell),
                    size = androidx.compose.ui.geometry.Size(cell, cell)
                )
            }
        }
    }
}

/** ردیف دستگاه متصل */
@Composable
fun DeviceRow(
    name: String,
    meta: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rSm,
        baseColor = Color.White.copy(alpha = 0.02f),
        borderColor = TColor.LineSoft
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconTile(if (primary) TColor.Green else TColor.Muted2, Icons.Filled.Home, IcoSize.SM)
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(name, style = TText.bodyS.copy(fontSize = 8.sp), color = TColor.Ink2, maxLines = 1)
                Text(meta, style = TText.micro.copy(fontSize = 6.5.sp), color = TColor.Muted2, maxLines = 1)
            }
            if (primary) Chip("این دستگاه", TTone.GREEN, small = true)
        }
    }
}

/** ردیف «بازگشت» کوچک برای فوتر */
@Composable
fun BackRow(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(TD.rPill))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, TColor.LineSoft, RoundedCornerShape(TD.rPill))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(Icons.Filled.KeyboardArrowLeft, null, tint = TColor.Muted, modifier = Modifier.size(9.dp))
        Text(text, style = TText.micro, color = TColor.Muted)
    }
}

/** آیکون هشدار برای پیام خطا */
@Composable
fun ErrorLine(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TD.rSm))
            .background(Color(0x1FFF6B81))
            .border(1.dp, Color(0x42FF6B81), RoundedCornerShape(TD.rSm))
            .padding(horizontal = 9.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(Icons.Filled.Warning, null, tint = TColor.RedInk, modifier = Modifier.size(9.dp))
        Text(text, style = TText.micro.copy(fontSize = 7.5.sp), color = TColor.RedInk)
    }
}

/** ردیف پلن اشتراک (۴ کاشی) */
@Composable
fun PlanTile(
    label: String,
    price: Long,
    suggested: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = TD.rSm,
        baseColor = if (suggested) Color(0x1AE8B84B) else Color.White.copy(alpha = 0.025f),
        borderColor = if (suggested) TColor.GoldLine else TColor.LineSoft
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(label, style = TText.micro.copy(fontSize = 6.5.sp), color = TColor.Muted2)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    money(price),
                    style = TText.micro.copy(fontSize = 7.5.sp),
                    color = if (suggested) TColor.GoldInk else TColor.Ink2
                )
                Spacer(Modifier.width(1.5.dp))
                Text("ت", style = TText.nano.copy(fontSize = 5.5.sp), color = TColor.Muted2)
            }
            if (suggested) {
                Text("پیشنهادی", style = TText.nano.copy(fontSize = 5.5.sp), color = TColor.Gold)
            }
        }
    }
}

/** کارت آیکون خالی برای سرویس‌های بدون صفحهٔ اختصاصی */
@Composable
fun ServicePlaceholderCard(
    title: String,
    body: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TD.rLg))
            .background(TBrush.card)
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rLg))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconTile(color, icon, IcoSize.LG)
        Text(title, style = TText.titleS, color = TColor.Ink, textAlign = TextAlign.Center)
        Text(body, style = TText.micro, color = TColor.Muted2, textAlign = TextAlign.Center)
    }
}

/** دو آواتار روی هم (حساب دونفره) */
@Composable
fun PairAvatars(size: androidx.compose.ui.unit.Dp = 26.dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        GoldAvatar("م", size)
        GoldAvatar("ز", size)
    }
}

/** ردیف کاشی وضعیت کوچک (پرداخت‌شده/مانده/بعدی) */
@Composable
fun SmallStat(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .clip(RoundedCornerShape(TD.rSm))
            .background(color.copy(alpha = 0.09f))
            .border(1.dp, color.copy(alpha = 0.22f), RoundedCornerShape(TD.rSm))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(label, style = TText.micro.copy(fontSize = 6.25.sp), color = TColor.Muted2, maxLines = 1)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = TText.micro.copy(fontSize = 7.75.sp), color = color, maxLines = 1)
            Spacer(Modifier.width(1.5.dp))
            Text("تومان", style = TText.nano.copy(fontSize = 5.5.sp), color = TColor.Muted2)
        }
    }
}

/** آیکون کیف پول پیش‌فرض برای ردیف‌های بدون آیکون */
val DefaultWalletIcon = Icons.Filled.AccountBalanceWallet

// ---------------------------------------------------------------------------
//  کمک‌تابع‌های صفحهٔ ورود
// ---------------------------------------------------------------------------

/** «۴۸۲۹۱۳» → «۴۸۲ ۹۱۳» برای خوانایی بیشتر */
internal fun String.toFaHyphenated(): String {
    val d = toAsciiDigits()
    if (d.length != 6) return toFa()
    return (d.substring(0, 3) + " " + d.substring(3)).toFa()
}

/** ۲۴۵ → «۴:۰۵» */
internal fun formatCountdown(seconds: Int): String {
    val s = seconds.coerceAtLeast(0)
    val m = s / 60
    val r = s % 60
    return "$m:${if (r < 10) "0$r" else "$r"}".toFa()
}
