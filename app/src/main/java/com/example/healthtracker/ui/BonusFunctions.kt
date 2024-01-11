package com.example.healthtracker.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.util.Base64
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.healthtracker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

fun navigateToActivity(currentActivity: Activity, targetActivityClass: Class<*>) {
    val intent = Intent(currentActivity, targetActivityClass)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    currentActivity.startActivity(intent)
    currentActivity.finish()
}
suspend fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bitmap
    }
}
fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
fun base64ToBitmap(base64String: String): Bitmap {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}
suspend fun saveBitmapToDatabase(bitmap: Bitmap) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val database: DatabaseReference = Firebase.database.getReference("user/${currentUser!!.uid}/userInfo/image")
    val base64String = bitmapToBase64(bitmap)
    withContext(Dispatchers.IO) {
        database.setValue(base64String).await()
    }
}
fun FragmentActivity.hideBottomNav() {
    findViewById<BottomNavigationView>(R.id.nav_view).apply {
        visibility = View.GONE
    }
}

fun FragmentActivity.showBottomNav() {
    findViewById<BottomNavigationView>(R.id.nav_view).apply {
        visibility = View.VISIBLE
    }
}
fun cropImageToSquare(context: Context, uri: Uri): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(stream, null, options)
            val imageWidth = options.outWidth
            val imageHeight = options.outHeight

            val scaleFactor = if (imageWidth >= imageHeight) {
                imageWidth.toFloat() / imageHeight.toFloat()
            } else {
                imageHeight.toFloat() / imageWidth.toFloat()
            }

            val squareSize = if (imageWidth >= imageHeight) {
                imageHeight
            } else {
                imageWidth
            }

            val left = if (imageWidth >= imageHeight) 0 else (imageWidth - squareSize) / 2
            val top = if (imageHeight >= imageWidth) 0 else (imageHeight - squareSize) / 2

            val squareBitmap = Bitmap.createBitmap(
                squareSize,
                squareSize,
                Bitmap.Config.ARGB_8888
            )

            val paint = Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
                isDither = true
            }

            Canvas(squareBitmap).apply {
                drawBitmap(
                    squareBitmap,
                    Rect(left, top, left + squareSize, top + squareSize),
                    Rect(0, 0, squareSize, squareSize),
                    paint
                )
            }

            bitmap = Bitmap.createScaledBitmap(squareBitmap, 1, 1, true)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bitmap
}
