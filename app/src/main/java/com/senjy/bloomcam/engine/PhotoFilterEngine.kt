package com.senjy.bloomcam.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.senjy.bloomcam.data.FilterType
import kotlin.math.max
import kotlin.math.min

object PhotoFilterEngine {

    /**
     * Applies the chosen [FilterType] to the given [src] bitmap with [intensity] (0.0f .. 1.0f).
     * Returns a new processed Bitmap.
     */
    fun applyFilter(src: Bitmap, filter: FilterType, intensity: Float = 1.0f): Bitmap {
        if (filter == FilterType.ORIGINAL || intensity <= 0.01f) {
            return src.copy(src.config ?: Bitmap.Config.ARGB_8888, true)
        }

        val width = src.width
        val height = src.height

        // Result bitmap
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val basePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Color adjustment matrix (Brightness, Contrast, Saturation)
        val cm = ColorMatrix()

        // Saturation
        val effectiveSat = 1.0f + (filter.saturation - 1.0f) * intensity
        cm.setSaturation(effectiveSat)

        // Brightness & Contrast
        val effectiveContrast = 1.0f + (filter.contrast - 1.0f) * intensity
        val effectiveBrightness = filter.brightness * 255f * intensity

        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                effectiveContrast, 0f, 0f, 0f, effectiveBrightness,
                0f, effectiveContrast, 0f, 0f, effectiveBrightness,
                0f, 0f, effectiveContrast, 0f, effectiveBrightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        cm.postConcat(contrastMatrix)
        basePaint.colorFilter = ColorMatrixColorFilter(cm)

        // Draw base adjusted bitmap
        canvas.drawBitmap(src, 0f, 0f, basePaint)

        // 2. Soft Skin Smoothing / Bloom Glow (Downscale blur and overlay with soft alpha)
        if (filter.softBlurRadius > 0 && intensity > 0.05f) {
            val blurScale = 0.25f
            val smallW = max(1, (width * blurScale).toInt())
            val smallH = max(1, (height * blurScale).toInt())

            val small = Bitmap.createScaledBitmap(src, smallW, smallH, true)
            val blurred = fastBoxBlur(small, filter.softBlurRadius)

            val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                alpha = ((0.30f * intensity) * 255).toInt().coerceIn(0, 255)
            }
            canvas.drawBitmap(Bitmap.createScaledBitmap(blurred, width, height, true), 0f, 0f, glowPaint)
            small.recycle()
            blurred.recycle()
        }

        // 3. Subtle Warm/Pastel Tint Overlay
        if (filter.tintAlpha > 0.01f && intensity > 0.05f) {
            val effectiveTintAlpha = (filter.tintAlpha * intensity).coerceIn(0.0f, 1.0f)
            val tintColorInt = android.graphics.Color.argb(
                (effectiveTintAlpha * 255).toInt(),
                (filter.tintR * 255).toInt(),
                (filter.tintG * 255).toInt(),
                (filter.tintB * 255).toInt()
            )
            val tintPaint = Paint().apply {
                color = tintColorInt
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), tintPaint)
        }

        return output
    }

    /**
     * Fast CPU box blur for soft glow effect without relying on RenderScript (deprecated in Android 12+).
     */
    private fun fastBoxBlur(src: Bitmap, radius: Int): Bitmap {
        val w = src.width
        val h = src.height
        val pix = IntArray(w * h)
        src.getPixels(pix, 0, w, 0, 0, w, h)

        val r = max(1, min(radius, 10))
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = r + r + 1

        val rArr = IntArray(wh)
        val gArr = IntArray(wh)
        val bArr = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        val vmin = IntArray(max(w, h))

        var divsum = (div + 1) shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        for (idx in 0 until 256 * divsum) {
            dv[idx] = (idx / divsum)
        }

        var yw = 0
        yi = 0

        for (curY in 0 until h) {
            rsum = 0
            gsum = 0
            bsum = 0
            for (curR in -r..r) {
                p = pix[yi + min(wm, max(curR, 0))]
                rsum += (p and 0xff0000) shr 16
                gsum += (p and 0x00ff00) shr 8
                bsum += (p and 0x0000ff)
            }
            for (curX in 0 until w) {
                rArr[yi] = dv[rsum]
                gArr[yi] = dv[gsum]
                bArr[yi] = dv[bsum]

                if (curY == 0) {
                    vmin[curX] = min(curX + r + 1, wm)
                }
                val p1 = pix[yw + vmin[curX]]
                val p2 = pix[yw + max(curX - r, 0)]

                rsum += ((p1 and 0xff0000) - (p2 and 0xff0000)) shr 16
                gsum += ((p1 and 0x00ff00) - (p2 and 0x00ff00)) shr 8
                bsum += (p1 and 0x0000ff) - (p2 and 0x0000ff)
                yi++
            }
            yw += w
        }

        for (curX in 0 until w) {
            rsum = 0
            gsum = 0
            bsum = 0
            yp = -r * w
            for (curR in -r..r) {
                yi = max(0, yp) + curX
                rsum += rArr[yi]
                gsum += gArr[yi]
                bsum += bArr[yi]
                yp += w
            }
            yi = curX
            for (curY in 0 until h) {
                pix[yi] = (0xff000000.toInt()) or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                if (curX == 0) {
                    vmin[curY] = min(curY + r + 1, hm) * w
                }
                val p1 = curX + vmin[curY]
                val p2 = curX + max(curY - r, 0) * w

                rsum += rArr[p1] - rArr[p2]
                gsum += gArr[p1] - gArr[p2]
                bsum += bArr[p1] - bArr[p2]

                yi += w
            }
        }

        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(pix, 0, w, 0, 0, w, h)
        return out
    }
}
