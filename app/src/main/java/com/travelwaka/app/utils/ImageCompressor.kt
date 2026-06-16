package com.travelwaka.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageCompressor {
    fun compressImage(context: Context, uri: Uri, maxBytes: Int = 1024 * 1024): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // Calculate sample size to scale down if image is extremely large
            var sampleSize = 1
            val maxDimension = 2048
            if (options.outWidth > maxDimension || options.outHeight > maxDimension) {
                val longestSide = maxOf(options.outWidth, options.outHeight)
                sampleSize = Math.round(longestSide.toFloat() / maxDimension.toFloat())
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val decodeInputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(decodeInputStream, null, decodeOptions)
            decodeInputStream.close()

            if (bitmap == null) return null

            var quality = 85
            var byteArray: ByteArray
            do {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                byteArray = outputStream.toByteArray()
                quality -= 10
            } while (byteArray.size > maxBytes && quality > 10)

            bitmap.recycle()
            byteArray
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
