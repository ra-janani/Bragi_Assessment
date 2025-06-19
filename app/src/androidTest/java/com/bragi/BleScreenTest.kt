package com.bragi

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class BleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bleScreen_ComposesWithoutCrash() {
        composeTestRule.setContent {
            BleScreen()
        }
        // You can add more assertions here for UI state, etc.
    }
} 