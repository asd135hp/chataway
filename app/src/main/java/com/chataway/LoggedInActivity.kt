package com.chataway

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView
import com.chataway.data.FirebaseCustomOperations
import com.chataway.data.model.FirebaseConstants
import com.chataway.databinding.ActivityLoggedInBinding
import com.chataway.ui.login.afterTextChanged
import com.google.firebase.auth.FirebaseUser

class LoggedInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoggedInBinding
    private var user: FirebaseUser? = null

    private val newActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                user = result.data?.getParcelableExtra(FIREBASE_USER_INTENT_KEY)
                if(user == null){
                    Toast.makeText(
                        this,
                        "An unexpected error happened. Please try again!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra<FirebaseUser>(FIREBASE_USER_INTENT_KEY)
        if(user == null){
            // send back to main activity because the data is not there to process further
            Toast.makeText(
                this,
                "No user data found. Please try again!",
                Toast.LENGTH_LONG
            ).show()
            setResult(Activity.RESULT_CANCELED)
        }

        val userPicture = binding.messageUserPicture
        val searchIcon = binding.searchIcon
        val searchBar = binding.searchBar
        val friendListContainer = binding.friendList

        // set user to the database
        FirebaseCustomOperations.addUser(user!!) { err, _ ->
            if(err != null){
                Log.i(TAG, "Could not set user up on Firebase")
                setResult(Activity.RESULT_CANCELED)
            }
        }

        userPicture.setOnClickListener { settleToProfileViewerActivity() }

        // search bar
        searchIcon.setOnClickListener { searchBar.requestFocus() }
        searchBar.afterTextChanged { fetchUserList(friendListContainer, it) }

        fetchUserList(friendListContainer)
    }

    private fun settleToProfileViewerActivity(){
        newActivity.launch(Intent(this, ProfileViewerActivity::class.java).apply{
            putExtra(FIREBASE_USER_INTENT_KEY, user)
        })
    }

    private fun fetchUserList(container: FragmentContainerView, nameFilter: String = ""){
        val fragmentRecyclerView = container.rootView as RecyclerView
        FirebaseCustomOperations.iterateThroughAllUsers { dataSnapshot ->
            dataSnapshot.children.forEach { userSnapshot ->
                val name = userSnapshot
                    .child(FirebaseConstants.USER_DISPLAY_NAME)
                    .getValue(String::class.java)

                // breaks immediately if username does not match
                if(name != null && !name.contains(nameFilter)) return@forEach

                val lastMessage = "" // wait for the encryption scheme to be stabilized
                val photoUrl = userSnapshot
                    .child(FirebaseConstants.USER_PHOTO_URL)
                    .getValue(String::class.java)

            }
        }
    }
}