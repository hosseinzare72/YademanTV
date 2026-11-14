package com.hossein.yademan.tv.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import com.hossein.yademan.tv.core.currentOccurrence
import com.hossein.yademan.tv.core.daysLabel
import com.hossein.yademan.tv.core.daysLeft
import com.hossein.yademan.tv.core.isPaid
import com.hossein.yademan.tv.core.millisToFaDate
import com.hossein.yademan.tv.core.millisToFaShort
import com.hossein.yademan.tv.core.money
import com.hossein.yademan.tv.core.occurrenceIndex
import com.hossein.yademan.tv.core.paidCount
import com.hossein.yademan.tv.core.progressOf
import com.hossein.yademan.tv.core.resolveType
import com.hossein.yademan.tv.core.toFa
import com.hossein.yademan.tv.core.totalOccurrences
import com.hossein.yademan.tv.ui.theme.TColor

/**
 * ============================================================================
 *  فایل: data/RecordMapper.kt
 *  بخش: نگاشت رکوردهای سرور به مدل‌های UI همین اپ
 *
 *  این فایل «داده» را عوض می‌کند نه «ظاهر» را؛ اگر سرور داده‌ای نداشت،
 *  SampleData دست‌نخورده می‌ماند تا اسکرین‌ها دقیقاً مثل طراحی بمانند.
 * ============================================================================
 */
object RecordMapper {

    // ---------------------------------------------------------- نوع سرویس
    fun sectionTypeOf(service: String): SectionType? = when (service) {
        "installment", "loan" -> SectionType.INSTALLMENT
        "check", "cheque" -> SectionType.CHECK
        "rent" -> SectionType.RENT
        "subscription" -> SectionType.SUBSCRIPTION
        "meeting", "appointment" -> SectionType.MEETING
        "medicine" -> SectionType.MEDICINE
        "shopping" -> SectionType.SHOPPING
        "note" -> SectionType.NOTE
        "trust", "deposit" -> SectionType.TRUST
        "bills", "bill" -> SectionType.BILLS
        "building" -> SectionType.BUILDING
        "travel" -> SectionType.TRAVEL
        "insurance" -> SectionType.INSURANCE
        "car" -> SectionType.CAR
        "salary" -> SectionType.SALARY
        "pocket" -> SectionType.POCKET
        "birthday" -> SectionType.BIRTHDAY
        "anniversary" -> SectionType.ANNIVERSARY
        else -> null
    }

    /** رکوردهای یک سرویس مشخص */
    fun forService(records: List<Record>, service: String): List<Record> =
        records.filter { resolveType(it) == service }

    // --------------------------------------------------------- وضعیت/رنگ
    private fun statusOf(days: Long): Pair<String, TTone> = when {
        days < 0 -> daysLabel(days) to TTone.RED
        days == 0L -> "امروز" to TTone.GOLD
        days <= 3 -> daysLabel(days) to TTone.BLUE
        days <= 10 -> daysLabel(days) to TTone.GOLD
        else -> daysLabel(days) to TTone.NEUTRAL
    }

    private fun upcomingLabel(days: Long): String = when {
        days == 0L -> "امروز"
        days == 1L -> "فردا"
        days < 0 -> daysLabel(days)
        else -> "${days.toFa()} روز"
    }

    // ------------------------------------------------------- کارت‌های هیرو
    /** سه کارت «امروز» — همان چیزی که خانه نشان می‌دهد */
    fun toHeroes(records: List<Record>, now: Long): List<HeroItem> {
        val today = records.mapNotNull { r ->
            val type = resolveType(r)
            val occ = currentOccurrence(r, now) ?: return@mapNotNull null
            val d = daysLeft(occ, now)
            if (d != 0L || isPaid(r, occ)) return@mapNotNull null
            val st = sectionTypeOf(type) ?: return@mapNotNull null
            val idx = occurrenceIndex(r, occ)
            val total = totalOccurrences(r)
            HeroItem(
                title = r.title,
                subtitle = r.subtitle.ifBlank { r.meta.str("bank") ?: st.title },
                progressLabel = if (r.repeatRule == "none") "یک‌بار" else "قسط ${idx.toFa()} از ${total.toFa()}",
                percent = "${(progressOf(r, occ) * 100).toInt().toFa()}٪",
                fraction = progressOf(r, occ),
                amount = r.amount,
                chip = "امروز",
                tone = if (st.color == TColor.Gold) TTone.GOLD else TTone.BLUE,
                icon = st.icon,
                color = st.color
            )
        }
        return today.take(3)
    }

