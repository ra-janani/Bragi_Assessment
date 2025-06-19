package com.bragi.blemiddleware

import com.bragi.blemiddleware.data.repository.AndroidBleScanner
import com.bragi.blemiddleware.domain.model.Peripheral
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanResult
import com.polidea.rxandroidble3.scan.ScanSettings
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.reactivex.rxjava3.core.Observable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AndroidBleScannerTest {
    private lateinit var rxBleClient: RxBleClient
    private lateinit var scanner: AndroidBleScanner

    @Before
    fun setUp() {
        rxBleClient = mockk()
        scanner = AndroidBleScanner(rxBleClient)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `startScan emits list of Peripherals`() {
        val device1 = mockk<RxBleDevice> {
            every { name } returns "Device1"
            every { macAddress } returns "AA:BB:CC:DD:EE:01"
        }
        val device2 = mockk<RxBleDevice> {
            every { name } returns "Device2"
            every { macAddress } returns "AA:BB:CC:DD:EE:02"
        }
        val scanRecord1 = mockk<com.polidea.rxandroidble3.scan.ScanRecord> {
            every { getManufacturerSpecificData(0) } returns byteArrayOf(1, 2, 3)
        }
        val scanRecord2 = mockk<com.polidea.rxandroidble3.scan.ScanRecord> {
            every { getManufacturerSpecificData(0) } returns null
        }
        val scanResult1 = mockk<ScanResult> {
            every { bleDevice } returns device1
            every { scanRecord } returns scanRecord1
        }
        val scanResult2 = mockk<ScanResult> {
            every { bleDevice } returns device2
            every { scanRecord } returns scanRecord2
        }
        every {
            rxBleClient.scanBleDevices(any<ScanSettings>())
        } returns Observable.fromArray(scanResult1, scanResult2)

        val result = scanner.startScan().test()
        result.assertValue { list ->
            list.size == 2 &&
            list[0] == Peripheral("Device1", "AA:BB:CC:DD:EE:01", byteArrayOf(1, 2, 3)) &&
            list[1] == Peripheral("Device2", "AA:BB:CC:DD:EE:02", null)
        }
    }
} 