package com.hossein.yademan.tv.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * ============================================================================
 *  فایل: ui/components/QrImage.kt
 *  بخش: تولید QR واقعی با ZXing
 *
 *  محتوای QR دقیقاً رشتهٔ جفت‌سازی است:
 *  {"v":1,"h":"kafezare.ir","p":"<pair_id>","c":"<code>","n":"<nonce>"}
 *  بدون هیچ فاصله‌ای. حاشیهٔ سفید = ۴ ماژول (پیش‌فرض استاندارد).
 * ============================================================================
 */
@Composable
fun RealQr(
    payload: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val bitmap: Bitmap? = remember(payload, size) {
        runCatching {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN to 4
            )
            // اندازهٔ پیکسلی با چگالی صفحه (۳۲۰dpi → dp×۲)
            val px = (size.value * 2f).toInt().coerceAtLeast(200)
            val matrix = QRCodeWriter().encode(
                payload,
                BarcodeFormat.QR_CODE,
                px,
                px,
                hints
            )
            val bmp = Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
            for (x in 0 until matrix.width) {
                for (y in 0 until matrix.height) {
                    bmp.setPixel(x, y, if (matrix[x, y]) 0xFF0B0E14.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            bmp
        }.getOrNull()
    }

    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size / 6))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "کد QR جفت‌سازی تلویزیون",
                modifier = Modifier.size(size)
            )
        }
    }
}
