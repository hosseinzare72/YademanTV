package com.hossein.yademan.tv.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.ui.theme.TColor

/**
 * ============================================================================
 *  فایل: data/SampleData.kt
 *  بخش: دادهٔ نمونهٔ دقیقاً هم‌عدد با اسکرین‌شات‌های تلویزیون
 * ============================================================================
 */
object SampleData {

    // ------------------------------------------------------------- کاربر
    const val USER_NAME = "زهرا کاظمی"
    const val USER_ID = "Zkazemi377"
    const val USER_PHONE = "۰۹۱۲ ••• ۴۵۸۷"
    const val USER_EMAIL = "z.kazemi@mail.ir"
    const val MEMBER_SINCE = "۱۲ فروردین ۱۴۰۴"
    const val PARTNER_NAME = "مریم کاظمی"
    const val PREMIUM_LINE = "اشتراک ویژه فعال است — تا ۱ دی ۱۴۰۵، ۵۳ روز باقی‌مانده"
    const val SUB_END = "۱ دی ۱۴۰۵"
    const val SUB_DAYS = "۵۳ روز"
    const val TODAY_LINE = "دوشنبه ۱۸ آبان ۱۴۰۵"
    const val CLOCK = "۲۰:۴۵"

    // ------------------------------------------------------ آمار کلی
    const val ACTIVE_REMINDERS = 42
    const val TODAY_COUNT = 3
    const val UPCOMING_COUNT = 4
    const val OVERDUE_COUNT = 1

    // -------------------------------------------------- سه کارت امروز
    val heroItems = listOf(
        HeroItem(
            title = "قسط وام باجت",
            subtitle = "بانک تجارت · ماهانه",
            progressLabel = "قسط ۱ از ۲۴",
            percent = "۴٪",
            fraction = 0.04f,
            amount = 7_075_000,
            chip = "امروز",
            tone = TTone.GOLD,
            icon = Icons.Filled.AccountBalanceWallet,
            color = TColor.Gold
        ),
        HeroItem(
            title = "اجاره خانه",
            subtitle = "قرارداد ۱۲ ماهه · واحد ۴",
            progressLabel = "ماه ۱۰ از ۱۲",
            percent = "۸۳٪",
            fraction = 0.83f,
            amount = 25_000_000,
            chip = "امروز",
            tone = TTone.BLUE,
            icon = Icons.Filled.Home,
            color = TColor.Blue
        ),
        HeroItem(
            title = "قرار دندان‌پزشکی",
            subtitle = "کلینیک آرام · ۱۷:۳۰",
            progressLabel = "۸ ساعت دیگر",
            percent = "امروز",
            fraction = 0.35f,
            amount = 0,
            chip = "۱۵:۳۰ زمان قرار",
            tone = TTone.PURPLE,
            icon = Icons.Filled.CalendarMonth,
            color = TColor.Purple
        )
    )

    // ---------------------------------------------- پیش‌رو — ۱۴ روز آینده
    val upcoming = listOf(
        UpcomingRow("چک شهر لوازم خانگی", 38_400_000, "۹ روز", TTone.PURPLE, Icons.Filled.Receipt, TColor.Purple),
        UpcomingRow("قسط وام خودرو", 9_800_000, "۲ روز", TTone.BLUE, Icons.Filled.AccountBalanceWallet, TColor.Blue),
        UpcomingRow("قبض برق", 480_000, "۴ روز", TTone.GREEN, Icons.Filled.Bolt, TColor.Green),
        UpcomingRow("سالگرد آشنایی", 0, "مناسبت", TTone.RED, Icons.Filled.CalendarMonth, TColor.Red),
        UpcomingRow("قسط بیمهٔ عمر", 2_350_000, "۱۱ روز", TTone.BLUE, Icons.Filled.Receipt, TColor.Blue)
    )

    // ------------------------------------------------------- عقب‌افتاده
    val overdue = OverdueItem(
        title = "شارژ ساختمان",
        amount = 1_200_000,
        meta = "۲ روز گذشته · ۱۶ آبان",
        icon = Icons.Filled.Apartment
    )

    // ---------------------------------------------------- میان‌برها
    val shortcuts = listOf(
        ShortcutItem(SectionType.INSTALLMENT),
        ShortcutItem(SectionType.CHECK),
        ShortcutItem(SectionType.MEDICINE),
        ShortcutItem(SectionType.NOTE),
        ShortcutItem(SectionType.BILLS),
        ShortcutItem(SectionType.RENT),
        ShortcutItem(SectionType.BIRTHDAY),
        ShortcutItem(SectionType.TRUST)
    )

