package com.chataway

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import com.chataway.data.ChatLog

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule val activityScenarioRule = ActivityScenarioRule<MainActivity>(Intent())

    @Test
    fun test_encryption_and_decryption() {
        activityScenarioRule.scenario.onActivity {
            val chatLog = ChatLog(it, "abc").apply {
                addNewChatMessage("Hello!")
            }

            val encryptedChatLog = chatLog.encryptChatLog()
            assertNotEquals(encryptedChatLog, null)

            chatLog.decryptChatLog(encryptedChatLog!!)
            assertEquals(chatLog.chatMessages[0], "Hello!")
        }
    }
}