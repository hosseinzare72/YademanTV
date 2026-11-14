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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.core.ratio
import com.hossein.yademan.tv.data.Installment
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.GhostButton
import com.hossein.yademan.tv.ui.components.IcoSize
import com.hossein.yademan.tv.ui.components.IconTile
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.PrimaryButton
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.components.RingProgress
import com.hossein.yademan.tv.ui.components.SmallStat
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px
import com.hossein.yademan.tv.data.DataHub

/**
 * ============================================================================
 *  فایل: ui/screens/InstallmentDetailScreen.kt
 *  صفحهٔ ۰۶ — جزئیات قسط (اسکرین 06-installment-detail.png)
 *  ستون راست: حلقهٔ پیشرفت + مبالغ | ستون چپ: کاشی‌ها، خط زمان، پرداخت‌ها، دکمه‌ها
 *  قاعدهٔ TV: دکمهٔ «پرداخت‌شده» فقط برای رکورد مشترک فعال است
 * ============================================================================
 */
@Composable
fun InstallmentDetailScreen(
    inst: Installment,
    onBack: () -> Unit
) {
    var markedPaid by remember { mutableStateOf(false) }
    // دادهٔ تکمیلی همین قسط (واقعی اگر از سرور آمده باشد، وگرنه مرجع طراحی)
    val det = com.hossein.yademan.tv.data.DataHub.detailFor(inst)

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        // ------------------------------------------ بردکرامب + سربرگ
        Text("اقساط › ${inst.title}", style = TText.micro.copy(fontSize = 7.75.sp), color = TColor.Muted2)
        PageHead(
            title = inst.title,
            subtitle = "${inst.bank} · ۲۴ قسط ماهانه · سررسید ۲۸ هر ماه",
            onBack = onBack,
            trailing = {
                Chip(if (markedPaid) "پرداخت شد" else "فعال", if (markedPaid) TTone.GREEN else TTone.GOLD, small = true)
                Chip(inst.dueLine, TTone.NEUTRAL, small = true)
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ======================================== ستون راست: کارت پیشرفت
            Column(
                Modifier
                    .width(px(540))
                    .clip(RoundedCornerShape(TD.rLg))
                    .background(TBrush.card)
                    .border(1.dp, TColor.Line, RoundedCornerShape(TD.rLg))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("پیشرفت بازپرداخت", style = TText.titleS, color = TColor.Ink)
                Text("${ratio(inst.index, inst.total)} · ${inst.percentText} انجام شده", style = TText.micro, color = TColor.Muted2)

                RingProgress(
                    fraction = inst.fraction,
                    percentText = inst.percentText,
                    label = "پرداخت‌شده",
                    bottomGold = ratio(inst.index - 1, inst.total)
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(money(inst.amount), style = TText.h2, color = TColor.Gold)
                    Spacer(Modifier.width(3.dp))
                    Text("تومان هر قسط", style = TText.micro, color = TColor.Muted2)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Chip("کل وام ${money(inst.totalLoan)}", TTone.NEUTRAL, small = true)
                    Chip("${inst.total - inst.index} قسط مانده", TTone.GOLD, small = true)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    SmallStat("پرداخت‌شده", money(det.paid), TColor.GreenInk, Modifier.weight(1f))
                    SmallStat("ماندهٔ وام", money(inst.remaining), TColor.PurpleInk, Modifier.weight(1f))
                    SmallStat("قسط بعدی", inst.dueLine, TColor.GoldInk, Modifier.weight(1f))
                }

                Text(
                    "قسط بعدی: ${inst.dueLine} — پرداخت خودکار از حساب بانک تجارت فعال نیست.",
                    style = TText.micro.copy(fontSize = 6.75.sp),
                    color = TColor.Muted2,
                    textAlign = TextAlign.Center
                )
            }

            // ============================== ستون چپ: اطلاعات، خط زمان، پرداخت‌ها
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                // ۴ کاشی اطلاعات
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    KvBox("بانک عامل", inst.bank, Modifier.weight(1f))
                    KvBox("شمارهٔ قرارداد", inst.contractNo, Modifier.weight(1f), ltr = true)
                    KvBox("مبلغ کل وام", money(inst.totalLoan), Modifier.weight(1f))
                    KvBox("مبلغ هر قسط", money(inst.amount), Modifier.weight(1f), valueColor = TColor.Gold)
                }

                // خط زمان ۶ گامه
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(TD.rMd))
                        .background(TColor.Card.copy(alpha = 0.55f))
                        .border(1.dp, TColor.Line, RoundedCornerShape(TD.rMd))
                        .padding(horizontal = 11.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("خط زمان بازپرداخت", style = TText.micro.copy(fontSize = 7.5.sp), color = TColor.Ink3)
                        Spacer(Modifier.weight(1f))
                        Chip("گام ${ratio(inst.index, inst.total)}", TTone.GOLD, small = true)
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        det.timeline.forEachIndexed { i, (label, done) ->
                            val current = i == inst.index
                            Column(
                                Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(if (current) 13.dp else 9.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                current -> TBrush.button
                                                done -> Brush.verticalGradient(
                                                    listOf(TColor.GreenInk, Color(0xFF2FBE86))
                                                )
                                                else -> Brush.verticalGradient(
                                                    listOf(TColor.Track, TColor.Track)
                                                )
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (current) TColor.Gold else Color.Transparent,
                                            CircleShape
                                        )
                                )
                                Text(
                                    label,
                                    style = TText.nano.copy(fontSize = 5.75.sp),
                                    color = if (current) TColor.GoldInk else TColor.Muted2,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (i < det.timeline.lastIndex) {
                                Box(
                                    Modifier
                                        .weight(0.25f)
                                        .height(1.dp)
                                        .background(if (done) Color(0x333ED598) else TColor.Line)
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Chip("پرداخت خودکار: خاموش", TTone.NEUTRAL, small = true)
                        Chip("یادآور ۳ روز قبل", TTone.BLUE, small = true)
                    }
                }

                // آخرین پرداخت‌ها
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(TD.rMd))
                        .background(TColor.Card.copy(alpha = 0.55f))
                        .border(1.dp, TColor.Line, RoundedCornerShape(TD.rMd))
                        .padding(horizontal = 11.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("آخرین پرداخت‌ها", style = TText.micro.copy(fontSize = 7.5.sp), color = TColor.Ink3)
                    det.payments.forEach { (title, sub, done) ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            IconTile(
                                if (done) TColor.Green else TColor.Muted2,
                                if (done) Icons.Filled.CheckCircle else Icons.Filled.Schedule,
                                IcoSize.SM
                            )
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text(title, style = TText.micro.copy(fontSize = 7.5.sp), color = TColor.Ink2, maxLines = 1)
                                Text(sub, style = TText.nano.copy(fontSize = 6.sp), color = TColor.Muted2, maxLines = 1)
                            }
                            Chip(
                                if (done) "موفق" else "در انتظار",
                                if (done) TTone.GREEN else TTone.GOLD,
                                small = true
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // دکمه‌ها
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    PrimaryButton(
                        text = if (markedPaid) "پرداخت ثبت شد" else "علامت‌گذاری به‌عنوان پرداخت‌شده",
                        onClick = { markedPaid = true },
                        icon = Icons.Filled.Check,
                        modifier = Modifier.weight(1f),
                        small = true
                    )
                    GhostButton("بازگشت", onBack, small = true)
                }
                Text(
                    "در تلویزیون فقط رکوردهای مشترک علامت‌گذاری می‌شوند؛ پرداخت از اپ موبایل انجام می‌شود.",
                    style = TText.nano.copy(fontSize = 6.25.sp),
                    color = TColor.Faint
                )
            }
        }

        Spacer(Modifier.height(5.dp))
        RemoteHint("▲▼" to "جابه‌جایی", "OK" to "انتخاب", "Back" to "اقساط")
    }
}

/** کاشی کلید/مقدار */
@Composable
private fun KvBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    ltr: Boolean = false,
    valueColor: Color = TColor.Ink2
) {
    Column(
        modifier
            .clip(RoundedCornerShape(TD.rSm))
            .background(Color.White.copy(alpha = 0.028f))
            .border(1.dp, TColor.LineSoft, RoundedCornerShape(TD.rSm))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, style = TText.nano.copy(fontSize = 6.25.sp), color = TColor.Muted2, maxLines = 1)
        Text(
            value,
            style = TText.micro.copy(fontSize = 7.75.sp, fontWeight = FontWeight.SemiBold),
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (ltr) TextAlign.Start else TextAlign.End
        )
    }
}
