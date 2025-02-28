package at.marcneureiter.kotlin.coroutines.modeling.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import at.marcneureiter.kotlin.coroutines.modeling.CoroutinesModellingApplication

fun readBitmapFromUri(uri: Uri): Bitmap =
    CoroutinesModellingApplication.INSTANCE.contentResolver.openInputStream(uri)!!.use { input ->
        BitmapFactory.decodeStream(input)
    }