    // --------------------------------------------------- نوار متریک خانه
    val homeMetrics = listOf(
        Metric("اقساط این ماه", money(7_075_000)),
        Metric("چک در جریان", money(58_900_000)),
        Metric("پرداخت‌شده", "۳ مورد", TColor.GreenInk)
    )

    // ------------------------------------------------------------- اقساط
    const val INST_THIS_MONTH = 16_875_000L
    const val INST_PAID_COUNT = 41
    const val INST_REMAINING = 744_200_000L
    const val INST_TOTAL_ACTIVE = 56_850_000L

    val installments = listOf(
        Installment(
            id = "i1", title = "قسط وام باجت", bank = "بانک تجارت",
            bankLine = "بانک تجارت · ۲۸ هر ماه · سررسید ۲۸ آبان ۱۴۰۵",
            index = 1, total = 24, percentText = "۴٪", fraction = 0.04f,
            amount = 7_075_000, status = "۱۰ روز مانده", tone = TTone.GOLD,
            color = TColor.Gold, icon = Icons.Filled.AccountBalanceWallet,
            contractNo = "TJB-4528-91", totalLoan = 169_800_000, remaining = 162_725_000,
            dueLine = "۲۸ آبان ۱۴۰۵"
        ),
        Installment(
            id = "i2", title = "قسط وام خودرو", bank = "بانک پاسارگاد",
            bankLine = "بانک پاسارگاد · ۲۰ هر ماه · سررسید ۲۰ آبان ۱۴۰۵",
            index = 18, total = 36, percentText = "۵۰٪", fraction = 0.5f,
            amount = 9_800_000, status = "۲ روز مانده", tone = TTone.BLUE,
            color = TColor.Blue, icon = Icons.Filled.AccountBalanceWallet,
            contractNo = "PSG-8821-04", totalLoan = 352_800_000, remaining = 176_400_000,
            dueLine = "۲۰ آبان ۱۴۰۵"
        ),
        Installment(
            id = "i3", title = "چک شهر لوازم خانگی", bank = "بانک تجارت",
            bankLine = "بانک تجارت · ۶ قسط · ۲۷ آبان ۱۴۰۵",
            index = 1, total = 6, percentText = "۱۷٪", fraction = 0.17f,
            amount = 38_400_000, status = "۹ روز مانده", tone = TTone.PURPLE,
            color = TColor.Purple, icon = Icons.Filled.Receipt,
            contractNo = "TJB-4528-19", totalLoan = 230_400_000, remaining = 192_000_000,
            dueLine = "۲۷ آبان ۱۴۰۵"
        ),
        Installment(
            id = "i4", title = "اجاره خانه", bank = "واحد ۴",
            bankLine = "واحد ۴ · خیابان گلستان · ۱۸ هر ماه",
            index = 10, total = 12, percentText = "۸۳٪", fraction = 0.83f,
            amount = 25_000_000, status = "امروز", tone = TTone.GOLD,
            color = TColor.Blue, icon = Icons.Filled.Home,
            contractNo = "RNT-1405-04", totalLoan = 300_000_000, remaining = 50_000_000,
            dueLine = "۱۸ آبان ۱۴۰۵"
        )
    )

    // -------------------------------------------------- جزئیات قسط باجت
    const val DETAIL_PAID = 7_075_000L
    const val DETAIL_LEFT = 162_725_000L
    const val DETAIL_TOTAL = 169_800_000L
    val timeline = listOf(
        "شروع قرارداد" to true,
        "قسط ۱ (جاری)" to true,
        "قسط ۲" to false,
        "قسط ۳" to false,
        "قسط ۱۲" to false,
        "پایان قرارداد" to false
    )
    val payments = listOf(
        Triple("قسط ۱ · ۲۸ مهر ۱۴۰۵", "پرداخت موفق · کارت ·۵۸۷", true),
        Triple("قسط ۲ · ۲۸ آبان ۱۴۰۵", "در انتظار پرداخت", false)
    )

