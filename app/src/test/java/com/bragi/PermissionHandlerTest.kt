package com.bragi.core

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PermissionHandlerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = org.mockito.Mockito.mock(Context::class.java)
        mockkStatic(ContextCompat::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `returns true when all permissions are granted`() {
        every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_GRANTED
        assertTrue(hasRequiredPermissions(context))
    }

    @Test
    fun `returns false when any permission is denied`() {
        val permissionsCount = Class.forName("com.bragi.core.PermissionHandlerKt")
            .getDeclaredField("REQUIRED_PERMISSIONS")
            .apply { isAccessible = true }
            .get(null)
            .let { it as Array<*> }
            .size
        val results = MutableList(permissionsCount - 1) { PackageManager.PERMISSION_GRANTED } +
                PackageManager.PERMISSION_DENIED
        every { ContextCompat.checkSelfPermission(any(), any()) } returnsMany results
        assertFalse(hasRequiredPermissions(context))
    }
} 