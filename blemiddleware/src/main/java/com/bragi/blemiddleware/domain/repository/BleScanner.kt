package com.bragi.blemiddleware.domain.repository

import com.bragi.blemiddleware.domain.model.Peripheral
import io.reactivex.rxjava3.core.Flowable

/**
 * BLE scanner interface for discovering nearby BLE devices.
 */
interface BleScanner {
    /**
     * Starts scanning for BLE peripherals.
     *
     * @return a [Flowable] emitting lists of discovered [Peripheral]s.
     */
    fun startScan(): Flowable<List<Peripheral>>
}