    // ------------------------------------------------------------- چک‌ها
    const val CHEQUES_TOTAL = 58_900_000L
    val cheques = listOf(
        Cheque(
            id = "c1", title = "چک شهر لوازم خانگی", bank = "بانک تجارت", branch = "شعبهٔ ونک",
            amount = 38_400_000, dueLine = "۲۷ آبان ۱۴۰۵", serial = "45281907",
            owner = "زهرا کاظمی", planLine = "سررسید ۲۷ آبان · قسط ۱ از ۶",
            fraction = 1f / 6f, progressText = "۱ از ۶", chip = "۹ روز مانده",
            tone = TTone.PURPLE, accent = TColor.Purple
        ),
        Cheque(
            id = "c2", title = "چک اجارهٔ خانه", bank = "بانک صادرات", branch = "شعبهٔ گلستان",
            amount = 12_000_000, dueLine = "۲۵ آبان ۱۴۰۵", serial = "99305221",
            owner = "زهرا کاظمی", planLine = "سررسید ۲۵ آبان · یک‌بار",
            fraction = 1f, progressText = "یک‌بار", chip = "۷ روز مانده",
            tone = TTone.BLUE, accent = TColor.Blue
        ),
        Cheque(
            id = "c3", title = "چک تعمیرات ساختمان", bank = "بانک ملی", branch = "شعبهٔ مرکزی",
            amount = 8_500_000, dueLine = "۳۰ آبان ۱۴۰۵", serial = "71084432",
            owner = "زهرا کاظمی", planLine = "سررسید ۳۰ آبان · یک‌بار",
            fraction = 1f, progressText = "یک‌بار", chip = "۱۲ روز مانده",
            tone = TTone.ORANGE, accent = TColor.Orange
        )
    )

    // ---------------------------------------------------------- یادداشت‌ها
    val notes = listOf(
        Note(
            id = "n1", title = "خرید هفتگی", meta = "امروز ۱۹:۱۰",
            items = listOf(
                NoteItem("k1", "نان تازه — ۲ عدد", true),
                NoteItem("k2", "شیر کم‌چرب — ۲ لیتر", true),
                NoteItem("k3", "تخم‌مرغ — یک شانه"),
                NoteItem("k4", "پنیر و ماست — ۵۰۰ گرم"),
                NoteItem("k5", "میوهٔ فصل — ۳ کیلو"),
                NoteItem("k6", "دستمال کاغذی — ۲ بسته")
            ),
            sharedWith = "مریم",
            selectedItemId = "k3"
        ),
        Note(
            id = "n2", title = "کارهای بانکی", meta = "دیروز ۱۰:۳۰",
            items = listOf(
                NoteItem("b1", "تسویهٔ قسط باجت", true),
                NoteItem("b2", "به‌روزرسانی امضا"),
                NoteItem("b3", "دریافت گردش حساب"),
                NoteItem("b4", "فعال‌سازی رمز دوم")
            )
        ),
        Note(
            id = "n3", title = "چک‌لیست سفر کیش", meta = "۱۴ آبان",
            items = listOf(
                NoteItem("t1", "رزرو هتل", true),
                NoteItem("t2", "بلیت هواپیما", true),
                NoteItem("t3", "بیمهٔ سفر", true),
                NoteItem("t4", "شارژ کارت بانکی"),
                NoteItem("t5", "لباس سفر"),
                NoteItem("t6", "شارژر و پاوربانک"),
                NoteItem("t7", "کرم ضدآفتاب"),
                NoteItem("t8", "کتاب سفر")
            )
        ),
        Note(
            id = "n4", title = "ایدهٔ کادوی مریم", meta = "۱۱ آبان",
            items = listOf(
                NoteItem("g1", "کتاب رمان"),
                NoteItem("g2", "شال دست‌بافت"),
                NoteItem("g3", "ست عطر")
            )
        )
    )

    // ------------------------------------------------------------- داروها
    private val taken = DoseState.TAKEN
    private val now = DoseState.NOW

