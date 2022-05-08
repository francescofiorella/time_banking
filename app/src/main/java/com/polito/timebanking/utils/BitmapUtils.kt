package com.polito.timebanking.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

fun rotateBitmap(context: Context, bitmap: Bitmap, pathname: String): Bitmap {
    try {
        val fileInputStream = context.openFileInput(pathname)
        val ei = ExifInterface(fileInputStream)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    } catch (e: Exception) {
        when (e) {
            is FileNotFoundException, is IOException -> {
                e.printStackTrace()
                return bitmap
            }
            else -> throw e
        }
    }
}

private fun rotate(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height,
        matrix, true
    )
}

fun loadBitmapFromStorage(context: Context, path: String): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        val fileInputStream = context.openFileInput(path)
        bitmap = BitmapFactory.decodeStream(fileInputStream)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return bitmap
}

fun saveBitmapToStorage(context: Context, bitmap: Bitmap, path: String) {
    try {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        context.openFileOutput(path, Context.MODE_PRIVATE).use {
            it.write(stream.toByteArray())
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}