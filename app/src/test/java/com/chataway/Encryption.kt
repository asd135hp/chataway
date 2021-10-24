package com.chataway

import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Encryption {
    private var isPrivateKeySet = false
    private var privateKey: SecretKeySpec? = null
    private var iv: ByteArray = ByteArray(16)

    private val charset = Charset.forName("UTF-8")

    private const val ALGORITHM_TRANSFORMATION = "AES/CBC/PKCS5Padding"

    fun initializeParameters(uid: String, targetUID: String){
        // only both sides could see the message
        if(!isPrivateKeySet){
            val key = (uid + targetUID).toByteArray(charset).run {
                val sha = MessageDigest.getInstance("SHA-256")
                val digest = Base64.getEncoder().encode(sha.digest(this))
                iv = digest.copyOfRange(0, 16)
                digest.copyOfRange(12, 44)
            }
            privateKey = SecretKeySpec(key, "AES")
        }
    }

    fun encrypt(originalString: String): String? {
        return try {
            if(privateKey == null) return null

            val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, privateKey)
            }

            Base64.getEncoder().encodeToString(
                cipher.doFinal(originalString.toByteArray(charset))
            )
        } catch(e: Exception){
            print("Cause: ${e.cause}\n" +
                    "Message: ${e.message}\n" +
                    "Stack trace: ${e.stackTrace.joinToString("\n")}")
            null
        }
    }

    fun decrypt(encryptedString: String): ByteArray? {
        return try {
            if(privateKey == null) return null

            val ivParamSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, privateKey, ivParamSpec)
            }

            print("$iv ${encryptedString.length}")
            cipher.doFinal(Base64.getDecoder().decode(encryptedString))
        } catch(e: Exception){
            print("Cause: ${e.cause}\n" +
                        "Message: ${e.message}\n" +
                        "Stack trace: ${e.stackTrace.joinToString("\n")}")
            null
        }
    }
}