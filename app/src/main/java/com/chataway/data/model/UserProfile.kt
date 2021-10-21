package com.chataway.data.model

import android.net.Uri

data class UserProfile(
    val displayName: String?,
    val photoURL: Uri?,
    val email: String?,
    val DOB: String = ""
) {
    fun toMap(): Map<String, String>
    = mapOf(
        FirebaseConstants.USER_DISPLAY_NAME to "$displayName",
        FirebaseConstants.USER_PHOTO_URL to "${photoURL?.encodedPath}",
        FirebaseConstants.USER_EMAIL to "$email",
        FirebaseConstants.USER_DOB to DOB
    )
}