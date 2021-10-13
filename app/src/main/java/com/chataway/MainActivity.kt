package com.chataway

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.security.crypto.MasterKey
import com.chataway.data.Encryption
import com.chataway.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.security.KeyPairGenerator

const val FIREBASE_USER_INTENT_KEY = "firebase_user"
const val KEYSTORE_ALIAS = "chataway_master_key"
const val ANDROID_KEY_STORE = "AndroidKeyStore"

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
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val guestLoginButton = binding.mainContinueAsGuestButton
        val loginButton = binding.mainLoginButton

        guestLoginButton.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signInAnonymously().addOnCompleteListener { result ->
                if(result.isSuccessful && auth.currentUser != null){
                    newActivity.launch(Intent(this, LoggedInActivity::class.java).apply{
                        putExtra(FIREBASE_USER_INTENT_KEY, auth.currentUser)
                    })
                }

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

        Encryption.setPrivateKey(
            MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        )
    }
}