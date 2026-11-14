# یادمان تلویزیون (Android TV) — نقشهٔ کد، پروتکل سرور و راهنمای تحویل

نسخهٔ **۱٫۴٫۰ (versionCode 140)** · بستهٔ `com.hossein.yademan.tv` · ۱۱ صفحه · Kotlin + Jetpack Compose

این پروژه **همان ظاهر و همان سرعت** نسخهٔ قبلی را دارد؛ تغییرها فقط در «رفتار» و «داده» است:
جفت‌سازی واقعی با QR، همگام‌سازی افزایشی با سرور، محاسبهٔ درست وقوع/قسط/وضعیت پرداخت،
و حالت‌های آفلاین / نشست منقضی. هیچ رنگ، فونت، فاصله یا اندازه‌ای نسبت به طراحی عوض نشده است.

| مورد | مقدار |
|---|---|
| بستهٔ برنامه (package / namespace) | `com.hossein.yademan.tv` |
| applicationId | `com.hossein.yademan.tv` |
| نام اپ | **یادمان** |
| versionName / versionCode | `1.4.0` / `140` |
| کمینهٔ اندروید | ۷٫۰ (API 24) · هدف API 35 |
| Kotlin / AGP / Gradle | 2.0.20 / 8.5.2 / 8.7 |
| Compose | BOM 2024.09.03 · material3 · icons-extended |
| کتابخانه‌های افزوده | `zxing:core:3.5.3` (QR واقعی) · `security-crypto:1.1.0-alpha06` (توکن رمزنگاری‌شده) · `desugar_jdk_libs:2.0.4` |
| بوم طراحی | ۱۹۲۰×۱۰۸۰ px · density 320 · **dp = px ÷ 2** |
| تصاویر مرجع | `design/screens/01-splash.png` … `11-account.png` |
| سند طراحی کامل | `design/YADEMAN-TV-HANDOFF.md` |

> ✅ کامپایل واقعی انجام شد: `> Task :app:compileDebugKotlin — BUILD SUCCESSFUL`
>
> دستور ساخت (همان دستوری که تست شده):
> ```
> export JAVA_HOME=<jdk17> ANDROID_HOME=<android-sdk>
> ./gradlew :app:compileDebugKotlin
> ```

---

## ۱) ساختار پروژه

```
YademanTV/
├── settings.gradle.kts · build.gradle.kts · gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
├── README-CODE.md                    ← همین فایل
├── design/                           ← مرجع طراحی (سند + ۱۱ اسکرین + نقشهٔ کد)
└── app/
    ├── build.gradle.kts              ← وابستگی‌ها + desugaring + versionCode 140
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml       ← LEANBACK_LAUNCHER + leanback + INTERNET
        ├── res/                      ← ۶ وزن Vazirmatn UI-FD · themes · آیکون و بنر
        └── java/com/hossein/yademan/tv/
            ├── MainActivity.kt
            ├── core/
            │   ├── Format.kt         ← اعداد فارسی، مبلغ، درصد
            │   ├── Jalali.kt         ← تقویم جلالی ↔ میلادی، برچسب روز
            │   ├── TimeFormats.kt    ← سه فرمت زمانی سرور (MySQL / ISO-8601 / روز)
            │   └── Occurrence.kt     ← نوع سرویس، وقوع جاری، شمارهٔ قسط، وضعیت پرداخت
            ├── data/
            │   ├── Models.kt         ← مدل‌های UI (۱۸ سرویس)
            │   ├── SampleData.kt     ← دادهٔ مرجع طراحی (پشتوانهٔ حالت بدون داده)
            │   ├── Record.kt         ← رکورد سرور + پارس امن meta_json
            │   ├── RemoteApi.kt      ← کلاینت HTTP سرور (بدون Retrofit)
            │   ├── TvStore.kt        ← ذخیرهٔ امن توکن (EncryptedSharedPreferences)
            │   ├── PairingController.kt ← ماشین حالت جفت‌سازی QR
            │   ├── SyncEngine.kt     ← همگام‌سازی افزایشی + حلقهٔ has_more
            │   ├── RecordMapper.kt   ← نگاشت رکورد سرور → کارت‌های UI
            │   ├── DataHub.kt        ← منبع واحد داده برای همهٔ صفحه‌ها
            │   └── YademanTvViewModel.kt ← نشست، ورود، همگام‌سازی، انقضا
            └── ui/
                ├── YademanTvApp.kt   ← ناوبری ۱۱ صفحه + نوار آفلاین + دیالوگ‌ها
                ├── theme/            ← TColor · TType · TDimens · TTheme
                ├── components/       ← Atoms · Cards · Chrome · QrImage · StatusBars · Focus
                └── screens/          ← ۱۱ فایل صفحه + ServiceScreen
```

