package com.hossein.yademan.tv.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.delay

/**
 * ============================================================================
 *  فایل: ui/components/Focus.kt
 *  بخش: «فوکوس اول» هر صفحه — جدول بخش ۱۴ سند طراحی
 *
 *    Splash      → ندارد (خودکار)
 *    Login       → فیلد نام کاربری
 *    Home        → تب «امروز»
 *    Sections    → تایل «اقساط»
 *    Installments→ اولین کارت قسط
 *
 *  چون آیتم‌های LazyColumn/LazyVerticalGrid کمی دیرتر ساخته می‌شوند،
 *  درخواست فوکوس چند بار (با فاصلهٔ کوتاه) تکرار می‌شود.
 * ============================================================================
 */
@Composable
fun rememberFirstFocus(): FocusRequester {
    val requester = remember { FocusRequester() }
    LaunchedEffect(requester) {
        repeat(4) { attempt ->
            delay(if (attempt == 0) 40L else 90L)
            runCatching { requester.requestFocus() }
        }
    }
    return requester
}
