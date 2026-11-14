package com.hossein.yademan.tv.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.hossein.yademan.tv.core.daysLeft
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.core.toFa
import com.hossein.yademan.tv.core.faFull
import com.hossein.yademan.tv.ui.theme.TColor
import java.time.LocalDate
import java.time.LocalTime

/**
 * ============================================================================
 *  فایل: data/DataHub.kt
 *  بخش: منبع واحد داده برای همهٔ صفحه‌ها
 *
 *  چرا این فایل؟ صفحه‌ها عیناً همان طراحی قبلی را نگه می‌دارند (همان نام‌ها و
 *  همان ساختار)، اما مقادیرشان از این‌جا می‌آید:
 *    • اگر سرور رکوردی برای یک بخش بفرستد → مقدار واقعی محاسبه‌شده
 *    • اگر نفرستد (یا آفلاین باشیم) → همان دادهٔ مرجع طراحی، بدون تغییر ظاهر
 *  بنابراین هیچ صفحه‌ای «خالی» یا «شکسته» نمی‌شود و ظاهر هم دست نمی‌خورد.
 * ============================================================================
 */
object DataHub {

    // ------------------------------------------------------- وضعیت پایه
    /** همهٔ رکوردهای همگام‌شده از سرور */
    var records: List<Record> by mutableStateOf(emptyList())

    /** لحظهٔ مرجع محاسبات (هر ۳۰ ثانیه توسط اپ تازه می‌شود) */
    var now: Long by mutableStateOf(System.currentTimeMillis())

    /** اطلاعات نشست که از سرور/ذخیره‌گاه می‌آید */
    var userName: String by mutableStateOf(SampleData.USER_NAME)
    var userId: String by mutableStateOf(SampleData.USER_ID)
    var deviceName: String by mutableStateOf("تلویزیون نشیمن")
    var deviceShortId: String by mutableStateOf("tv-7f2a")
    var lastSyncLabel: String by mutableStateOf("۲ دقیقه پیش")
    var readOnly: Boolean by mutableStateOf(true)
    var offline: Boolean by mutableStateOf(false)

    /** آیا دادهٔ واقعی داریم؟ (اگر نه، دادهٔ مرجع طراحی نمایش داده می‌شود) */
    val hasData: Boolean get() = records.isNotEmpty()

    fun refreshNow() { now = System.currentTimeMillis() }

    /** دادهٔ واقعی یا جایگزین طراحی */
    private fun <T> pick(computed: List<T>, fallback: List<T>): List<T> =
        if (computed.isNotEmpty()) computed else fallback

    // ------------------------------------------------------- هویت کاربر
    val USER_NAME: String get() = userName
    val USER_ID: String get() = userId
    val USER_PHONE: String get() = SampleData.USER_PHONE
    val USER_EMAIL: String get() = SampleData.USER_EMAIL
    val MEMBER_SINCE: String get() = SampleData.MEMBER_SINCE
    val PARTNER_NAME: String get() = SampleData.PARTNER_NAME
    val PREMIUM_LINE: String get() = SampleData.PREMIUM_LINE
    val SUB_END: String get() = SampleData.SUB_END
    val SUB_DAYS: String get() = SampleData.SUB_DAYS

    /** تاریخ کامل امروز به شمسی — «دوشنبه ۱۸ آبان ۱۴۰۵» */
    val TODAY_LINE: String get() = LocalDate.now().faFull()

    /** ساعت جاری — «۲۰:۴۵» */
    val CLOCK: String get() {
        val t = LocalTime.now()
        val h = t.hour.toString().padStart(2, '0').toFa()
        val m = t.minute.toString().padStart(2, '0').toFa()
        return "$h:$m"
    }

    // -------------------------------------------------- شمارنده‌های خانه
    val ACTIVE_REMINDERS: Int
        get() = if (!hasData) SampleData.ACTIVE_REMINDERS else records.size

