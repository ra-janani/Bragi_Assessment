package com.bragi.blemiddleware

import com.bragi.blemiddleware.data.repository.BleRepositoryImpl
import com.bragi.blemiddleware.domain.model.ConnectionState
import com.bragi.blemiddleware.domain.model.Peripheral
import com.bragi.blemiddleware.domain.repository.BleGattManager
import com.bragi.blemiddleware.domain.repository.BleScanner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test
import java.util.UUID

class BleRepositoryImplTest {
    private lateinit var scanner: BleScanner
    private lateinit var gattManager: BleGattManager
    private lateinit var repository: BleRepositoryImpl

    @Before
    fun setUp() {
        scanner = mockk()
        gattManager = mockk()
        repository = BleRepositoryImpl(scanner, gattManager)
    }

    @Test
    fun `scanForPeripherals delegates to scanner`() {
        val peripherals = listOf(Peripheral("Test", "00:11:22:33:44:55", null))
        every { scanner.startScan() } returns Flowable.just(peripherals)
        val testObserver = repository.scanForPeripherals().test()
        testObserver.assertValue(peripherals)
        verify { scanner.startScan() }
    }

    @Test
    fun `connect delegates to gattManager`() {
        val address = "00:11:22:33:44:55"
        every { gattManager.connect(address) } returns Completable.complete()
        val testObserver = repository.connect(address).test()
        testObserver.assertComplete()
        verify { gattManager.connect(address) }
    }

    @Test
    fun `observeConnectionState delegates to gattManager`() {
        val observable = Observable.just(ConnectionState.CONNECTED)
        every { gattManager.observeConnectionState() } returns observable
        val result = repository.observeConnectionState()
        assert(result === observable)
        verify { gattManager.observeConnectionState() }
    }

    @Test
    fun `readCharacteristic delegates to gattManager`() {
        val uuid = UUID.randomUUID()
        val data = byteArrayOf(1, 2, 3)
        every { gattManager.read(uuid) } returns Single.just(data)
        val testObserver = repository.readCharacteristic(uuid).test()
        testObserver.assertValue(data)
        verify { gattManager.read(uuid) }
    }

    @Test
    fun `writeCharacteristic delegates to gattManager`() {
        val uuid = UUID.randomUUID()
        val data = byteArrayOf(1, 2, 3)
        every { gattManager.write(uuid, data) } returns Completable.complete()
        val testObserver = repository.writeCharacteristic(uuid, data).test()
        testObserver.assertComplete()
        verify { gattManager.write(uuid, data) }
    }

    @Test
    fun `enableNotifications delegates to gattManager`() {
        val uuid = UUID.randomUUID()
        every { gattManager.enableNotifications(uuid) } returns Completable.complete()
        val testObserver = repository.enableNotifications(uuid).test()
        testObserver.assertComplete()
        verify { gattManager.enableNotifications(uuid) }
    }

    @Test
    fun `disconnect delegates to gattManager`() {
        every { gattManager.disconnect() } returns Completable.complete()
        val testObserver = repository.disconnect().test()
        testObserver.assertComplete()
        verify { gattManager.disconnect() }
    }
} 