    val medicines = listOf(
        Medicine(
            id = "m1", name = "استامینوفن ۵۰۰", doseText = "۱ قرص · بعد از غذا",
            instruction = "هر ۸ ساعت", color = TColor.Pink,
            doses = listOf(
                Dose("۰۸:۰۰", "صبح", taken),
                Dose("۱۴:۰۰", "ظهر", taken),
                Dose("۲۲:۰۰", "شب", now)
            ),
            course = "روز ۳ از ۷", fraction = 0.43f,
            chip = "نوبت بعدی ۲۲:۰۰", tone = TTone.GOLD
        ),
        Medicine(
            id = "m2", name = "متفورمین ۵۰۰", doseText = "۱ قرص · همراه صبحانه",
            instruction = "۲ بار در روز", color = TColor.Blue,
            doses = listOf(
                Dose("۰۸:۰۰", "صبح", taken),
                Dose("۱۴:۰۰", "ظهر", DoseState.EMPTY),
                Dose("۲۲:۰۰", "شب", DoseState.EMPTY)
            ),
            course = "ادامه‌دار", fraction = 0.55f,
            chip = "۲ نوبت امروز", tone = TTone.BLUE
        ),
        Medicine(
            id = "m3", name = "ویتامین D3", doseText = "۱ قطره · هفتگی",
            instruction = "هر شنبه", color = TColor.Gold,
            doses = listOf(
                Dose("۰۹:۰۰", "شنبه", taken),
                Dose("—", "بعدی", DoseState.EMPTY),
                Dose("—", "بعدی", DoseState.EMPTY)
            ),
            course = "۴ دوز باقی", fraction = 0.72f,
            chip = "نوبت بعدی: شنبه", tone = TTone.GOLD
        ),
        Medicine(
            id = "m4", name = "قرص آهن", doseText = "۱ قرص · ناشتا",
            instruction = "روزی یک‌بار", color = TColor.Orange,
            doses = listOf(
                Dose("۰۷:۳۰", "صبح", taken),
                Dose("—", "", DoseState.EMPTY),
                Dose("—", "", DoseState.EMPTY)
            ),
            course = "روز ۱۸ از ۳۰", fraction = 0.6f,
            chip = "امروز انجام شد", tone = TTone.GREEN
        ),
        Medicine(
            id = "m5", name = "شربت سرماخوردگی", doseText = "۱۰ سی‌سی · هر ۸ ساعت",
            instruction = "۳ بار در روز", color = TColor.Purple,
            doses = listOf(
                Dose("۰۸:۰۰", "صبح", taken),
                Dose("۱۶:۰۰", "عصر", DoseState.EMPTY),
                Dose("۰۰:۰۰", "نیمه‌شب", DoseState.EMPTY)
            ),
            course = "روز ۲ از ۵", fraction = 0.4f,
            chip = "۲ نوبت امروز", tone = TTone.PURPLE
        ),
        Medicine(
            id = "m6", name = "قطره بینی", doseText = "۲ قطره · در هر بینی",
            instruction = "۲ بار در روز", color = TColor.Green,
            doses = listOf(
                Dose("۰۸:۰۰", "صبح", taken),
                Dose("۲۰:۰۰", "شب", taken),
                Dose("—", "", DoseState.EMPTY)
            ),
            course = "روز ۴ از ۷", fraction = 0.57f,
            chip = "امروز کامل شد", tone = TTone.GREEN
        )
    )

    // ------------------------------------------------------- قبوض و اجاره
    const val BILLS_TOTAL = 2_290_000L

    val bills = listOf(
        Bill("b1", "برق", "شرکت توزیع برق تهران", 480_000, "۴ روز", TColor.Amber, Icons.Filled.Bolt),
        Bill("b2", "گاز", "شرکت گاز استان تهران", 320_000, "۸ روز", TColor.Orange, Icons.Filled.LocalFireDepartment),
        Bill("b3", "اینترنت خانه", "شاتل · ADSL", 290_000, "۱۱ روز", TColor.Blue, Icons.Filled.Wifi),
        Bill("b4", "شارژ ساختمان", "مدیر ساختمان", 1_200_000, "۲ روز گذشته", TColor.Red, Icons.Filled.Apartment)
    )

    val rent = Rent(
        amount = 25_000_000,
        title = "اجاره خانه",
        ownerLine = "واحد ۴ · خیابان گلستان، پلاک ۱۲ · موجر: آقای رضایی",
        chips = listOf("قرارداد ۱۲ ماهه", "پرداخت ماهانه", "ودیعه ۳۰۰ میلیون"),
        deposit = 300_000_000,
        months = 12,
        thisMonth = 10,
        paidMonths = 9,
        endNote = "پایان قرارداد: ۱۸ شهریور ۱۴۰۶ — برای تمدید ۳۰ روز قبل با موجر هماهنگ کن.",
        dueLine = "سررسید ۱۸ هر ماه",
        nextPay = "۱۸ آذر ۱۴۰۵",
        endDate = "۱۸ شهریور ۱۴۰۶",
        status = "قرارداد فعال"
    )

    // ------------------------------------------------------------ حساب
    val plans = listOf(
        Plan("۱ ماهه", 45_000),
        Plan("۳ ماهه", 99_000),
        Plan("۶ ماهه", 199_000),
        Plan("۱ ساله", 349_000, suggested = true)
    )

