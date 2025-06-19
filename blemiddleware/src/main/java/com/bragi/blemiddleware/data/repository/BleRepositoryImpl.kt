package com.bragi.blemiddleware.data.repository

import com.bragi.blemiddleware.domain.repository.BleGattManager
import com.bragi.blemiddleware.domain.repository.BleRepository
import com.bragi.blemiddleware.domain.repository.BleScanner
import java.util.UUID
import javax.inject.Inject

class BleRepositoryImpl @Inject constructor(
    private val scanner: BleScanner,
    private val gattManager: BleGattManager
) : BleRepository {

    override fun scanForPeripherals() =
        scanner.startScan() // returns Flowable<List<Peripheral>>

    override fun connect(address: String) =
        gattManager.connect(address) // returns Completable

    override fun observeConnectionState() =
        gattManager.observeConnectionState()

    override fun readCharacteristic(uuid: UUID) =
        gattManager.read(uuid)

    override fun writeCharacteristic(uuid: UUID, data: ByteArray) =
        gattManager.write(uuid, data)

    override fun enableNotifications(uuid: UUID) =
        gattManager.enableNotifications(uuid)

    override fun disconnect() =
        gattManager.disconnect()
}