package com.bragi

import android.app.Application
import android.os.Build
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Test
    fun launchesSuccessfully() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.use {
            assertNotNull(it)
        }
    }
} 