    /** ردیف‌های «پیش‌رو — ۱۴ روز آینده» */
    fun toUpcoming(records: List<Record>, now: Long, limit: Int = 5): List<UpcomingRow> {
        val rows = records.mapNotNull { r ->
            val type = resolveType(r)
            val occ = currentOccurrence(r, now) ?: return@mapNotNull null
            val d = daysLeft(occ, now)
            if (d <= 0 || d > 14 || isPaid(r, occ)) return@mapNotNull null
            val st = sectionTypeOf(type) ?: return@mapNotNull null
            UpcomingRow(
                title = r.title,
                amount = r.amount,
                chip = upcomingLabel(d),
                tone = if (d <= 3) TTone.BLUE else TTone.GOLD,
                icon = st.icon,
                color = st.color
            ) to d
        }.sortedBy { it.second }
        return rows.take(limit).map { it.first }
    }

    /** کارت «عقب‌افتاده» */
    fun toOverdue(records: List<Record>, now: Long): OverdueItem? {
        val item = records.mapNotNull { r ->
            val type = resolveType(r)
            val occ = currentOccurrence(r, now) ?: return@mapNotNull null
            val d = daysLeft(occ, now)
            if (d >= 0 || isPaid(r, occ)) return@mapNotNull null
            val st = sectionTypeOf(type) ?: return@mapNotNull null
            (OverdueItem(
                title = r.title,
                amount = r.amount,
                meta = "${daysLabel(d)} · ${millisToFaShort(occ)}",
                icon = st.icon
            ) to d)
        }.minByOrNull { it.second }
        return item?.first
    }

    // ------------------------------------------------------------- اقساط
    fun toInstallments(records: List<Record>, now: Long): List<Installment> {
        return records.mapNotNull { r ->
            val type = resolveType(r)
            if (type !in setOf("installment", "loan")) return@mapNotNull null
            val occ = currentOccurrence(r, now) ?: return@mapNotNull null
            val idx = occurrenceIndex(r, occ)
            val total = totalOccurrences(r)
            val paid = isPaid(r, occ)
            val d = daysLeft(occ, now)
            val (statusText, tone) = if (paid) "پرداخت شد" to TTone.GREEN else statusOf(d)
            val st = SectionType.INSTALLMENT
            Installment(
                id = r.id,
                title = r.title,
                bank = r.meta.str("bank") ?: r.subtitle.ifBlank { "بانک" },
                bankLine = listOfNotNull(
                    r.meta.str("bank") ?: r.subtitle.takeIf { it.isNotBlank() },
                    r.meta.str("due_day")?.let { "$it هر ماه" },
                    "سررسید ${millisToFaDate(occ)}"
                ).joinToString(" · "),
                index = idx,
                total = total,
                percentText = "${(progressOf(r, occ) * 100).toInt().toFa()}٪",
                fraction = progressOf(r, occ),
                amount = r.amount,
                status = statusText,
                tone = tone,
                color = if (paid) TColor.Green else TColor.Gold,
                icon = st.icon,
                contractNo = r.meta.str("contract_no") ?: "—",
                totalLoan = r.meta.long("total_loan") ?: (r.amount * total),
                remaining = (r.meta.long("remaining"))
                    ?: ((total - idx).coerceAtLeast(0) * r.amount),
                dueLine = millisToFaDate(occ)
            ) to d
        }.sortedBy { it.second }.map { it.first }
    }

    // --------------------------------------------------------------- چک‌ها
    fun toCheques(records: List<Record>, now: Long): List<Cheque> {
        val accents = listOf(TColor.Purple, TColor.Blue, TColor.Orange, TColor.Gold, TColor.Green)
        return records.mapIndexedNotNull { i, r ->
            val type = resolveType(r)
            if (type !in setOf("check", "cheque")) return@mapIndexedNotNull null
            val occ = currentOccurrence(r, now) ?: return@mapIndexedNotNull null
            val paid = isPaid(r, occ)
            val d = daysLeft(occ, now)
            val (chip, tone) = if (paid) "پرداخت شد" to TTone.GREEN else statusOf(d)
            val total = totalOccurrences(r)
            val idx = occurrenceIndex(r, occ)
            Cheque(
                id = r.id,
                title = r.title,
                bank = r.meta.str("bank") ?: "بانک",
                branch = r.meta.str("branch") ?: r.subtitle.ifBlank { "شعبهٔ مرکزی" },
                amount = r.amount,
                dueLine = millisToFaDate(occ),
                serial = r.meta.str("serial") ?: r.meta.str("cheque_no") ?: r.shortId,
                owner = r.meta.str("owner") ?: "زهرا کاظمی",
                planLine = "سررسید ${millisToFaShort(occ)} · " +
                        if (total > 1) "قسط ${idx.toFa()} از ${total.toFa()}" else "یک‌بار",
                fraction = progressOf(r, occ),
                progressText = if (total > 1) "${idx.toFa()} از ${total.toFa()}" else "یک‌بار",
                chip = chip,
                tone = tone,
                accent = accents[i % accents.size]
            )
        }.sortedBy { it.dueLine }
    }

