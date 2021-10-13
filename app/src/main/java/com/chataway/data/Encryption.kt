package com.chataway.data

import android.util.Log
import androidx.security.crypto.MasterKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Encryption {
    private var privateKey: String? = null
    private val base64Encoder = Base64.getEncoder()
    private val base64Decoder = Base64.getDecoder()
    private const val algorithm = "AES"
    private const val algoTransformation = "AES/GCM/NoPadding"
    private const val delimiter = ","

    fun setPrivateKey(key: MasterKey){
        privateKey = key.toString()
        Log.i("Private key", privateKey!!)
    }

    fun encryptChatLog(uid: String, chatLog: List<String>): String? {
        return try {
            if(privateKey == null) return null

            val encodedChatLog = chatLog.joinToString(delimiter) {
                base64Encoder.encodeToString(it.toByteArray())
            }

            val keySpec = SecretKeySpec(privateKey!!.toByteArray(), algorithm)
            val ivParamSpec = IvParameterSpec(uid.toByteArray())
            val cipher = Cipher.getInstance(algoTransformation)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec)

            val encryptedValue = cipher.doFinal(encodedChatLog.toByteArray())
            base64Encoder.encodeToString(encryptedValue)
        } catch(e: Exception){
            null
        }

    }

    fun decryptChatLog(uid: String, encryptedChatLog: String): List<String>? {
        return try {
            if(privateKey == null) return null

            val keySpec = SecretKeySpec(privateKey!!.toByteArray(), algorithm)
            val ivParamSpec = IvParameterSpec(uid.toByteArray())
            val cipher = Cipher.getInstance(algoTransformation)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec)

            val decryptedValue = cipher.doFinal(base64Decoder.decode(encryptedChatLog))
            String(base64Decoder.decode(decryptedValue)).split(delimiter).map {
                String(base64Decoder.decode(it))
            }
        } catch(e: Exception){
            null
        }
    }
}