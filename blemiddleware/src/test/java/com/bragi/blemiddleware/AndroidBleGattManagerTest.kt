package com.bragi.blemiddleware

import com.bragi.blemiddleware.data.repository.AndroidBleGattManager
import com.bragi.blemiddleware.domain.model.ConnectionState
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleConnection.RxBleConnectionState
import com.polidea.rxandroidble3.RxBleDevice
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AndroidBleGattManagerTest {
    private lateinit var rxBleClient: RxBleClient
    private lateinit var rxBleDevice: RxBleDevice
    private lateinit var rxBleConnection: RxBleConnection
    private lateinit var gattManager: AndroidBleGattManager

    private val testAddress = "00:11:22:33:44:55"
    private val testUuid = UUID.randomUUID()
    private val testData = byteArrayOf(1, 2, 3)

    @Before
    fun setUp() {
        rxBleClient = mockk()
        rxBleDevice = mockk()
        rxBleConnection = mockk()
        gattManager = AndroidBleGattManager(rxBleClient)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `connect completes and sets connection`() {
        every { rxBleClient.getBleDevice(testAddress) } returns rxBleDevice
        every { rxBleDevice.establishConnection(any()) } returns Observable.just(rxBleConnection)

        val testObserver = gattManager.connect(testAddress).test()
        testObserver.assertComplete()
    }

    @Test
    fun `disconnect disposes connection and clears state`() {
        val disposable = mockk<io.reactivex.rxjava3.disposables.Disposable>(relaxed = true)
        gattManager.apply {
            javaClass.getDeclaredField("connectionDisposable").apply { isAccessible = true }.set(this, disposable)
            javaClass.getDeclaredField("connection").apply { isAccessible = true }.set(this, rxBleConnection)
        }
        val testObserver = gattManager.disconnect().test()
        testObserver.assertComplete()
        verify { disposable.dispose() }
        // connection and connectionDisposable should be null
        val conn = gattManager.javaClass.getDeclaredField("connection").apply { isAccessible = true }.get(gattManager)
        val connDisp = gattManager.javaClass.getDeclaredField("connectionDisposable").apply { isAccessible = true }.get(gattManager)
        assertNull(conn)
        assertNull(connDisp)
    }

    @Test
    fun `read returns Single from connection`() {
        every { rxBleConnection.readCharacteristic(testUuid) } returns Single.just(testData)
        gattManager.apply {
            javaClass.getDeclaredField("connection").apply { isAccessible = true }.set(this, rxBleConnection)
        }
        val result = gattManager.read(testUuid).test()
        result.assertValue(testData)
    }

    @Test
    fun `read returns error if not connected`() {
        val result = gattManager.read(testUuid).test()
        result.assertError(IllegalStateException::class.java)
    }

    @Test
    fun `write returns Completable from connection`() {
        every { rxBleConnection.writeCharacteristic(testUuid, testData) } returns Single.just(testData)
        gattManager.apply {
            javaClass.getDeclaredField("connection").apply { isAccessible = true }.set(this, rxBleConnection)
        }
        val result = gattManager.write(testUuid, testData).test()
        result.assertComplete()
    }

    @Test
    fun `write returns error if not connected`() {
        val result = gattManager.write(testUuid, testData).test()
        result.assertError(IllegalStateException::class.java)
    }

    @Test
    fun `enableNotifications returns Completable from connection`() {
        val notificationObservable = Observable.just(testData)
        every { rxBleConnection.setupNotification(testUuid) } returns Observable.just(notificationObservable)
        gattManager.apply {
            javaClass.getDeclaredField("connection").apply { isAccessible = true }.set(this, rxBleConnection)
        }
        val result = gattManager.enableNotifications(testUuid).test()
        result.assertComplete()
    }

    @Test
    fun `enableNotifications returns error if not connected`() {
        val result = gattManager.enableNotifications(testUuid).test()
        result.assertError(IllegalStateException::class.java)
    }

    @Test
    fun `observeConnectionState maps RxBleConnectionState to ConnectionState`() {
        val stateChanges = Observable.just(
            RxBleConnectionState.CONNECTED,
            RxBleConnectionState.CONNECTING,
            RxBleConnectionState.DISCONNECTING,
            RxBleConnectionState.DISCONNECTED
        )
        every { rxBleDevice.observeConnectionStateChanges() } returns stateChanges
        gattManager.apply {
            javaClass.getDeclaredField("rxBleDevice").apply { isAccessible = true }.get(this)
            javaClass.getDeclaredField("rxBleDevice").apply { isAccessible = true }.set(this, java.util.concurrent.atomic.AtomicReference(rxBleDevice))
        }
        val result = gattManager.observeConnectionState()!!.test()
        result.assertValues(
            ConnectionState.CONNECTED,
            ConnectionState.CONNECTING,
            ConnectionState.FAILED,
            ConnectionState.DISCONNECTED
        )
    }

    @Test
    fun `observeConnectionState returns null if device is null`() {
        gattManager.apply {
            javaClass.getDeclaredField("rxBleDevice").apply { isAccessible = true }.set(this, java.util.concurrent.atomic.AtomicReference(null))
        }
        val result = gattManager.observeConnectionState()
        assertNull(result)
    }
} 