    // --------------------------------------------------------- یادداشت‌ها
    fun toNotes(records: List<Record>): List<Note> {
        return records.mapNotNull { r ->
            val type = resolveType(r)
            if (type !in setOf("note", "shopping")) return@mapNotNull null
            val items = parseItems(r)
            if (items.isEmpty()) return@mapNotNull null
            Note(
                id = r.id,
                title = r.title,
                meta = r.updatedAt?.let { millisToFaShort(it) } ?: "بدون تاریخ",
                items = items,
                sharedWith = r.meta.str("shared_with"),
                selectedItemId = items.firstOrNull { !it.done }?.id
            )
        }
    }

    /** آیتم‌های چک‌لیست از meta_json (چند شکل مختلف) */
    private fun parseItems(r: Record): List<NoteItem> {
        val out = ArrayList<NoteItem>()
        val arr = r.raw.optJSONArray("items")
            ?: runCatching { org.json.JSONArray(r.meta.str("items") ?: "") }.getOrNull()
        if (arr != null) {
            for (i in 0 until arr.length()) {
                when (val v = arr.opt(i)) {
                    is String -> if (v.isNotBlank()) out.add(NoteItem("$i", v.trim()))
                    is org.json.JSONObject -> {
                        val text = v.optString("text").ifBlank { v.optString("title") }
                        if (text.isNotBlank()) {
                            out.add(
                                NoteItem(
                                    id = v.optString("id").ifBlank { "$i" },
                                    text = text.trim(),
                                    done = v.optBoolean("done", false)
                                )
                            )
                        }
                    }
                }
            }
        }
        if (out.isNotEmpty()) return out

        // متن چندخطی در meta.note
        val note = r.meta.str("note") ?: r.subtitle
        return note.split('\n', '؛', ';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { i, t -> NoteItem("$i", t, done = false) }
    }

    // ------------------------------------------------------------- داروها
    fun toMedicines(records: List<Record>, now: Long): List<Medicine> {
        return records.mapNotNull { r ->
            val type = resolveType(r)
            if (type != "medicine") return@mapNotNull null
            val occ = currentOccurrence(r, now) ?: return@mapNotNull null
            val times = r.meta.str("times")
                ?.split(',', '؛', '/')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: listOf("۰۸:۰۰", "۱۴:۰۰", "۲۲:۰۰")
            val doneToday = isPaid(r, occ)
            val doses = times.take(3).mapIndexed { i, t ->
                val state = when {
                    doneToday -> DoseState.TAKEN
                    i == 0 && !doneToday -> DoseState.NOW
                    else -> DoseState.EMPTY
                }
                Dose(t, if (i == 0) "صبح" else if (i == 1) "ظهر" else "شب", state)
            }
            Medicine(
                id = r.id,
                name = r.title,
                doseText = r.subtitle.ifBlank { r.meta.str("dose") ?: "۱ قرص" },
                instruction = r.meta.str("instruction") ?: "طبق دستور پزشک",
                color = TColor.Pink,
                doses = doses,
                course = r.meta.str("course") ?: "ادامه‌دار",
                fraction = if (doneToday) 1f else progressOf(r, occ),
                chip = if (doneToday) "امروز انجام شد" else "${times.size.toFa()} نوبت امروز",
                tone = if (doneToday) TTone.GREEN else TTone.GOLD
            )
        }
    }

    // -------------------------------------------------------- قبوض و اجاره
    fun toBills(records: List<Record>, now: Long): List<Bill> {
        return records.mapNotNull { r ->
            val type = resolveType(r)
            if (type !in setOf("bills", "bill", "building")) return@mapNotNull null
            val occ = currentOccurrence(r, now) ?: return@mapNotNull null
            val d = daysLeft(occ, now)
            val paid = isPaid(r, occ)
            if (paid) return@mapNotNull null
            val (color, icon) = when {
                r.title.contains("برق") -> TColor.Amber to Icons.Filled.Bolt
                r.title.contains("گاز") -> TColor.Orange to Icons.Filled.LocalFireDepartment
                r.title.contains("نت") || r.title.contains("اینترنت") -> TColor.Blue to Icons.Filled.Wifi
                type == "building" || r.title.contains("شارژ") -> TColor.Red to Icons.Filled.Apartment
                else -> TColor.Green to Icons.Filled.Bolt
            }
            Bill(
                id = r.id,
                title = r.title,
                provider = r.subtitle.ifBlank { r.meta.str("provider") ?: "سازمان" },
                amount = r.amount,
                daysChip = daysLabel(d),
                color = color,
                icon = icon
            )
        }.sortedBy { it.daysChip }
    }

    fun toRent(records: List<Record>): Rent? {
        val r = records.firstOrNull { resolveType(it) == "rent" } ?: return null
        val months = r.meta.long("months")?.toInt() ?: 12
        val thisMonth = r.meta.long("month_index")?.toInt()
            ?: (occurrenceIndex(r, currentOccurrence(r) ?: System.currentTimeMillis())).coerceIn(1, months)
        return Rent(
            amount = r.amount,
            title = r.title.ifBlank { "اجاره خانه" },
            ownerLine = r.subtitle.ifBlank { r.meta.str("address") ?: "—" },
            chips = listOf(
                "قرارداد ${months.toFa()} ماهه",
                "پرداخت ماهانه",
                r.meta.long("deposit")?.let { "ودیعه ${money(it)}" } ?: "ودیعه —"
            ),
            deposit = r.meta.long("deposit") ?: 0L,
            months = months,
            thisMonth = thisMonth,
            paidMonths = (thisMonth - 1).coerceAtLeast(0),
            endNote = r.endAt?.let { "پایان قرارداد: ${millisToFaDate(it)}" } ?: "پایان قرارداد ثبت نشده است",
            dueLine = r.meta.str("due_day")?.let { "سررسید $it هر ماه" } ?: "سررسید ماهانه",
            nextPay = r.dueAt?.let { millisToFaDate(com.hossein.yademan.tv.core.jalaliPlusMonths(it, 1)) } ?: "—",
            endDate = r.endAt?.let { millisToFaDate(it) } ?: "—",
            status = "قرارداد فعال"
        )
    }

    // ------------------------------------------------------------- بخش‌ها
    /** گرید ۱۸ سرویس با شمارنده و خط بعدی واقعی */
    fun toSections(records: List<Record>, now: Long): List<SectionCard> {
        val byType = records.groupBy { resolveType(it) }
        return SectionType.entries.map { st ->
            val key = serviceKey(st)
            val list = byType[key].orEmpty()
            val next = list.minByOrNull { currentOccurrence(it, now) ?: Long.MAX_VALUE }
            val nextOcc = next?.let { currentOccurrence(it, now) }
            val paidAll = list.isNotEmpty() && list.all { r ->
                val o = currentOccurrence(r, now) ?: return@all false
                isPaid(r, o)
            }
            SectionCard(
                type = st,
                count = list.count { r ->
                    val o = currentOccurrence(r, now) ?: return@count false
                    !isPaid(r, o)
                },
                subtitle = if (list.isEmpty()) "بدون مورد فعال" else {
                    val sum = list.sumOf { it.amount }
                    if (sum > 0) "${money(sum)} در جریان" else "${list.size.toFa()} مورد"
                },
                nextLine = when {
                    next == null -> "—"
                    nextOcc == null -> "—"
                    else -> "${millisToFaShort(nextOcc)} · ${daysLabel(daysLeft(nextOcc, now))}"
                },
                percent = if (list.isEmpty()) 0 else {
                    (list.count { r ->
                        val o = currentOccurrence(r, now) ?: return@count false
                        isPaid(r, o)
                    } * 100 / list.size)
                },
                tone = when {
                    list.isEmpty() -> TTone.NEUTRAL
                    paidAll -> TTone.GREEN
                    else -> when (st) {
                        SectionType.CHECK -> TTone.PURPLE
                        SectionType.RENT, SectionType.INSURANCE -> TTone.BLUE
                        SectionType.SHOPPING, SectionType.BILLS -> TTone.GREEN
                        SectionType.BUILDING, SectionType.ANNIVERSARY -> TTone.RED
                        else -> TTone.GOLD
                    }
                }
            )
        }
    }

    /** کلید متنی هر SectionType برای گروه‌بندی رکوردها */
    fun serviceKey(st: SectionType): String = when (st) {
        SectionType.INSTALLMENT -> "installment"
        SectionType.CHECK -> "check"
        SectionType.RENT -> "rent"
        SectionType.SUBSCRIPTION -> "subscription"
        SectionType.MEETING -> "meeting"
        SectionType.MEDICINE -> "medicine"
        SectionType.SHOPPING -> "shopping"
        SectionType.NOTE -> "note"
        SectionType.TRUST -> "trust"
        SectionType.BILLS -> "bills"
        SectionType.BUILDING -> "building"
        SectionType.TRAVEL -> "travel"
        SectionType.INSURANCE -> "insurance"
        SectionType.CAR -> "car"
        SectionType.SALARY -> "salary"
        SectionType.POCKET -> "pocket"
        SectionType.BIRTHDAY -> "birthday"
        SectionType.ANNIVERSARY -> "anniversary"
    }

    // -------------------------------------------------------------- متریک
    /** نوار متریک صفحهٔ خانه از دادهٔ واقعی */
    fun homeMetrics(records: List<Record>, now: Long): List<Metric> {
        val installments = records.filter { resolveType(it) in setOf("installment", "loan") }
        val thisMonthInstallment = installments.sumOf { r ->
            val occ = currentOccurrence(r, now) ?: return@sumOf 0L
            if (com.hossein.yademan.tv.core.jalaliMonthsBetween(com.hossein.yademan.tv.core.jalaliMonthStart(now), occ) == 0) r.amount else 0L
        }
        val cheques = records.filter { resolveType(it) in setOf("check", "cheque") }
        val chequeSum = cheques.sumOf { r ->
            val occ = currentOccurrence(r, now) ?: return@sumOf 0L
            if (!isPaid(r, occ)) r.amount else 0L
        }
        val paidToday = records.count { r ->
            val occ = currentOccurrence(r, now) ?: return@count false
            daysLeft(occ, now) == 0L && isPaid(r, occ)
        }
        return listOf(
            Metric("اقساط این ماه", money(thisMonthInstallment)),
            Metric("چک در جریان", money(chequeSum)),
            Metric("پرداخت‌شده", "${paidToday.toFa()} مورد", TColor.GreenInk)
        )
    }

    /** خلاصهٔ سه‌کارتی بالای صفحهٔ اقساط */
    fun installmentSummary(records: List<Record>, now: Long): Triple<Long, Int, Long> {
        val list = records.filter { resolveType(it) in setOf("installment", "loan") }
        val thisMonth = list.sumOf { r ->
            val occ = currentOccurrence(r, now) ?: return@sumOf 0L
            if (com.hossein.yademan.tv.core.jalaliMonthsBetween(com.hossein.yademan.tv.core.jalaliMonthStart(now), occ) == 0) r.amount else 0L
        }
        val paid = list.sumOf { paidCount(it, now) }
        val remaining = list.sumOf { r ->
            val total = totalOccurrences(r)
            val idx = occurrenceIndex(r, currentOccurrence(r, now) ?: System.currentTimeMillis())
            ((total - idx).coerceAtLeast(0) * r.amount)
        }
        return Triple(thisMonth, paid, remaining)
    }

    /** مجموع چک‌های در جریان */
    fun chequesTotal(records: List<Record>, now: Long): Long =
        records.filter { resolveType(it) in setOf("check", "cheque") }
            .sumOf { r ->
                val occ = currentOccurrence(r, now) ?: return@sumOf 0L
                if (!isPaid(r, occ)) r.amount else 0L
            }

    /** مجموع قبوض پرداخت‌نشده */
    fun billsTotal(records: List<Record>, now: Long): Long =
        records.filter { resolveType(it) in setOf("bills", "bill", "building") }
            .sumOf { r ->
                val occ = currentOccurrence(r, now) ?: return@sumOf 0L
                if (!isPaid(r, occ)) r.amount else 0L
            }

    /** رنگ خنثی برای رکوردهای بدون سرویس */
    val neutralColor: Color = TColor.Muted2
}
