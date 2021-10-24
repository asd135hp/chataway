package com.chataway.data.firebase

import com.chataway.data.model.UserProfile
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

object UserOperations {
    // add new user to the list of registered user for this app
    fun addUser(user: FirebaseUser, completionHandler: DatabaseReference.CompletionListener?)
        = Operations.getUserDatabase(user.uid).setValue(
            UserProfile(user.displayName, user.email).toMap(),
            completionHandler
        )

    // edit the profile of the existing user
    fun editUser(userUID: String,
                 newProfile: UserProfile,
                 completionHandler: DatabaseReference.CompletionListener?)
        = Operations.getUserDatabase(userUID)
            .updateChildren(newProfile.toMap(), completionHandler)

    // get all user data from the database
    fun getUser(userUID: String, onTaskCompleteHandler: OnCompleteListener<DataSnapshot>)
        = Operations.getUserDatabase(userUID).get().addOnCompleteListener(onTaskCompleteHandler)

    // delete user when the session is finished (guest)
    fun deleteUser(userUID: String, completionHandler: DatabaseReference.CompletionListener?)
        = Operations.getUserDatabase(userUID).removeValue(completionHandler)
}