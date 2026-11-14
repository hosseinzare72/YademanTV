package com.hossein.yademan.tv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hossein.yademan.tv.data.TTone
import com.hossein.yademan.tv.data.Metric
import com.hossein.yademan.tv.ui.theme.TBrush
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.px

/**
 * ============================================================================
 *  فایل: ui/components/Atoms.kt
 *  بخش: اجزای پایهٔ تلویزیون — کاشی آیکون، چیپ، نوار، حلقه، دکمه، کلید ریموت
 *  همهٔ اجزا فوکوس‌پذیرند: حلقهٔ طلایی + هاله + بالاآمدن (بخش ۵ و ۱۰ سند)
 * ============================================================================
 */

/** اندازهٔ کاشی آیکون (`.ico`) */
enum class IcoSize(val box: Dp, val radius: Dp, val icon: Dp) {
    SM(24.dp, 7.5.dp, 11.5.dp),
    MD(31.dp, 9.5.dp, 14.5.dp),
    LG(40.dp, 12.dp, 19.dp),
    TILE(33.dp, 10.5.dp, 15.dp)
}

/**
 * کاشی رنگی آیکون — ستون فقرات بصری اپ.
 * پس‌زمینهٔ گرادیان دو مرحله‌ای از رنگ سرویس + حاشیهٔ ۱px هم‌رنگ.
 */
@Composable
fun IconTile(
    color: Color,
    icon: ImageVector,
    size: IcoSize = IcoSize.MD,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .size(size.box)
            .clip(RoundedCornerShape(size.radius))
            .background(
                Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.22f), color.copy(alpha = 0.09f))
                )
            )
            .border(1.dp, color.copy(alpha = 0.30f), RoundedCornerShape(size.radius)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(size.icon))
    }
}

/** رنگ متن چیپ بر اساس معنا */
fun TTone.textColor(): Color = when (this) {
    TTone.GOLD -> TColor.GoldInk
    TTone.GREEN -> TColor.GreenInk
    TTone.RED -> TColor.RedInk
    TTone.BLUE -> TColor.BlueInk
    TTone.PURPLE -> TColor.PurpleInk
    TTone.ORANGE -> Color(0xFFFFB08A)
    TTone.NEUTRAL -> TColor.Muted
}

fun TTone.bgColor(): Color = when (this) {
    TTone.GOLD -> Color(0x21E8B84B)
    TTone.GREEN -> Color(0x1F3ED598)
    TTone.RED -> Color(0x1FFF6B81)
    TTone.BLUE -> Color(0x1F4CC3FF)
    TTone.PURPLE -> Color(0x219D8CFF)
    TTone.ORANGE -> Color(0x1FFF8F5C)
    TTone.NEUTRAL -> Color(0xFF1A2130)
}

fun TTone.lineColor(): Color = when (this) {
    TTone.GOLD -> Color(0x47E8B84B)
    TTone.GREEN -> Color(0x3D3ED598)
    TTone.RED -> Color(0x42FF6B81)
    TTone.BLUE -> Color(0x3D4CC3FF)
    TTone.PURPLE -> Color(0x429D8CFF)
    TTone.ORANGE -> Color(0x40FF8F5C)
    TTone.NEUTRAL -> TColor.Line
}

/**
 * چیپ — ارتفاع ۱۹dp (۳۸px) در حالت عادی، ۱۵dp (۳۰px) در حالت کوچک.
 */
@Composable
fun Chip(
    text: String,
    tone: TTone = TTone.NEUTRAL,
    icon: ImageVector? = null,
    small: Boolean = false,
    dot: Boolean = false,
    solid: Boolean = false,
    modifier: Modifier = Modifier
) {
    val h = if (small) TD.chipSmallH else TD.chipH
    val fs = if (small) 6.75.sp else 8.25.sp
    Row(
        modifier
            .height(h)
            .clip(RoundedCornerShape(TD.rPill))
            .background(if (solid) Brush.verticalGradient(listOf(Color(0xFFF3D07C), Color(0xFFE0AC44))) else Brush.verticalGradient(listOf(tone.bgColor(), tone.bgColor())))
            .border(1.dp, if (solid) Color.Transparent else tone.lineColor(), RoundedCornerShape(TD.rPill))
            .padding(horizontal = if (small) 6.dp else 7.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.5.dp)
    ) {
        if (dot) {
            Box(
                Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(tone.textColor())
            )
        }
        if (icon != null) {
            Icon(icon, null, tint = if (solid) TColor.OnGold else tone.textColor(), modifier = Modifier.size(if (small) 6.dp else 7.5.dp))
        }
        Text(
            text,
            style = TText.label.copy(fontSize = fs),
            color = if (solid) TColor.OnGold else tone.textColor(),
            maxLines = 1
        )
    }
}