    val TODAY_COUNT: Int get() = heroItems.size
    val UPCOMING_COUNT: Int get() = upcoming.size
    val OVERDUE_COUNT: Int get() = if (overdue.title.isBlank()) 0 else 1

    // ---------------------------------------------------- سه کارت امروز
    val heroItems: List<HeroItem>
        get() = pick(RecordMapper.toHeroes(records, now), SampleData.heroItems)

    val upcoming: List<UpcomingRow>
        get() = pick(RecordMapper.toUpcoming(records, now), SampleData.upcoming)

    val overdue: OverdueItem
        get() = if (hasData) (RecordMapper.toOverdue(records, now) ?: SampleData.overdue)
        else SampleData.overdue

    val shortcuts: List<ShortcutItem> get() = SampleData.shortcuts

    val homeMetrics: List<Metric>
        get() = if (!hasData) SampleData.homeMetrics else RecordMapper.homeMetrics(records, now)

    // ------------------------------------------------------------- اقساط
    val installments: List<Installment>
        get() = pick(RecordMapper.toInstallments(records, now), SampleData.installments)

    val INST_THIS_MONTH: Long
        get() = if (!hasData) SampleData.INST_THIS_MONTH
        else RecordMapper.installmentSummary(records, now).first

    val INST_PAID_COUNT: Int
        get() = if (!hasData) SampleData.INST_PAID_COUNT
        else RecordMapper.installmentSummary(records, now).second

    val INST_REMAINING: Long
        get() = if (!hasData) SampleData.INST_REMAINING
        else RecordMapper.installmentSummary(records, now).third

    val INST_TOTAL_ACTIVE: Long
        get() = installments.filter { it.status != "پرداخت شد" }.sumOf { it.amount }

    /** «۴ قسط فعال · مجموع … تومان» */
    val INST_ACTIVE_LINE: String
        get() = "${installments.size.toFa()} قسط فعال · مجموع ${money(INST_TOTAL_ACTIVE)} تومان"

    // ------------------------------------------------ جزئیات یک قسط
    /** اطلاعات تکمیلی کارت جزئیات، بر اساس همان قسط باز‌شده */
    data class DetailData(
        val paid: Long,
        val left: Long,
        val total: Long,
        val timeline: List<Pair<String, Boolean>>,
        val payments: List<Triple<String, String, Boolean>>
    )

    fun detailFor(inst: Installment): DetailData {
        if (!hasData) {
            return DetailData(
                SampleData.DETAIL_PAID,
                SampleData.DETAIL_LEFT,
                SampleData.DETAIL_TOTAL,
                SampleData.timeline,
                SampleData.payments
            )
        }
        val doneAmount = (inst.total - inst.index + 1).coerceAtLeast(0) * inst.amount
        val left = inst.remaining
        val timeline = buildList {
            add("شروع قرارداد" to true)
            add("قسط ${inst.index.toFa()} (جاری)" to true)
            val next = (inst.index + 1).coerceAtMost(inst.total)
            if (next <= inst.total) add("قسط ${next.toFa()}" to false)
            if (inst.total > next + 1) add("قسط ${inst.total.toFa()}" to false)
            add("پایان قرارداد" to false)
        }
        val payments = listOf(
            Triple(
                "قسط ${inst.index.toFa()} · ${inst.dueLine}",
                "در انتظار پرداخت",
                false
            )
        )
        return DetailData(doneAmount, left, inst.totalLoan, timeline, payments)
    }

    // ------------------------------------------------------------- چک‌ها
    val cheques: List<Cheque>
        get() = pick(RecordMapper.toCheques(records, now), SampleData.cheques)

    val CHEQUES_TOTAL: Long
        get() = if (!hasData) SampleData.CHEQUES_TOTAL else RecordMapper.chequesTotal(records, now)

    val CHEQUES_ACTIVE_LINE: String
        get() = "${cheques.size.toFa()} چک در جریان · ${money(CHEQUES_TOTAL)} تومان"

