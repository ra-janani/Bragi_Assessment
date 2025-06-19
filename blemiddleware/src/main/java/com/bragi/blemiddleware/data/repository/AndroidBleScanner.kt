package com.bragi.blemiddleware.data.repository

import com.bragi.blemiddleware.domain.model.Peripheral
import com.bragi.blemiddleware.domain.repository.BleScanner
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidBleScanner @Inject constructor(
    private val rxBleClient: RxBleClient
) : BleScanner {

    override fun startScan(): Flowable<List<Peripheral>> {
        return rxBleClient
            .scanBleDevices(
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build()
            )
            .map { scanResult ->
                Peripheral(
                    name = scanResult.bleDevice.name,
                    address = scanResult.bleDevice.macAddress,
                    manufacturerData = scanResult.scanRecord?.getManufacturerSpecificData(0)
                )
            }
            .toList()
            .toFlowable()
    }
}