/** نوار پیشرفت — ارتفاع پیش‌فرض ۴dp (۸px)، ریل #212A3B */
@Composable
fun ProgressBar(
    fraction: Float,
    color: Color = TColor.Gold,
    height: Dp = TD.barH,
    brush: Brush? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(TD.rPill))
            .background(TColor.Track)
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0.02f, 1f))
                .height(height)
                .clip(RoundedCornerShape(TD.rPill))
                .background(brush ?: TBrush.bar(color))
        )
    }
}

/** حلقهٔ پیشرفت — ۱۵۰dp (۳۰۰px)، خط ۹dp (۱۸px) */
@Composable
fun RingProgress(
    fraction: Float,
    percentText: String,
    label: String,
    bottomGold: String,
    modifier: Modifier = Modifier,
    size: Dp = TD.ring
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(Modifier.size(size)) {
            val stroke = TD.ringStroke.toPx()
            val inset = stroke / 2
            val diameter = this.size.minDimension - stroke
            drawArc(
                color = TColor.RingTrack,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
            )
            drawArc(
                color = TColor.Gold,
                startAngle = -90f,
                sweepAngle = 360f * fraction.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = stroke,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(percentText, style = TText.h2, color = TColor.Ink)
            Text(label, style = TText.caption, color = TColor.Muted2)
            Spacer(Modifier.height(3.dp))
            Text(bottomGold, style = TText.bodyS.copy(fontSize = 7.5.sp), color = TColor.GoldInk)
        }
    }
}

/** سربرگ بخش داخل صفحه: عنوان + خط تزئینی + اکشن سمت چپ */
@Composable
fun SectionHead(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(title, style = TText.title, color = TColor.Ink3, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(subtitle, style = TText.caption, color = TColor.Muted2, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(6.dp))
            trailing()
        }
    }
}

/** نوار متریک ۳ ستونه (خانه و اقساط) */
@Composable
fun MetricStrip(items: List<Metric>, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TD.rMd))
            .background(Color.White.copy(alpha = 0.025f))
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rMd))
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { i, m ->
            if (i > 0) {
                Box(
                    Modifier
                        .width(1.dp)
                        .height(17.dp)
                        .background(TColor.Line)
                )
            }
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(m.value, style = TText.bodyS.copy(fontSize = 9.sp), color = m.color ?: TColor.Ink)
                Text(m.label, style = TText.micro, color = TColor.Muted2, maxLines = 1)
            }
        }
    }
}

/**
 * دکمهٔ اصلی (Primary) — ارتفاع ۴۰dp (۸۰px)، تایپوگرافی ۱۱sp (۲۲px).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    small: Boolean = false,
    enabled: Boolean = true
) {
    FocusSurface(
        onClick = { if (enabled) onClick() },
        modifier = modifier,
        radius = if (small) px(16) else px(20),
        baseBackground = if (enabled) TBrush.button else null,
        baseColor = if (enabled) TColor.OnGold else Color(0xFF6B6252)
    ) { focused ->
        Row(
            Modifier
                .height(if (small) TD.btnSmH else TD.btnH)
                .padding(horizontal = if (small) 11.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, null, tint = if (enabled) TColor.OnGold else Color(0xFF6B6252), modifier = Modifier.size(if (small) 8.dp else 10.dp))
                Spacer(Modifier.width(3.dp))
            }
            Text(
                text,
                style = if (small) TText.buttonSm else TText.button,
                color = if (enabled) TColor.OnGold else Color(0xFF6B6252),
                maxLines = 1
            )
        }
    }
}

/** دکمهٔ شبح (Ghost) */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    small: Boolean = false,
    danger: Boolean = false
) {
    val base = if (danger) Color(0x1AFF6B81) else Color(0xFF141C2C)
    val lineC = if (danger) Color(0x42FF6B81) else TColor.LineStrong
    val txt = if (danger) Color(0xFFFF9AA7) else Color(0xFFDDE3EE)
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        radius = if (small) px(16) else px(20),
        baseBackground = null,
        baseColor = base,
        borderColor = lineC
    ) {
        Row(
            Modifier
                .height(if (small) TD.btnSmH else TD.btnH)
                .padding(horizontal = if (small) 11.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, null, tint = txt, modifier = Modifier.size(if (small) 8.dp else 10.dp))
                Spacer(Modifier.width(3.dp))
            }
            Text(text, style = if (small) TText.buttonSm else TText.button, color = txt, maxLines = 1)
        }
    }
}

/** تب‌های صفحهٔ خانه: «امروز ۳» / «پیش‌رو ۴» / «عقب‌افتاده ۱» */
@Composable
fun TvTab(
    label: String,
    count: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** فوکوس اول صفحهٔ خانه روی تب «امروز» است */
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        focusRequester = focusRequester,
        radius = TD.rPill,
        baseBackground = if (active) TBrush.button else null,
        baseColor = if (active) Color.Transparent else Color(0xFF111825),
        borderColor = if (active) Color.Transparent else TColor.Line
    ) {
        Row(
            Modifier
                .height(TD.tabH)
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                style = TText.label.copy(fontSize = 8.25.sp),
                color = if (active) TColor.OnGold else TColor.Muted
            )
            Text(
                count,
                style = TText.nano,
                color = if (active) TColor.OnGold.copy(alpha = 0.62f) else TColor.Muted2
            )
        }
    }
}

