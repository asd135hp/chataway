package com.chataway

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.chataway.data.firebase.UserOperations
import com.chataway.databinding.ActivityLoggedInBinding
import com.chataway.distinction.FriendListFragment
import com.chataway.ui.main.FriendBoxFragment
import com.chataway.ui.utility.afterTextChanged
import com.google.firebase.auth.FirebaseUser

const val NEW_FRIEND_UID_INTENT_KEY = "new_friend_uid"

class LoggedInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoggedInBinding

    private var user: FirebaseUser? = null

    private val newActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                val newFriendUID = result.data?.getStringExtra(NEW_FRIEND_UID_INTENT_KEY)
                user = result.data?.getParcelableExtra(FIREBASE_USER_INTENT_KEY)
                if(user == null){
                    if(newFriendUID == null)
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

        user = intent.getParcelableExtra(FIREBASE_USER_INTENT_KEY)
        if(user == null){
            // send back to main activity because the data is not there to process further
            Toast.makeText(
                this,
                "No user data found. Please try again!",
                Toast.LENGTH_LONG
            ).show()
            returnToMainActivityWithFault()
        }

        // set user to the database
        UserOperations.addUser(user!!) { err, _ ->
            if(err != null){
                Log.i(TAG, "Could not set user up on Firebase")
                returnToMainActivityWithFault()
            }
        }

        val userPicture = binding.checkOutProfile
        val searchIcon = binding.searchIcon
        val searchBar = binding.searchBar
        val friendListFragment = binding.friendList.getFragment<FriendBoxFragment>()

        userPicture.setOnClickListener { settleToProfileViewerActivity() }

        // search bar
        searchIcon.setOnClickListener { searchBar.requestFocus() }
        searchBar.afterTextChanged { setFragment(friendListFragment, it) }

        // initialization of the fragment
        setFragment(friendListFragment, "")

        // brings user to new activity
        binding.addNewFriend.setOnClickListener {
            newActivity.launch(Intent(this, NewFriendActivity::class.java).apply{
                putExtra(FIREBASE_USER_INTENT_KEY, user)
            })
        }
    }

    private fun settleToProfileViewerActivity(){
        newActivity.launch(Intent(this, ProfileViewerActivity::class.java).apply{
            putExtra(FIREBASE_USER_INTENT_KEY, user)
        })
    }

    private fun returnToMainActivityWithFault() {
        // return to the main activity because this activity connects to the main activity
        setResult(Activity.RESULT_CANCELED)
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    private fun setFragment(fragment: FriendBoxFragment?, nameFilter: String){
        if(fragment != null && !fragment.isAdded)
            fragment.parentFragmentManager.beginTransaction().apply {
                replace(R.id.friend_list, FriendBoxFragment.newInstance(user!!.uid, nameFilter))
                commit()
            }
    }
}