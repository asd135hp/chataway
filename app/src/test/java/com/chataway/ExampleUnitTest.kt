package com.chataway

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    @Test
    fun testEncryption(){
        Encryption.initializeParameters("abc", "xyz")
        val testString = "Hello!"
        val encryptedString = Encryption.encrypt(testString)
        assertNotEquals(testString, encryptedString)
        assertNotEquals(null, encryptedString.toString())
        assertEquals(testString, Encryption.decrypt(encryptedString!!))
    }
}