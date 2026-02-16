package com.pasindu.woundcareai.feature.guidance

import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.abs

class BlurDetector @Inject constructor() {

    // Laplacian variance threshold. Below this, image is considered blurry.
    // This value requires tuning for specific camera hardware.
    private val BLUR_THRESHOLD = 50.0

    /**
     * returns true if the image is considered blurry.
     * Uses a simplified Laplacian edge detection kernel.
     */
    fun isBlurry(buffer: ByteBuffer, width: Int, height: Int): Boolean {
        // To keep this fast for real-time preview (30fps), we check a center patch only.
        // And we use a simplified 1D derivative or small kernel step.

        // Validating buffer size
        if (buffer.remaining() < width * height) return false

        val centerX = width / 2
        val centerY = height / 2
        val patchSize = 200 // Analyze 200x200 center patch

        val startX = (centerX - patchSize / 2).coerceAtLeast(1)
        val startY = (centerY - patchSize / 2).coerceAtLeast(1)
        val endX = (centerX + patchSize / 2).coerceAtMost(width - 2)
        val endY = (centerY + patchSize / 2).coerceAtMost(height - 2)

        var varianceSum = 0.0
        var count = 0

        // Accessing byte buffer randomly is slow, so we map indices carefully.
        // Y buffer stride is usually 'width'.

        for (y in startY until endY step 2) {
            for (x in startX until endX step 2) {
                val idx = y * width + x

                // 3x3 Laplacian Kernel (Simplified)
                //  0  1  0
                //  1 -4  1
                //  0  1  0

                val pCenter = (buffer.get(idx).toInt() and 0xFF)
                val pLeft = (buffer.get(idx - 1).toInt() and 0xFF)
                val pRight = (buffer.get(idx + 1).toInt() and 0xFF)
                val pUp = (buffer.get(idx - width).toInt() and 0xFF)
                val pDown = (buffer.get(idx + width).toInt() and 0xFF)

                val laplacian = pLeft + pRight + pUp + pDown - (4 * pCenter)
                varianceSum += (laplacian * laplacian)
                count++
            }
        }

        val variance = if (count > 0) varianceSum / count else 0.0
        return variance < BLUR_THRESHOLD
    }
}