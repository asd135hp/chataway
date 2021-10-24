package com.chataway.data.model

import com.chataway.data.FirebaseConstants

data class UserProfile(
    val displayName: String?,
    val email: String?,
    val DOB: String = ""
) {
    fun toMap(): Map<String, String> = mapOf(
        FirebaseConstants.USER_DISPLAY_NAME to "$displayName",
        FirebaseConstants.USER_EMAIL to "$email",
        FirebaseConstants.USER_DOB to DOB
    )
}