/** کلید ریموت در نوار راهنما */
@Composable
fun RemoteKey(text: String) {
    Box(
        Modifier
            .defaultMinSize(minWidth = TD.keyHintMinW, minHeight = TD.keyHintH)
            .clip(RoundedCornerShape(TD.rMini))
            .background(Color.White.copy(alpha = 0.045f))
            .border(1.dp, TColor.Line, RoundedCornerShape(TD.rMini))
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = TText.micro.copy(fontSize = 6.75.sp), color = TColor.Muted, maxLines = 1)
    }
}

/**
 * نوار راهنمای ریموت پایین صفحه — کلید + متن.
 * مثال: ▲▼ جابه‌جایی · OK انتخاب · Back بازگشت
 */
@Composable
fun RemoteHint(vararg pairs: Pair<String, String>, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pairs.forEachIndexed { i, (key, label) ->
            if (i > 0) {
                Box(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .width(1.dp)
                        .height(8.dp)
                        .background(TColor.Line)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RemoteKey(key)
                Text(label, style = TText.caption, color = TColor.Hint, maxLines = 1)
            }
        }
    }
}

/**
 * «حالت خالی» — وقتی سرویس داده ندارد
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconTile(TColor.Muted2, icon, IcoSize.LG)
        Spacer(Modifier.height(10.dp))
        Text(title, style = TText.titleS, color = TColor.Ink3, textAlign = TextAlign.Center)
        Spacer(Modifier.height(3.dp))
        Text(body, style = TText.caption, color = TColor.Muted2, textAlign = TextAlign.Center)
    }
}

/** جداکنندهٔ افقی */
@Composable
fun HDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TColor.LineSoft)
    )
}

/** ردیف کلید/مقدار پنل حساب */
@Composable
fun KvRowView(label: String, value: String, ltr: Boolean = false, valueColor: Color? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = TText.bodyS, color = TColor.Muted2)
        Spacer(Modifier.weight(1f))
        Text(
            value,
            style = TText.bodyS.copy(fontSize = 8.5.sp),
            color = valueColor ?: TColor.Ink2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
        if (ltr) {
            // شناسه‌های فنی با جهت لاتین
            Spacer(Modifier.width(0.dp))
        }
    }
    HDivider(Modifier.padding(top = 3.dp))
}

/** جعبهٔ تزئینی امنیتی/نشان کوچک */
@Composable
fun Dot(color: Color, size: Dp = 3.5.dp) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

/** Spacer عمودی کوتاه‌نویس */
@Composable
fun VSpace(h: Dp) = Spacer(Modifier.height(h))

/** برچسب ریز بالای فیلد */
@Composable
fun TinyLabel(text: String, color: Color = TColor.Muted2) {
    Text(text, style = TText.micro, color = color)
}

/**
 * ============================================================================
 *  FocusSurface — قلب تعامل تلویزیون
 *  هر عنصر فوکوس‌پذیر روی این پایه ساخته می‌شود تا قواعد بخش ۱۰ سند یک‌جا رعایت شود:
 *   • حلقهٔ طلایی ۱.۲۵dp + هالهٔ نور از بالا
 *   • بالاآمدن ملایم هنگام فوکوس (۱۴۰ms ورود / ۱۱۰ms خروج)
 *   • کلیک با OK روی ریموت (onClick)
 * ============================================================================
 */
@Composable
fun FocusSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    radius: Dp = TD.rLg2,
    baseBackground: Brush? = null,
    baseColor: Color = TColor.Card,
    borderColor: Color = TColor.Line,
    liftOnFocus: Dp = 3.dp,
    /** برای «فوکوس اول» هر صفحه (جدول بخش ۱۴ سند) */
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null,
    content: @Composable (Boolean) -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val lift by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = tween(durationMillis = if (focused) 140 else 110),
        label = "focusLift"
    )
    val shape = RoundedCornerShape(radius)
    val bg = baseBackground ?: Brush.verticalGradient(listOf(baseColor, baseColor))

    // جابه‌جایی عمودی نرم، بدون تغییر اندازهٔ چیدمان
    val offsetPx = with(androidx.compose.ui.platform.LocalDensity.current) { liftOnFocus.toPx() }
    val offsetY = -offsetPx * lift

    Box(
        modifier
            .graphicsLayer { translationY = offsetY }
            .clip(shape)
            .background(bg)
            .then(
                if (focused) {
                    Modifier
                        .background(
                            Brush.verticalGradient(listOf(Color(0x1FE8B84B), Color.Transparent))
                        )
                        .border(1.25.dp, TColor.Gold, shape)
                } else {
                    Modifier.border(1.dp, borderColor, shape)
                }
            )
            .onFocusChanged { focused = it.isFocused }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .focusable()
            .then(
                if (focusRequester != null)
                    Modifier.focusRequester(focusRequester) else Modifier
            )
    ) {
        content(focused)
    }
}