    val profileRows = listOf(
        KvRow("موبایل", USER_PHONE),
        KvRow("ایمیل", USER_EMAIL, ltr = true),
        KvRow("عضویت", MEMBER_SINCE)
    )

    val subscriptionRows = listOf(
        KvRow("روش پرداخت", "کافه‌بازار · موبایل"),
        KvRow("سقف رایگان", "۲۰ سرویس"),
        KvRow("تمدید بعدی", SUB_END, color = TColor.GoldInk)
    )

    val syncRows = listOf(
        KvRow("سرور", "kafezare.ir", ltr = true),
        KvRow("دستگاه", "تلویزیون نشیمن · tv-7f2a"),
        KvRow("همگام‌سازی خودکار", "هر ۱۵ دقیقه")
    )

    val partnerRows = listOf(
        KvRow("نقش", "مالک"),
        KvRow("رکوردهای مشترک", "۹ رکورد"),
        KvRow("دسترسی این دستگاه", "فقط‌خواندنی")
    )

    val devices = listOf(
        Device("تلویزیون نشیمن", "tv-7f2a · همین حالا", true),
        Device("تلویزیون اتاق", "tv-91c4 · ۲ روز پیش"),
        Device("گوشی زهرا", "mobile-3d21 · ۵ دقیقه پیش")
    )

    val securityChips = listOf("ورود دوعاملی", "۳ دستگاه", "آخرین ورود: امروز ۱۹:۵۸")

    /** گرید ۱۸ سرویس برای صفحهٔ «همهٔ بخش‌ها» — جدول بخش ۱۲ سند */
    val sections = listOf(
        SectionCard(SectionType.INSTALLMENT, 4, "۷,۰۷۵,۰۰۰ این ماه", "امروز", 42, TTone.GOLD),
        SectionCard(SectionType.CHECK, 3, "۵۸,۹۰۰,۰۰۰ در جریان", "۲۷ آبان · ۹ روز", 78, TTone.PURPLE),
        SectionCard(SectionType.RENT, 1, "سررسید ۱۸ آبان", "امروز · سررسید", 83, TTone.BLUE),
        SectionCard(SectionType.SUBSCRIPTION, 2, "نتفلیکس · اسپاتیفای", "نتفلیکس · ۳ آذر", 25, TTone.PURPLE),
        SectionCard(SectionType.MEETING, 3, "۱ مورد امروز", "امروز ۱۷:۳۰", 30, TTone.BLUE),
        SectionCard(SectionType.MEDICINE, 6, "۲ نوبت باقی‌مانده", "نوبت بعدی ۲۲:۰۰", 64, TTone.PURPLE),
        SectionCard(SectionType.SHOPPING, 2, "۲ فهرست", "۲ مورد باز", 22, TTone.GREEN),
        SectionCard(SectionType.NOTE, 4, "آخرین: خرید هفتگی", "خرید هفتگی", 45, TTone.GOLD),
        SectionCard(SectionType.TRUST, 1, "سند نزد آقای رضایی", "نزد رضایی · ۲ آذر", 18, TTone.BLUE),
        SectionCard(SectionType.BILLS, 4, "برق · گاز · آب · نت", "برق · ۲۲ آبان", 35, TTone.GREEN),
        SectionCard(SectionType.BUILDING, 1, "۱ مورد عقب‌افتاده", "۲ روز گذشته", 12, TTone.RED),
        SectionCard(SectionType.TRAVEL, 1, "کیش · ۳ آذر", "کیش · ۳ آذر", 9, TTone.GOLD),
        SectionCard(SectionType.INSURANCE, 3, "بدنه · عمر · درمان", "بدنه · ۳ آذر", 55, TTone.BLUE),
        SectionCard(SectionType.CAR, 1, "۱۰,۰۰۰ کیلومتر", "۱۰,۰۰۰ کیلومتر", 26, TTone.GOLD),
        SectionCard(SectionType.SALARY, 1, "پنجم هر ماه", "پنجم آذر", 68, TTone.GOLD),
        SectionCard(SectionType.POCKET, 2, "هفتگی", "هفتگی · شنبه", 40, TTone.GOLD),
        SectionCard(SectionType.BIRTHDAY, 4, "بعدی: مریم ۲ آذر", "۲ آذر · مریم", 31, TTone.PURPLE),
        SectionCard(SectionType.ANNIVERSARY, 2, "۲۴ آبان · ۶ روز", "۲۴ آبان · ۶ روز", 14, TTone.RED)
    )
}
