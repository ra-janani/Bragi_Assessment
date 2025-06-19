package com.bragi

import com.bragi.blemiddleware.domain.model.Peripheral
import com.bragi.blemiddleware.domain.usecase.ConnectDeviceUseCase
import com.bragi.blemiddleware.domain.usecase.DiscoverDevicesUseCase
import com.bragi.blemiddleware.domain.usecase.ObserveConnectionStateUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.junit.After

@OptIn(ExperimentalCoroutinesApi::class)
class BleViewModelTest {
    private lateinit var discoverUseCase: DiscoverDevicesUseCase
    private lateinit var connectUseCase: ConnectDeviceUseCase
    private lateinit var stateUseCase: ObserveConnectionStateUseCase
    private lateinit var viewModel: BleViewModel

    @Before
    fun setUp() {
        discoverUseCase = mockk()
        connectUseCase = mockk()
        stateUseCase = mockk()
        // Override RxJava schedulers to trampoline for testing
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }
        // Mock AndroidSchedulers.mainThread() to trampoline
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        viewModel = BleViewModel(discoverUseCase, connectUseCase, stateUseCase)
    }

    @Test
    fun `startScanning emits devices and updates state to Success`() = runTest {
        val devices = listOf(Peripheral("Test", "00:11:22:33:44:55", null))
        every { discoverUseCase.invoke() } returns Flowable.just(devices)
        every { stateUseCase.invoke() } returns null

        viewModel.startScanning()

        assertEquals(devices, viewModel.devices.first())
        assertTrue(viewModel.discoveryState.first() is DiscoveryUiState.Success)
        verify { discoverUseCase.invoke() }
    }

    @Test
    fun `startScanning emits error and updates state to Error`() = runTest {
        val errorMsg = "Scan failed"
        every { discoverUseCase.invoke() } returns Flowable.error(RuntimeException(errorMsg))
        every { stateUseCase.invoke() } returns null

        viewModel.startScanning()

        val state = viewModel.discoveryState.first()
        assertTrue(state is DiscoveryUiState.Error)
        assertTrue(state.message.contains(errorMsg))
        verify { discoverUseCase.invoke() }
    }

    @Test
    fun `connectTo calls connectUseCase`() = runTest {
        val device = Peripheral("Test", "00:11:22:33:44:55", null)
        every { connectUseCase.invoke(device.address) } returns Completable.complete()

        viewModel.connectTo(device)

        verify { connectUseCase.invoke(device.address) }
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}