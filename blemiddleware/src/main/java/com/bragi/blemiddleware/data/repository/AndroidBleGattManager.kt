package com.bragi.blemiddleware.data.repository

import com.bragi.blemiddleware.domain.model.ConnectionState
import com.bragi.blemiddleware.domain.repository.BleGattManager
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleConnection.RxBleConnectionState
import com.polidea.rxandroidble3.RxBleDevice
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidBleGattManager @Inject constructor(
    private val rxBleClient: RxBleClient
) : BleGattManager {

    private var connection: RxBleConnection? = null
    private var connectionDisposable: Disposable? = null
    private val rxBleDevice = AtomicReference<RxBleDevice?>(null)

    override fun connect(address: String): Completable {
        return Completable.create { emitter ->
            connectionDisposable = rxBleClient
                .getBleDevice(address)
                .establishConnection(false)
                .doOnNext { conn ->
                    connection = conn
                    if (!emitter.isDisposed) {
                        rxBleDevice.set(rxBleClient.getBleDevice(address))
                        emitter.onComplete()
                    }
                }
                .doOnError { err ->
                    if (!emitter.isDisposed) {
                        emitter.onError(err)
                    }
                }
                .subscribe()
        }
    }

    override fun disconnect(): Completable {
        return Completable.fromAction {
            connectionDisposable?.dispose()
            connectionDisposable = null
            connection = null
        }
    }

    override fun read(uuid: UUID): Single<ByteArray> {
        return connection
            ?.readCharacteristic(uuid)
            ?: Single.error(IllegalStateException("Not connected"))
    }

    override fun write(uuid: UUID, data: ByteArray): Completable {
        return connection
            ?.writeCharacteristic(uuid, data)
            ?.ignoreElement()
            ?: Completable.error(IllegalStateException("Not connected"))
    }

    override fun enableNotifications(uuid: UUID): Completable {
        return connection
            ?.setupNotification(uuid)
            ?.flatMapCompletable { it.ignoreElements() }
            ?: Completable.error(IllegalStateException("Not connected"))
    }

    override fun observeConnectionState(): Observable<ConnectionState>? {
        val device = rxBleDevice.get()

        return device
            ?.observeConnectionStateChanges()
            ?.map { state ->
                when (state) {
                    RxBleConnectionState.CONNECTED -> ConnectionState.CONNECTED
                    RxBleConnectionState.CONNECTING -> ConnectionState.CONNECTING
                    RxBleConnectionState.DISCONNECTING -> ConnectionState.FAILED
                    RxBleConnectionState.DISCONNECTED -> ConnectionState.DISCONNECTED
                }
            }
    }
}