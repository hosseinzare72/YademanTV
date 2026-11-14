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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px

/**
 * ============================================================================
 *  فایل: ui/screens/SplashScreen.kt
 *  صفحهٔ ۰۱ — خوش‌آمدگویی / همگام‌سازی (اسکرین 01-splash.png)
 *  سه حلقهٔ تزئینی + مارک ۸۶dp + وردمارک طلایی + نوار لودینگ + نسخه
 * ============================================================================
 */
@Composable
fun SplashScreen(onFinish: () -> Unit) {

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        // ------------------------------------------------- حلقه‌های تزئینی
        listOf(
            Triple(260.dp, 0.09f, 1.dp),   // ۵۲۰px
            Triple(380.dp, 0.055f, 1.dp),  // ۷۶۰px
            Triple(520.dp, 0.035f, 1.dp)   // ۱۰۴۰px
        ).forEach { (d, a, w) ->
            Box(
                Modifier
                    .size(d)
                    .clip(CircleShape)
                    .border(w, TColor.Gold.copy(alpha = a), CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ------------------------------------------------------ مارک
            Box(
                Modifier
                    .size(px(172))
                    .clip(RoundedCornerShape(px(54)))
                    .background(TBrush.mark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.NotificationsActive,
                    contentDescription = null,
                    tint = Color(0xFF141006),
                    modifier = Modifier.size(px(92))
                )
            }

            Spacer(Modifier.height(13.dp))

            // --------------------------------------------------- وردمارک
            Text(
                "یادمان",
                style = TText.display,
                color = TColor.Gold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "همهٔ قسط‌ها، چک‌ها و قرارها؛ یک‌جا روی تلویزیون",
                style = TText.bodyL,
                color = Color(0xFF8D97AA),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(17.dp))

            // ------------------------------------------------ نوار لودینگ
            Box(
                Modifier
                    .width(px(340))
                    .height(px(5))
                    .clip(RoundedCornerShape(TD.rPill))
                    .background(Color(0xFF1C2331))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(0.64f)
                        .height(px(5))
                        .clip(RoundedCornerShape(TD.rPill))
                        .background(TBrush.barGold)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "در حال همگام‌سازی با سرور…",
                style = TText.caption,
                color = TColor.Hint,
                textAlign = TextAlign.Center
            )
        }

        // --------------------------------------------------- گوشه‌ها
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 26.dp, vertical = 17.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.Lock, null, tint = TColor.Faint, modifier = Modifier.size(8.dp))
                Text("kafezare.ir", style = TText.micro, color = TColor.Faint)
            }
            Spacer(Modifier.weight(1f))
            Text("نسخهٔ ۱٫۴ · تلویزیون", style = TText.micro, color = TColor.Faint)
        }
    }
}
