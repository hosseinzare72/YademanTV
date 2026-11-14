package com.hossein.yademan.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.ui.components.AccountPanel
import com.hossein.yademan.tv.ui.components.Chip
import com.hossein.yademan.tv.ui.components.GhostButton
import com.hossein.yademan.tv.ui.components.GoldAvatar
import com.hossein.yademan.tv.ui.components.HDivider
import com.hossein.yademan.tv.ui.components.IcoSize
import com.hossein.yademan.tv.ui.components.IconTile
import com.hossein.yademan.tv.ui.components.KvRowView
import com.hossein.yademan.tv.ui.components.PageHead
import com.hossein.yademan.tv.ui.components.PairAvatars
import com.hossein.yademan.tv.ui.components.PlanTile
import com.hossein.yademan.tv.ui.components.RemoteHint
import com.hossein.yademan.tv.ui.components.SyncStatusBar
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px
import com.hossein.yademan.tv.data.DataHub
import com.hossein.yademan.tv.core.toFa

/**
 * ============================================================================
 *  فایل: ui/screens/AccountScreen.kt
 *  صفحهٔ ۱۱ — حساب کاربری (اسکرین 11-account.png)
 *  گرید ۲×۲: پروفایل · اشتراک ویژه · همگام‌سازی · حساب دونفره
 * ============================================================================
 */
@Composable
fun AccountScreen(
    userName: String,
    userId: String,
    onBack: () -> Unit,
    onSync: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = TD.sideL, end = TD.side, top = TD.top, bottom = 8.dp)
    ) {
        PageHead(
            title = "حساب کاربری",
            subtitle = "$userName · اشتراک ویژه فعال",
            onBack = onBack,
            trailing = {
                Chip("تلویزیون نشیمن", TTone.NEUTRAL, small = true)
                Chip("ویژه", TTone.GOLD, small = true)
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ------------------------------------------- ستون راست
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // پنل پروفایل
                AccountPanel("پروفایل", Icons.Filled.Star, Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GoldAvatar("ز", px(80))
                        Spacer(Modifier.width(9.dp))
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(userName, style = TText.h3, color = TColor.Ink)
                            Text(userId, style = TText.micro.copy(fontSize = 7.5.sp), color = TColor.Muted)
                            Spacer(Modifier.height(3.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Chip("ویژه", TTone.GOLD, small = true)
                                Chip("عضو حساب دونفره", TTone.GREEN, small = true)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    DataHub.profileRows.forEach { KvRowView(it.label, it.value, it.ltr, it.color) }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        DataHub.securityChips.forEach { Chip(it, TTone.NEUTRAL, small = true) }
                    }
                }

                // پنل همگام‌سازی
                AccountPanel("همگام‌سازی", Icons.Filled.Sync, Modifier.weight(1f)) {
                    SyncStatusBar(
                        title = if (DataHub.offline) "حالت آفلاین — دادهٔ ذخیره‌شده" else "همه‌چیز به‌روز است",
                        body = "آخرین همگام‌سازی: ${DataHub.lastSyncLabel} · " +
                                "${DataHub.ACTIVE_REMINDERS.toFa()} رکورد"
                    )
                    Spacer(Modifier.height(3.dp))
                    DataHub.syncRows.forEach { KvRowView(it.label, it.value, it.ltr, it.color) }
                    Spacer(Modifier.height(4.dp))
                    GhostButton("همگام‌سازی دستی", onSync, modifier = Modifier.fillMaxWidth(), icon = Icons.Filled.Refresh, small = true)
                }
            }

            // ------------------------------------------- ستون چپ
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // پنل اشتراک ویژه
                AccountPanel("اشتراک ویژه", Icons.Filled.Shield, Modifier.weight(1f)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(TD.rSm))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0x1A3ED598), Color(0x1AE8B84B))
                                )
                            )
                            .border(1.dp, Color(0x333ED598), RoundedCornerShape(TD.rSm))
                            .padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Filled.Star, null, tint = TColor.GoldInk, modifier = Modifier.size(9.dp))
                        Text(
                            DataHub.PREMIUM_LINE,
                            style = TText.micro.copy(fontSize = 7.sp),
                            color = TColor.GreenInk,
                            maxLines = 2
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        DataHub.plans.forEach { plan ->
                            PlanTile(
                                label = plan.label,
                                price = plan.price,
                                suggested = plan.suggested,
                                onClick = { },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    DataHub.subscriptionRows.forEach { KvRowView(it.label, it.value, it.ltr, it.color) }
                }

                // پنل حساب دونفره
                AccountPanel("حساب دونفره", Icons.Filled.Group, Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PairAvatars(px(52))
                        Spacer(Modifier.width(7.dp))
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(DataHub.PARTNER_NAME, style = TText.bodyS, color = TColor.Ink2)
                            Text(
                                "شریک حساب · دسترسی نمایش و علامت‌گذاری",
                                style = TText.nano.copy(fontSize = 6.25.sp),
                                color = TColor.Muted2,
                                maxLines = 1
                            )
                        }
                        Chip("فعال", TTone.GREEN, small = true)
                    }
                    Spacer(Modifier.height(3.dp))
                    DataHub.partnerRows.forEach { KvRowView(it.label, it.value, it.ltr, it.color) }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        GhostButton("تغییر رمز", { }, modifier = Modifier.weight(1f), icon = Icons.Filled.Lock, small = true)
                        GhostButton("خروج از حساب", onLogout, danger = true, small = true, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        RemoteHint("▲▼◀▶" to "جابه‌جایی", "OK" to "انتخاب", "Back" to "بازگشت")
    }
}
