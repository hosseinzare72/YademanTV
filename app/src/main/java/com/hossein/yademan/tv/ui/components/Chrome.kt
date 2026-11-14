package com.hossein.yademan.tv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px
import com.hossein.yademan.tv.data.DataHub

/**
 * ============================================================================
 *  فایل: ui/components/Chrome.kt
 *  بخش: اسکلت مشترک تلویزیون — ریل ناوبری، نوار بالا، نشان اشتراک، آواتار،
 *        سربرگ صفحه، نوار راهنمای ریموت
 * ============================================================================
 */

/** آیتم‌های ریل ناوبری (بخش ۹.۱ سند — به همین ترتیب) */
enum class RailDest(val label: String, val icon: ImageVector) {
    HOME("خانه", Icons.Filled.Home),
    SECTIONS("بخش‌ها", Icons.Filled.GridView),
    INSTALLMENTS("اقساط", Icons.Filled.AccountBalanceWallet),
    CHEQUES("چک‌ها", Icons.Filled.Receipt),
    NOTES("یادداشت", Icons.Filled.Assignment),
    MEDICINES("داروها", Icons.Filled.MedicalServices),
    BILLS("قبوض", Icons.Filled.Bolt),
    ACCOUNT("حساب", Icons.Filled.Person),
    EXIT("خروج", Icons.Filled.PowerSettingsNew)
}

/**
 * ریل سمت راست — عرض ۶۲dp (۱۲۴px)، تمام ارتفاع.
 * دکمهٔ فعال: متن طلایی + پس‌زمینهٔ طلایی ۱۰٪ + نوار نشانگر ۱.۵×۱۳dp.
 */
@Composable
fun NavRail(
    current: RailDest,
    onSelect: (RailDest) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .width(TD.railW)
            .fillMaxHeight()
            .background(TBrush.rail)
            .border(1.dp, TColor.Line)
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // برند
        Box(
            Modifier
                .size(TD.railBrand)
                .clip(RoundedCornerShape(px(20)))
                .background(TBrush.mark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = "یادمان",
                tint = Color(0xFF141006),
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(13.dp))

        val main = listOf(
            RailDest.HOME, RailDest.SECTIONS, RailDest.INSTALLMENTS, RailDest.CHEQUES,
            RailDest.NOTES, RailDest.MEDICINES, RailDest.BILLS, RailDest.ACCOUNT
        )
        main.forEach { dest ->
            RailButton(dest, active = current == dest) { onSelect(dest) }
            Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.weight(1f))
        Box(
            Modifier
                .width(20.dp)
                .height(1.dp)
                .background(TColor.LineStrong)
        )
        Spacer(Modifier.height(6.dp))
        RailButton(RailDest.EXIT, active = false) { onSelect(RailDest.EXIT) }
    }
}

/** دکمهٔ ریل — عرض ۴۶dp (۹۲px) */
@Composable
private fun RailButton(dest: RailDest, active: Boolean, onClick: () -> Unit) {
    Box {
        FocusSurface(
            onClick = onClick,
            modifier = Modifier.width(46.dp),
            radius = px(16),
            baseBackground = if (active) {
                Brush.verticalGradient(listOf(TColor.GoldSoft, TColor.GoldSoft))
            } else null,
            baseColor = Color.Transparent,
            borderColor = if (active) TColor.GoldLine else Color.Transparent
        ) {
            Column(
                Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    dest.icon,
                    null,
                    tint = if (active) TColor.Gold else TColor.Muted2,
                    modifier = Modifier.size(13.5.dp)
                )
                Text(
                    dest.label,
                    style = TText.micro.copy(fontSize = 6.75.sp),
                    color = if (active) TColor.Gold else TColor.Muted2,
                    maxLines = 1
                )
            }
        }
        // نشانگر ۳×۲۶ در سمت چپ (در RTL: end)
        if (active) {
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .width(1.5.dp)
                    .height(13.dp)
                    .clip(RoundedCornerShape(TD.rPill))
                    .background(TBrush.barGold)
            )
        }
    }
}

/** آواتار طلایی با حرف اول نام */
@Composable
fun GoldAvatar(letter: String = "ز", size: androidx.compose.ui.unit.Dp = TD.avatar) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3))
            .background(TBrush.avatar),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, style = TText.title.copy(fontSize = (size.value * 0.38f).sp), color = TColor.OnGold)
    }
}

/** نشان اشتراک ویژه (قاب طلایی کوچک) */
@Composable
fun PremiumBadge(text: String = "اشتراک ویژه") {
    Row(
        Modifier
            .height(21.dp)
            .clip(RoundedCornerShape(TD.rPill))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0x29E8B84B), Color(0x10E8B84B))
                )
            )
            .border(1.dp, TColor.GoldLine, RoundedCornerShape(TD.rPill))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(Icons.Filled.Star, null, tint = TColor.Gold, modifier = Modifier.size(8.5.dp))
        Text(text, style = TText.micro.copy(fontSize = 7.sp), color = TColor.GoldInk, maxLines = 1)
    }
}

/**
 * نوار بالای صفحه (TopBar)
 * راست: عنوان h1 + زیرعنوان | چپ: نشان اشتراک، ساعت، آواتار
 */
@Composable
fun TopBar(
    title: String,
    subtitle: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    showPremium: Boolean = true,
    showClock: Boolean = true,
    onAvatar: () -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ------------------------- سمت راست: عنوان و زیرعنوان (RTL: start)
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(title, style = TText.h1, color = TColor.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                subtitle()
            }
        }

        Spacer(Modifier.width(9.dp))

        // ------------------------------------------- سمت چپ: نشان، ساعت، آواتار
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            if (showPremium) PremiumBadge()
            if (showClock) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(DataHub.CLOCK, style = TText.clock, color = Color(0xFFE9EDF5))
                    Text(DataHub.TODAY_LINE, style = TText.clockDate, color = TColor.Muted2)
                }
            }
            GoldAvatar("ز")
        }
    }
}

/** سربرگ صفحه‌های داخلی: دکمهٔ بازگشت + عنوان + ردیف چیپ‌ها در سمت چپ */
@Composable
fun PageHead(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            FocusSurface(
                onClick = onBack,
                modifier = Modifier.size(20.dp),
                radius = TD.rSm,
                baseColor = Color.White.copy(alpha = 0.05f),
                borderColor = TColor.Line
            ) {
                Box(Modifier.fillMaxWidth().height(20.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ArrowBack, "بازگشت", tint = TColor.Ink2, modifier = Modifier.size(10.dp))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(title, style = TText.h1, color = TColor.Ink, maxLines = 1)
                Text(subtitle, style = TText.caption.copy(fontSize = 8.sp), color = TColor.Muted2, maxLines = 1)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp), content = trailing)
        Spacer(Modifier.width(9.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(DataHub.CLOCK, style = TText.clock, color = Color(0xFFE9EDF5))
            Text(DataHub.TODAY_LINE, style = TText.clockDate, color = TColor.Muted2)
        }
        Spacer(Modifier.width(7.dp))
        GoldAvatar("ز")
    }
}

/** نوار وضعیت همگام‌سازی سبز (پنل حساب) */
@Composable
fun SyncStatusBar(title: String, body: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TD.rMd))
            .background(Color(0x0F3ED598))
            .border(1.dp, Color(0x293ED598), RoundedCornerShape(TD.rMd))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(Icons.Filled.Refresh, null, tint = TColor.GreenInk, modifier = Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = TText.bodyS.copy(fontSize = 8.5.sp), color = TColor.GreenInk)
            Text(body, style = TText.micro, color = TColor.Muted2, maxLines = 1)
        }
    }
}

