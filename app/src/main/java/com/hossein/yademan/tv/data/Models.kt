package com.hossein.yademan.tv.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.hossein.yademan.tv.ui.theme.TColor

/**
 * ============================================================================
 *  فایل: data/Models.kt
 *  بخش: مدل دادهٔ همهٔ ۱۸ سرویس نسخهٔ تلویزیون
 * ============================================================================
 */

/** معنای رنگ چیپ‌ها */
enum class TTone { NEUTRAL, GOLD, GREEN, RED, BLUE, PURPLE, ORANGE }

/** ۱۸ سرویس — رنگ و آیکون طبق جدول بخش ۱۲ سند */
enum class SectionType(
    val title: String,
    val color: Color,
    val icon: ImageVector
) {
    INSTALLMENT("اقساط", TColor.Gold, Icons.Filled.AccountBalanceWallet),
    CHECK("چک‌ها", TColor.Purple, Icons.Filled.Receipt),
    RENT("اجاره", TColor.Blue, Icons.Filled.Home),
    SUBSCRIPTION("اشتراک‌ها", TColor.Orange, Icons.Filled.Refresh),
    MEETING("قرار و جلسه", TColor.Blue, Icons.Filled.DateRange),
    MEDICINE("داروها", TColor.Pink, Icons.Filled.MedicalServices),
    SHOPPING("لیست خرید", TColor.Green, Icons.Filled.ShoppingCart),
    NOTE("یادداشت‌ها", TColor.Amber, Icons.Filled.Description),
    TRUST("امانت‌ها", TColor.Trust, Icons.Filled.Security),
    BILLS("قبوض", TColor.Green, Icons.Filled.Bolt),
    BUILDING("شارژ ساختمان", TColor.Red, Icons.Filled.Apartment),
    TRAVEL("مسافرت", TColor.Gold, Icons.Filled.Flight),
    INSURANCE("بیمه‌ها", TColor.Blue, Icons.Filled.Security),
    CAR("سرویس خودرو", TColor.Amber, Icons.Filled.DirectionsCar),
    SALARY("حقوق", TColor.Gold, Icons.Filled.ArrowDownward),
    POCKET("پول توجیبی", TColor.Gold, Icons.Filled.AccountBalanceWallet),
    BIRTHDAY("تولدها", TColor.Pink, Icons.Filled.CardGiftcard),
    ANNIVERSARY("سالگردها", TColor.Red, Icons.Filled.Favorite)
}

/** تایل صفحهٔ «همهٔ بخش‌ها» */
data class SectionCard(
    val type: SectionType,
    val count: Int,
    val subtitle: String,
    val nextLine: String,
    val percent: Int,
    val tone: TTone
)

/** کارت هیرو صفحهٔ خانه */
data class HeroItem(
    val title: String,
    val subtitle: String,
    val progressLabel: String,
    val percent: String,
    val fraction: Float,
    val amount: Long,
    val chip: String,
    val tone: TTone,
    val icon: ImageVector,
    val color: Color
)

/** ردیف «پیش‌رو ۱۴ روز» */
data class UpcomingRow(
    val title: String,
    val amount: Long,
    val chip: String,
    val tone: TTone,
    val icon: ImageVector,
    val color: Color
)

/** کارت عقب‌افتاده */
data class OverdueItem(
    val title: String,
    val amount: Long,
    val meta: String,
    val icon: ImageVector
)

/** میان‌بر بخش‌ها در خانه */
data class ShortcutItem(val type: SectionType)

/** قسط */
data class Installment(
    val id: String,
    val title: String,
    val bank: String,
    val bankLine: String,          // «بانک تجارت · ۲۸ هر ماه · سررسید ۲۸ آبان ۱۴۰۵»
    val index: Int,
    val total: Int,
    val percentText: String,
    val fraction: Float,
    val amount: Long,
    val status: String,
    val tone: TTone,
    val color: Color,
    val icon: ImageVector,
    val contractNo: String,
    val totalLoan: Long,
    val remaining: Long,
    val dueLine: String            // «۲۸ آبان ۱۴۰۵»
)

/** چک */
data class Cheque(
    val id: String,
    val title: String,
    val bank: String,
    val branch: String,
    val amount: Long,
    val dueLine: String,
    val serial: String,
    val owner: String,
    val planLine: String,          // «سررسید ۲۷ آبان · قسط ۱ از ۶»
    val fraction: Float,
    val progressText: String,      // «۱ از ۶»
    val chip: String,
    val tone: TTone,
    val accent: Color
)

/** آیتم چک‌لیست یادداشت */
data class NoteItem(val id: String, val text: String, val done: Boolean = false)

/** یادداشت */
data class Note(
    val id: String,
    val title: String,
    val meta: String,              // «امروز ۱۹:۱۰»
    val items: List<NoteItem>,
    val sharedWith: String? = null,
    val selectedItemId: String? = null
) {
    val done: Int get() = items.count { it.done }
    val total: Int get() = items.size
    val progress: Float get() = if (total == 0) 0f else done.toFloat() / total
}

/** وضعیت نوبت دارو */
enum class DoseState { TAKEN, NOW, EMPTY, WEEKLY }

data class Dose(val time: String, val slot: String, val state: DoseState)

/** دارو */
data class Medicine(
    val id: String,
    val name: String,
    val doseText: String,
    val instruction: String,
    val color: Color,
    val doses: List<Dose>,
    val course: String,
    val fraction: Float,
    val chip: String,
    val tone: TTone
)

/** قبض */
data class Bill(
    val id: String,
    val title: String,
    val provider: String,
    val amount: Long,
    val daysChip: String,
    val color: Color,
    val icon: ImageVector
)

/** اجاره */
data class Rent(
    val amount: Long,
    val title: String,
    val ownerLine: String,
    val chips: List<String>,
    val deposit: Long,
    val months: Int,
    val thisMonth: Int,
    val paidMonths: Int,
    val endNote: String,
    val dueLine: String,
    val nextPay: String,
    val endDate: String,
    val status: String
)

/** پلن اشتراک */
data class Plan(val label: String, val price: Long, val suggested: Boolean = false)

/** ردیف کلید/مقدار پنل حساب */
data class KvRow(val label: String, val value: String, val ltr: Boolean = false, val color: Color? = null)

/** متریک نوار پایین */
data class Metric(val label: String, val value: String, val color: Color? = null)

data class Device(val name: String, val meta: String, val primary: Boolean = false)
