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
        "display_name" to "$displayName",
        "photo_url" to "${photoURL?.encodedPath}",
        "email" to "$email",
        "dob" to DOB
    )
}