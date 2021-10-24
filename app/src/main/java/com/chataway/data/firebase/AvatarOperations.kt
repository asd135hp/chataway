package com.chataway.data.firebase

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.chataway.TAG
import java.io.ByteArrayOutputStream
import java.io.File

object AvatarOperations {
    const val IMG_EXT = "jpg"

    fun uploadAvatar(uid: String, avatarView: Drawable) {
        val bitmap = avatarView.toBitmap()
        val compressedRaw = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, compressedRaw)
        Operations.getAvatarDatabase()
            .child("$uid.$IMG_EXT")
            .putBytes(compressedRaw.toByteArray())
            .addOnFailureListener {
                Log.e(TAG, "Cause: ${it.cause}\n" +
                        "Message: ${it.message}\n" +
                        "Stack trace: ${it.stackTrace.joinToString("\n")}")
            }
            .addOnSuccessListener {
                Log.i(TAG, "${it.metadata}")
            }
    }

    // can also be accessed offline through uid.IMG_EXT
    fun fetchAvatar(uid: String, avatarView: ImageView) {
        if(uid.isEmpty()) return

        val localFile = File.createTempFile(uid, IMG_EXT)
        Operations.getAvatarDatabase()
            .getFile(localFile)
            .addOnFailureListener {
                Log.e(TAG, "Cause: ${it.cause}\n" +
                        "Message: ${it.message}\n" +
                        "Stack trace: ${it.stackTrace.joinToString("\n")}")
            }
            .addOnSuccessListener {
                Log.i(TAG, "${it.bytesTransferred}")
                avatarView.setImageDrawable(Drawable.createFromPath(localFile.path))
            }
    }
}