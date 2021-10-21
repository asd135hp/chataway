package com.chataway.data

import android.content.Context
import android.util.Log
import androidx.security.crypto.MasterKey
import com.chataway.TAG
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

object Encryption {
    private var isPrivateKeySet = false
    private var privateKey: Key? = null

    private const val IV_FILE = "local_iv.txt"
    private const val GCM_LENGTH = 128
    private const val ALGORITHM_TRANSFORMATION = "AES/GCM/NoPadding"

    fun generatePrivateKey(context: Context){
        if(isPrivateKeySet) return

        val ks = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        // just in case the master key is not built yet
        if(!ks.containsAlias(MasterKey.DEFAULT_MASTER_KEY_ALIAS)){
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }

        // contains alias, no need to generate key anymore
        val entry = ks.getEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS, null)
        if(entry !is KeyStore.SecretKeyEntry){
            Log.w("private key warning", "not an instance of a PrivateKeyEntry")
            return
        }

        isPrivateKeySet = true
        privateKey = entry.secretKey
    }

    fun encryptString(context: Context, byte: ByteArray): String? {
        return try {
            if(privateKey == null) return null

            val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, privateKey)
            }

            val iv = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java).iv
            context.openFileOutput(IV_FILE, Context.MODE_PRIVATE).apply {
                write(iv)
                close()
            }

            cipher.doFinal(byte)?.toString()
        } catch(e: Exception){
            Log.e(TAG, "Cause: ${e.cause}\n" +
                    "Message: ${e.message}\n" +
                    "Stack trace: ${e.stackTrace.joinToString("\n")}")
            null
        }
    }

    fun decryptString(context: Context, encryptedString: String): ByteArray? {
        return try {
            if(privateKey == null) return null

            val ivParamSpec = context.openFileInput(IV_FILE).run {
                val iv = readBytes()
                close()
                GCMParameterSpec(GCM_LENGTH, iv)
            }

            val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, privateKey, ivParamSpec)
            }

            Log.i(TAG, "${encryptedString.toByteArray().size} ${encryptedString.length}")
            cipher.doFinal(encryptedString.toByteArray())
        } catch(e: Exception){
            Log.e(
                TAG, "Cause: ${e.cause}\n" +
                    "Message: ${e.message}\n" +
                    "Stack trace: ${e.stackTrace.joinToString("\n")}")
            null
        }
    }
}