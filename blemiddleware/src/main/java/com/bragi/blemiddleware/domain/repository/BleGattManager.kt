package com.bragi.blemiddleware.domain.repository

import com.bragi.blemiddleware.domain.model.ConnectionState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.UUID

/**
 * Low-level BLE GATT manager interface for direct GATT operations.
 */
interface BleGattManager {
    /**
     * Connects to a BLE device by its address.
     *
     * @param address The MAC address of the BLE device.
     * @return a [Completable] that completes when the connection is established.
     */
    fun connect(address: String): Completable

    /**
     * Disconnects from the currently connected BLE device.
     *
     * @return a [Completable] that completes when the device is disconnected.
     */
    fun disconnect(): Completable

    /**
     * Reads a characteristic from the connected BLE device.
     *
     * @param uuid The UUID of the characteristic to read.
     * @return a [Single] emitting the characteristic value as a [ByteArray].
     */
    fun read(uuid: UUID): Single<ByteArray>

    /**
     * Writes data to a characteristic on the connected BLE device.
     *
     * @param uuid The UUID of the characteristic to write.
     * @param data The data to write.
     * @return a [Completable] that completes when the write is successful.
     */
    fun write(uuid: UUID, data: ByteArray): Completable

    /**
     * Enables notifications for a characteristic on the connected BLE device.
     *
     * @param uuid The UUID of the characteristic to enable notifications for.
     * @return a [Completable] that completes when notifications are enabled.
     */
    fun enableNotifications(uuid: UUID): Completable

    /**
     * Observes the current BLE connection state.
     *
     * @return an [Observable] emitting [ConnectionState] updates, or null if not available.
     */
    fun observeConnectionState(): Observable<ConnectionState>?
}