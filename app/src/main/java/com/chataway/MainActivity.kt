package com.chataway

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.chataway.data.Encryption
import com.chataway.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

const val FIREBASE_USER_INTENT_KEY = "firebase_user"
const val TAG = "chataway"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
                } else settleToLoggedInActivity(user)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val guestLoginButton = binding.mainContinueAsGuestButton
        val loginButton = binding.mainLoginButton
        val registerButton = binding.mainRegisterButton

        val auth = FirebaseAuth.getInstance()

        guestLoginButton.setOnClickListener {
            auth.signInAnonymously().addOnSuccessListener {
                Log.i(TAG, "In complete listener for Firebase Auth")
                if(auth.currentUser != null)
                    settleToLoggedInActivity(auth.currentUser)
            }.addOnFailureListener {
                Log.i(TAG, "Could not sign in")
                Log.e(TAG, "Cause: ${it.cause}\n" +
                        "Message: ${it.message}\n" +
                        "Stack trace: ${it.stackTrace.joinToString("\n")}")

                Toast.makeText(
                    this,
                    "Could not sign in as guest this time. Please try again!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        loginButton.setOnClickListener {
            newActivity.launch(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            newActivity.launch(Intent(this, RegisterActivity::class.java))
        }

        Encryption.generatePrivateKey(this)
    }

    private fun settleToLoggedInActivity(user: FirebaseUser?){
        newActivity.launch(Intent(this, LoggedInActivity::class.java).apply{
            putExtra(FIREBASE_USER_INTENT_KEY, user)
        })
    }
}