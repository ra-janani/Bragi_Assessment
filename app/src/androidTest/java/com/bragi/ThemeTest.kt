package com.bragi.ui.theme

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ThemeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theme_ComposesWithoutCrash() {
        composeTestRule.setContent {
            BragiAssessmentTheme {
                // Empty content
            }
        }
    }
} 