---

## ۲) جدول «نام فایل ← صفحهٔ طراحی»

| صفحه | فایل کد | نام بخش | فوکوس اول (D-pad) |
|---|---|---|---|
| ۰۱ | `ui/screens/SplashScreen.kt` | اسپلش / بررسی توکن + اولین همگام‌سازی | ندارد (خودکار) |
| ۰۲ | `ui/screens/LoginScreen.kt` | ورود: QR زنده + کد ۶ رقمی + فرم دستی | فیلد «نام کاربری» |
| ۰۳ | `ui/screens/HomeScreen.kt` | یادآوری‌های امروز | تب «امروز» |
| ۰۴ | `ui/screens/SectionsScreen.kt` | همهٔ بخش‌ها (گرید ۶×۳ = ۱۸ سرویس) | تایل «اقساط» |
| ۰۵ | `ui/screens/InstallmentsScreen.kt` | اقساط | اولین کارت قسط |
| ۰۶ | `ui/screens/InstallmentDetailScreen.kt` | جزئیات قسط | دکمهٔ «پرداخت‌شده» |
| ۰۷ | `ui/screens/ChequesScreen.kt` | چک‌ها | اولین چک |
| ۰۸ | `ui/screens/NotesScreen.kt` | یادداشت‌ها | اولین یادداشت |
| ۰۹ | `ui/screens/MedicinesScreen.kt` | داروها | اولین دارو |
| ۱۰ | `ui/screens/BillsRentScreen.kt` | قبوض و اجاره | کارت اجاره |
| ۱۱ | `ui/screens/AccountScreen.kt` | حساب کاربری | اولین ردیف |
| — | `ui/screens/ServiceScreen.kt` | ۱۲ سرویس باقی‌مانده (اشتراک، جلسه، خرید، امانت، شارژ، سفر، بیمه، خودرو، حقوق، پول توجیبی، تولد، سالگرد) | اولین کارت |

ریل راست همیشه ۸ آیتم + «خروج» دارد و نوار راهنمای ریموت (`RemoteHint`) در **همهٔ** صفحه‌ها هست.

---

## ۳) پروتکل جفت‌سازی تلویزیون (TV Pairing)

آدرس پایه: `https://kafezare.ir/wp-json/lifeplanner/v1/`

### ۳.۱ شروع — `POST tv/pair/start`
```json
{ "device_name": "تلویزیون نشیمن", "device_model": "Android TV", "app_version": "1.4.0" }
```
پاسخ: `pair_id`, `code` (۶ رقمی), `nonce` (۸ کاراکتر hex), `expires_in` (۳۰۰ ثانیه), `poll_interval` (۳)

### ۳.۲ محتوای QR (دقیق، بدون فاصله)
```
{"v":1,"h":"kafezare.ir","p":"<pair_id>","c":"<code>","n":"<nonce>"}
```
ترتیب کلیدها `v,h,p,c,n` ثابت است و در `data/PairingController.kt → qrPayload()` ساخته می‌شود.
رندر واقعی با ZXing: `ui/components/QrImage.kt` — اندازهٔ قاب ۲۴۰dp، حاشیهٔ سفید ۴ ماژول،
کد ۶ رقمی زیر QR با اندازهٔ ۳۲sp.

### ۳.۳ انتظار — `POST tv/pair/poll` هر ۳ ثانیه
```json
{ "pair_id": "…", "nonce": "…" }
```
`status` یکی از: `pending` | `approved` (+ `token`, `tv_device_id`) | `expired` | `denied`

قواعد پیاده‌شده:
- **توکن فقط یک‌بار تحویل داده می‌شود** → همان لحظه در `TvStore` ذخیره می‌شود.
- شمارش معکوس ۳۰۰ ثانیه در UI؛ در پایان `expired` + دکمهٔ «دریافت کد جدید».
- خطای موقت شبکه، حلقه را قطع نمی‌کند (تا پایان مهلت ادامه می‌دهد).
- `ui/components/QrCard.kt` چهار حالت را نشان می‌دهد: `pending / approved / expired / denied` و `error`.

### ۳.۴ ورود دستی (استثنا)
دکمهٔ «ورود به حساب» **اول دیالوگ هشدار** نشان می‌دهد:
«با ورود دستی، نشست حساب روی گوشی شما خارج می‌شود…» و بعد `POST auth/login` صدا زده می‌شود.
در حالت عادی هیچ‌جا `auth/login` صدا زده نمی‌شود.

---

## ۴) همگام‌سازی (Sync)

