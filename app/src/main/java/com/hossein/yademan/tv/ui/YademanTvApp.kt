package com.hossein.yademan.tv.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hossein.yademan.tv.data.DataHub
import com.hossein.yademan.tv.data.SectionType
import com.hossein.yademan.tv.data.YademanTvViewModel
import com.hossein.yademan.tv.ui.components.GhostButton
import com.hossein.yademan.tv.ui.components.NavRail
import com.hossein.yademan.tv.ui.components.OfflineBar
import com.hossein.yademan.tv.ui.components.PrimaryButton
import com.hossein.yademan.tv.ui.components.RailDest
import com.hossein.yademan.tv.ui.components.SessionExpiredDialog
import com.hossein.yademan.tv.ui.screens.AccountScreen
import com.hossein.yademan.tv.ui.screens.BillsRentScreen
import com.hossein.yademan.tv.ui.screens.ChequesScreen
import com.hossein.yademan.tv.ui.screens.HomeScreen
import com.hossein.yademan.tv.ui.screens.InstallmentDetailScreen
import com.hossein.yademan.tv.ui.screens.InstallmentsScreen
import com.hossein.yademan.tv.ui.screens.LoginScreen
import com.hossein.yademan.tv.ui.screens.MedicinesScreen
import com.hossein.yademan.tv.ui.screens.NotesScreen
import com.hossein.yademan.tv.ui.screens.SectionsScreen
import com.hossein.yademan.tv.ui.screens.ServiceScreen
import com.hossein.yademan.tv.ui.screens.SplashScreen
import com.hossein.yademan.tv.ui.theme.TColor
import com.hossein.yademan.tv.ui.theme.TD
import com.hossein.yademan.tv.ui.theme.TText
import com.hossein.yademan.tv.ui.theme.TvBackground
import kotlinx.coroutines.delay

/**
 * ============================================================================
 *  فایل: ui/YademanTvApp.kt
 *  بخش: ناوبری سراسری اپ تلویزیون (۱۱ صفحه) + ریل سمت راست + پشتهٔ برگشت
 *
 *  جریان: Splash (بررسی توکن + اولین همگام‌سازی) → Login (QR) → Main
 *  سراسری‌ها: نوار آفلاین بالای همهٔ صفحه‌ها، دیالوگ انقضای نشست، دیالوگ خروج
 * ============================================================================
 */

/** مقصدهای اپ تلویزیون */
sealed interface TvDest {
    data object Home : TvDest
    data object Sections : TvDest
    data object Installments : TvDest
    data class InstallmentDetail(val id: String) : TvDest
    data object Cheques : TvDest
    data object Notes : TvDest
    data object Medicines : TvDest
    data object BillsRent : TvDest
    data object Account : TvDest
    data class Service(val type: SectionType) : TvDest
}