    // --------------------------------------------------------- یادداشت‌ها
    /** یادداشت‌ها با امکان تیک‌زدن (تنها نوشتن مجاز نسخهٔ تلویزیون) */
    var notes: List<Note> by mutableStateOf(SampleData.notes)

    fun syncNotesFromRecords() {
        val serverNotes = RecordMapper.toNotes(records)
        if (serverNotes.isNotEmpty()) {
            notes = serverNotes.map { s ->
                notes.firstOrNull { it.id == s.id } ?: s
            }
        }
    }

    fun note(id: String): Note = notes.firstOrNull { it.id == id } ?: notes.first()

    fun toggleItem(noteId: String, itemId: String) {
        notes = notes.map { n ->
            if (n.id != noteId) n
            else n.copy(items = n.items.map { i -> if (i.id != itemId) i else i.copy(done = !i.done) })
        }
    }

    // ------------------------------------------------------------- داروها
    val medicines: List<Medicine>
        get() = pick(RecordMapper.toMedicines(records, now), SampleData.medicines)

    // -------------------------------------------------------- قبوض/اجاره
    val bills: List<Bill>
        get() = pick(RecordMapper.toBills(records, now), SampleData.bills)

    val BILLS_TOTAL: Long
        get() = if (!hasData) SampleData.BILLS_TOTAL else RecordMapper.billsTotal(records, now)

    val rent: Rent
        get() = if (!hasData) SampleData.rent else (RecordMapper.toRent(records) ?: SampleData.rent)

    // ------------------------------------------------------- همهٔ بخش‌ها
    /**
     * گرید ۱۸ سرویس: برای هر بخش، اگر رکورد واقعی داشت از محاسبهٔ همان بخش،
     * وگرنه همان کارت مرجع طراحی. بنابراین گرید همیشه کامل و هم‌شکل است.
     */
    val sections: List<SectionCard>
        get() {
            if (!hasData) return SampleData.sections
            val computed = RecordMapper.toSections(records, now).associateBy { it.type }
            return SampleData.sections.map { sample ->
                val c = computed[sample.type]
                if (c != null && records.any { RecordMapper.serviceKey(sample.type) == it.serviceType }) c
                else sample
            }
        }

    // ------------------------------------------------------------- حساب
    val plans: List<Plan> get() = SampleData.plans

    val profileRows: List<KvRow>
        get() = listOf(
            KvRow("موبایل", USER_PHONE),
            KvRow("ایمیل", USER_EMAIL, ltr = true),
            KvRow("عضویت", MEMBER_SINCE)
        )

    val subscriptionRows: List<KvRow> get() = SampleData.subscriptionRows

    /** ردیف‌های پنل همگام‌سازی — مقادیر واقعی همین دستگاه (سه ردیف مثل طراحی) */
    val syncRows: List<KvRow>
        get() = listOf(
            KvRow("سرور", "kafezare.ir", ltr = true),
            KvRow("دستگاه", "$deviceName · $deviceShortId"),
            KvRow("همگام‌سازی خودکار", "هر ۱۵ دقیقه")
        )

    val partnerRows: List<KvRow> get() = SampleData.partnerRows

    val devices: List<Device> get() = SampleData.devices

    val securityChips: List<String> get() = SampleData.securityChips

    // ------------------------------------------------------------ پاک‌سازی
    fun clear() {
        records = emptyList()
        notes = SampleData.notes
    }

    /** رنگ خنثی */
    val neutral: Color get() = TColor.Muted2

    /** نوع سرویس یک کارت هیرو بر اساس عنوانش (اگر از سرور آمده باشد) */
    fun typeForTitle(title: String): SectionType? {
        val r = records.firstOrNull { it.title == title } ?: return null
        return RecordMapper.sectionTypeOf(com.hossein.yademan.tv.core.resolveType(r))
    }

    /** اختلاف روز با یک تاریخ جلالی برای چیپ‌ها */
    fun daysOf(occ: Long): Long = daysLeft(occ, now)
}