`POST sync/pull`
```json
{ "token": "…", "device_id": "…", "last_sync": "2026-11-13 15:31:56" }
```
پاسخ: `records[]`, `last_sync`, `has_more`, `read_only`

- **زمان‌ها:** هر ۱۵ دقیقه + باز شدن اپ + برگشت از standby (`ON_RESUME`) + دکمهٔ دستی.
- **حلقهٔ `has_more`:** تا `false` شدن ادامه می‌دهد (`SyncEngine.syncOnce`، سقف ۲۰ صفحه برای اطمینان).
- **`last_sync`** در `TvStore` ذخیره می‌شود و در درخواست بعدی ارسال می‌شود.
- **ادغام:** رکوردها بر اساس `id` ادغام می‌شوند (نسخهٔ جدیدتر برنده است).
- **۴۰۱:** توکن پاک می‌شود → دیالوگ «نشست شما منقضی شده است» → صفحهٔ ورود.
- **آفلاین:** نوار زرد بالای همهٔ صفحه‌ها + نمایش آخرین دادهٔ ذخیره‌شده.

## ۵) محاسبهٔ وقوع، قسط و پرداخت (`core/Occurrence.kt`)

1. **نوع سرویس** با زنجیرهٔ `meta.type` → `meta.hub` → `category_id` تعیین می‌شود؛
   `type="other"` به `hub` می‌رود و اگر آن هم نبود، «بخش‌های سفارشی».
2. **وقوع جاری** = اولین وقوع از **ابتدای ماه شمسی جاری** به بعد (نه `due_at` خام).
3. **شمارهٔ قسط** از `occurrenceIndex()` می‌آید، **نه** `paidCount + 1`.
4. **وضعیت پرداخت** برای تکرارشونده‌ها از `done_dates` می‌آید و `is_done` نادیده گرفته می‌شود؛
   برای یک‌باره‌ها از `is_done`.
5. **پیشرفت** = `occurrenceIndex / totalOccurrences`.
6. **`daysLeft`** نسبت به ابتدای روز محلی محاسبه می‌شود (نه ساعت).

### تله‌های داده که در کد حل شده‌اند
| تله | جای حل |
|---|---|
| `meta_json` هم رشته است هم آبجکت | `data/Record.kt → Meta.parse` |
| کلیدهای متا با فاصله (`" hub "`) | `Meta` — کلیدها trim و lowercase می‌شوند |
| ارقام فارسی داخل متا | `toAsciiDigits()` در `TimeFormats.kt` / `Meta.str` |
| سه فرمت زمانی مختلف | `TimeFmt.parseMysqlUtc` / `parseIsoUtc` / `parseAny` |
| `due_at` خام ≠ وقوع جاری | `currentOccurrence()` |
| `index ≠ paidCount + 1` | `occurrenceIndex()` |
| `type="other"` | `resolveType()` → `hub` |
| `has_more` نادیده | `SyncEngine.syncOnce()` |
| توکن یک‌بارمصرف | `PairingController.pollUntilDone()` + `TvStore` |

---

## ۶) لایهٔ نمایش: چرا صفحه‌ها عوض نشده‌اند؟

همهٔ صفحه‌ها از `data/DataHub.kt` می‌خوانند. این لایه برای هر بخش:

- اگر سرور رکوردی برای آن بخش فرستاده باشد → مقدار **واقعی** محاسبه‌شده (`RecordMapper`)
- اگر نفرستاده باشد (یا آفلاین باشیم) → همان **دادهٔ مرجع طراحی** (`SampleData`)

نتیجه: ظاهر هیچ صفحه‌ای خالی یا شکسته نمی‌شود، اما به‌محض رسیدن داده، اعداد و کارت‌ها واقعی می‌شوند.

---

## ۷) قواعد مهم نسخهٔ تلویزیون

1. **فقط‌خواندنی:** هیچ خرید/پرداخت/حذف/ویرایشی در TV انجام نمی‌شود؛ `read_only` سرور هم رعایت می‌شود.
2. **ناوبری ریموت:** `◀▶` افقی، `▲▼` عمودی، `OK` انتخاب، `Back` بازگشت؛ در خانه `Back` = دیالوگ خروج.
3. **فوکوس:** همهٔ عناصر تعاملی از `FocusSurface` → حلقهٔ طلایی `#E8B84B` + هاله + بالا‌آمدن ۳dp.
4. **اندازه‌ها:** هر عدد سند به px بوم ۱۹۲۰ است؛ در کد `px(v)` = v÷2 dp.
5. **Overscan:** پدینگ `TD.side / TD.top / TD.bottom` روی همهٔ صفحه‌ها.
6. **فونت:** ۶ وزن Vazirmatn UI-FD داخل `res/font`؛ ارقام فارسی با فونت + `core.toFa()`.
7. **java.time روی API 24:** `isCoreLibraryDesugaringEnabled = true` + `desugar_jdk_libs:2.0.4`.