@Composable
fun YademanTvApp(vm: YademanTvViewModel = viewModel()) {

    var stack by remember { mutableStateOf<List<TvDest>>(listOf(TvDest.Home)) }
    var showExitDialog by remember { mutableStateOf(false) }

    val current = stack.last()
    val stage = vm.stage

    fun push(d: TvDest) { stack = stack + d }
    fun pop() { if (stack.size > 1) stack = stack.dropLast(1) }

    // ------------------------------------------------------- شروع اپ
    LaunchedEffect(Unit) { vm.boot() }

    // ساعت/تاریخ و محاسبات روزانه — هر ۳۰ ثانیه تازه می‌شود
    LaunchedEffect(Unit) {
        while (true) {
            DataHub.refreshNow()
            delay(30_000L)
        }
    }

    // برگشت از standby یا بازگشت به اپ → همگام‌سازی فوری (الزام سند)
    val lifecycle = LocalLifecycleOwner.current
    DisposableEffect(lifecycle, stage) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && stage == YademanTvViewModel.Stage.MAIN) {
                vm.syncNow()
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        onDispose { lifecycle.lifecycle.removeObserver(observer) }
    }

    // ------------------------------------------------------------ اسپلش
    if (stage == YademanTvViewModel.Stage.SPLASH) {
        TvBackground { SplashScreen(onFinish = { }) }
        return
    }

    // -------------------------------------------------------------- ورود
    if (stage == YademanTvViewModel.Stage.LOGIN) {
        TvBackground {
            LoginScreen(
                pairState = vm.pairState,
                username = vm.username,
                password = vm.password,
                errorText = vm.errorText,
                busy = vm.busy,
                onUsername = vm::onUsername,
                onPassword = vm::onPassword,
                onSubmit = vm::submitManual,
                onRetryPair = vm::startPairing
            )
        }
        return
    }

    // ------------------------------------------------------------- اپ اصلی
    BackHandler(enabled = true) {
        when {
            showExitDialog -> showExitDialog = false
            stack.size > 1 -> pop()
            else -> showExitDialog = true
        }
    }

    TvBackground {
        Column(Modifier.fillMaxSize()) {
            // نوار آفلاین روی همهٔ صفحه‌ها
            OfflineBar(visible = vm.offline)

            Row(Modifier.weight(1f)) {
                // محتوای صفحه (سمت چپِ ریل در RTL)
                Box(Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = current,
                        transitionSpec = {
                            (slideInHorizontally(initialOffsetX = { it / 12 }) + fadeIn()) togetherWith
                                    (slideOutHorizontally(targetOffsetX = { -it / 16 }) + fadeOut())
                        },
                        label = "tvScreen"
                    ) { dest ->
                        when (dest) {
                            is TvDest.Home -> HomeScreen(
                                onOpenSection = { push(destFor(it)) },
                                onOpenInstallments = { push(TvDest.Installments) },
                                onOpenCheques = { push(TvDest.Cheques) }
                            )

                            is TvDest.Sections -> SectionsScreen(
                                onOpen = { push(destFor(it)) }
                            )

                            is TvDest.Installments -> InstallmentsScreen(
                                onBack = { pop() },
                                onOpen = { id -> push(TvDest.InstallmentDetail(id)) }
                            )

                            is TvDest.InstallmentDetail -> {
                                val inst = DataHub.installments.firstOrNull { it.id == dest.id }
                                    ?: DataHub.installments.firstOrNull()
                                if (inst != null) {
                                    InstallmentDetailScreen(inst = inst, onBack = { pop() })
                                } else {
                                    InstallmentsScreen(onBack = { pop() }, onOpen = { })
                                }
                            }

                            is TvDest.Cheques -> ChequesScreen(
                                onBack = { pop() },
                                onSync = { vm.syncNow() }
                            )

                            is TvDest.Notes -> NotesScreen(
                                notes = DataHub.notes,
                                onBack = { pop() },
                                onToggleItem = { noteId, itemId -> DataHub.toggleItem(noteId, itemId) }
                            )

                            is TvDest.Medicines -> MedicinesScreen(
                                medicines = DataHub.medicines,
                                onBack = { pop() },
                                onDose = { _, _ -> } // تلویزیون فقط‌خواندنی است
                            )

                            is TvDest.BillsRent -> BillsRentScreen(
                                rent = DataHub.rent,
                                bills = DataHub.bills,
                                onBack = { pop() }
                            )

                            is TvDest.Account -> AccountScreen(
                                userName = DataHub.USER_NAME,
                                userId = DataHub.USER_ID,
                                onBack = { pop() },
                                onSync = { vm.syncNow() },
                                onLogout = {
                                    vm.logout()
                                    stack = listOf(TvDest.Home)
                                }
                            )

                            is TvDest.Service -> ServiceScreen(
                                type = dest.type,
                                onBack = { pop() }
                            )
                        }
                    }
                }

                // ریل ناوبری سمت راست
                NavRail(
                    current = railFor(current),
                    onSelect = { dest ->
                        when (dest) {
                            RailDest.EXIT -> showExitDialog = true
                            RailDest.HOME -> stack = listOf(TvDest.Home)
                            RailDest.SECTIONS -> stack = listOf(TvDest.Sections)
                            RailDest.INSTALLMENTS -> stack = listOf(TvDest.Installments)
                            RailDest.CHEQUES -> stack = listOf(TvDest.Cheques)
                            RailDest.NOTES -> stack = listOf(TvDest.Notes)
                            RailDest.MEDICINES -> stack = listOf(TvDest.Medicines)
                            RailDest.BILLS -> stack = listOf(TvDest.BillsRent)
                            RailDest.ACCOUNT -> stack = listOf(TvDest.Account)
                        }
                    }
                )
            }
        }

        // ----------------------------------------------- دیالوگ خروج
        if (showExitDialog) {
            ExitDialog(
                onDismiss = { showExitDialog = false },
                onConfirm = {
                    showExitDialog = false
                    vm.logout()
                    stack = listOf(TvDest.Home)
                }
            )
        }

        // ------------------------------------- دیالوگ انقضای نشست (۴۰۱)
        SessionExpiredDialog(
            visible = vm.sessionExpired,
            onLogin = {
                vm.acknowledgeExpiry()
                stack = listOf(TvDest.Home)
            }
        )
    }
}

/** نگاشت سرویس → مقصد */
fun destFor(type: SectionType): TvDest = when (type) {
    SectionType.INSTALLMENT -> TvDest.Installments
    SectionType.CHECK -> TvDest.Cheques
    SectionType.NOTE, SectionType.SHOPPING -> TvDest.Notes
    SectionType.MEDICINE -> TvDest.Medicines
    SectionType.RENT, SectionType.BILLS, SectionType.BUILDING -> TvDest.BillsRent
    else -> TvDest.Service(type)
}

/** کدام آیتم ریل فعال است */
private fun railFor(d: TvDest): RailDest = when (d) {
    is TvDest.Home -> RailDest.HOME
    is TvDest.Sections -> RailDest.SECTIONS
    is TvDest.Installments, is TvDest.InstallmentDetail -> RailDest.INSTALLMENTS
    is TvDest.Cheques -> RailDest.CHEQUES
    is TvDest.Notes -> RailDest.NOTES
    is TvDest.Medicines -> RailDest.MEDICINES
    is TvDest.BillsRent -> RailDest.BILLS
    is TvDest.Account -> RailDest.ACCOUNT
    is TvDest.Service -> RailDest.SECTIONS
}

/** دیالوگ تأیید خروج از حساب */
@Composable
private fun ExitDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(TD.rLg))
                .background(TColor.Card)
                .border(1.dp, TColor.Line, RoundedCornerShape(TD.rLg))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text("خروج از حساب", style = TText.titleS, color = TColor.Ink)
            Text(
                "برای دیدن یادآوری‌ها باید دوباره وارد شوی. خارج می‌شوی؟",
                style = TText.micro,
                color = TColor.Muted2
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PrimaryButton("خروج", onConfirm, small = true)
                GhostButton("انصراف", onDismiss, small = true)
            }
        }
    }
}