---

## ۸) «چه فایل‌هایی را از پروژهٔ خودم بفرستم؟»

اگر می‌خواهی این نسخه را روی پروژهٔ فعلی‌ات (که قبلاً تحویل داده شد) بگذاری، **همهٔ** فایل‌های زیر را
از مسیر `app/src/main/java/com/hossein/yademan/tv/` بفرست (یا کل پوشه را یک‌جا):

**الزامی — هستهٔ منطق (جدید یا تغییرکرده):**
1. `core/TimeFormats.kt`
2. `core/Occurrence.kt`
3. `core/Jalali.kt`
4. `core/Format.kt`
5. `data/Record.kt`
6. `data/RemoteApi.kt`
7. `data/TvStore.kt`
8. `data/PairingController.kt`
9. `data/SyncEngine.kt`
10. `data/RecordMapper.kt`
11. `data/DataHub.kt`
12. `data/YademanTvViewModel.kt`
13. `data/Models.kt` + `data/SampleData.kt` (بدون تغییر، ولی باید بمانند)

**الزامی — UI که به دادهٔ واقعی وصل شد:**
14. `ui/YademanTvApp.kt`
15. `ui/screens/LoginScreen.kt` (بازنویسی کامل: QR واقعی + هشدار ورود دستی)
16. `ui/screens/HomeScreen.kt` · `SectionsScreen.kt` · `InstallmentsScreen.kt` (فوکوس اول + DataHub)
17. `ui/screens/InstallmentDetailScreen.kt` · `ChequesScreen.kt` · `NotesScreen.kt` · `MedicinesScreen.kt` · `BillsRentScreen.kt` · `AccountScreen.kt` · `ServiceScreen.kt` · `SplashScreen.kt`
18. `ui/components/Atoms.kt` · `Cards.kt` · `Chrome.kt` (فوکوس اول + QR واقعی)
19. **جدید:** `ui/components/QrImage.kt` · `ui/components/StatusBars.kt` · `ui/components/Focus.kt`
20. `MainActivity.kt` (بدون تغییر)

**الزامی — تنظیمات و منابع:**
21. `app/build.gradle.kts` (پکیج، نسخهٔ ۱۴۰، وابستگی‌های zxing و security-crypto)
22. `app/proguard-rules.pro` (نام پکیج)
23. `app/src/main/AndroidManifest.xml`
24. `app/src/main/res/` کامل (فونت‌ها، تم، آیکون، بنر)
25. `settings.gradle.kts` · `build.gradle.kts` · `gradle.properties` · `gradle/wrapper/gradle-wrapper.properties`

اگر فقط «منطق» را می‌خواهی و ظاهر را دست نمی‌زنی، حداقلِ لازم: **۱ تا ۱۳** + `app/build.gradle.kts` + دو فایل `QrImage.kt` و `StatusBars.kt`.

> ⚠️ اگر پوشهٔ `java/ir/kafezare/yademan/tv` قدیمی هنوز در پروژه است، **پاکش کن**؛ وگرنه دو نسخه از کلاس‌ها با پکیج‌های متفاوت هم‌زمان کامپایل می‌شوند.

---

## ۹) چطور در Android Studio باز کنم؟

1. `File > Open…` → پوشهٔ `YademanTV` → OK
2. Sync که تمام شد، Run configuration = `app` → ▶
3. برای تست: Android TV (1080p) یا Television emulator؛ روی موبایل هم اجرا می‌شود (`LAUNCHER`).

---

## ۱۰) تفاوت‌های عمدی با نسخهٔ موبایل

| موضوع | موبایل | تلویزیون |
|---|---|---|
| ناوبری | نوار پایین ۴ تب + FAB | ریل راست ۶۲dp (۸ آیتم + خروج) |
| تعامل | لمس | فوکوس D-pad + OK/Back |
| چیدمان | تک‌ستون | گریدهای ۲×۲، ۳×۲، ۶×۳ |
| ایجاد/ویرایش | کامل | ندارد — ارجاع به موبایل |
| مقیاس | dp = px ÷ ۳ | dp = px ÷ ۲ |

---

_همهٔ رنگ‌ها، اندازه‌ها و متن‌ها از `design/YADEMAN-TV-HANDOFF.md` گرفته شده‌اند. برای تغییر ظاهر، اول `TColor.kt` و `TDimens.kt` را ببینید._
