package com.bragi

import androidx.lifecycle.ViewModel
import com.bragi.blemiddleware.domain.model.Peripheral
import com.bragi.blemiddleware.domain.usecase.ConnectDeviceUseCase
import com.bragi.blemiddleware.domain.usecase.DiscoverDevicesUseCase
import com.bragi.blemiddleware.domain.usecase.ObserveConnectionStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import javax.inject.Inject

sealed class DiscoveryUiState {
    object Idle : DiscoveryUiState()
    object Loading : DiscoveryUiState()
    data class Success(val devices: List<Peripheral>) : DiscoveryUiState()
    data class Error(val message: String) : DiscoveryUiState()
}

@HiltViewModel
class BleViewModel @Inject constructor(
    private val discoverUseCase: DiscoverDevicesUseCase,
    private val connectUseCase: ConnectDeviceUseCase,
    private val stateUseCase: ObserveConnectionStateUseCase
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _devices = MutableStateFlow<List<Peripheral>>(emptyList())
    val devices: StateFlow<List<Peripheral>> = _devices.asStateFlow()

    private val _discoveryState = MutableStateFlow<DiscoveryUiState>(DiscoveryUiState.Idle)
    val discoveryState: StateFlow<DiscoveryUiState> = _discoveryState.asStateFlow()
    private val _connectionState = MutableStateFlow<String>("")
    val connectionState: StateFlow<String> = _connectionState.asStateFlow()
    fun startScanning() {
        _discoveryState.value = DiscoveryUiState.Loading
        discoverUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                _devices.value = list
                _discoveryState.value = DiscoveryUiState.Success(list)
            }, { throwable ->
                _discoveryState.value = DiscoveryUiState.Error(
                    throwable.localizedMessage ?: "Unknown error"
                )
            })
            .let { disposables.add(it) }

        stateUseCase()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { state ->
                _connectionState.value = state.name
            }
            ?.let { disposables.add(it) }
    }

    fun connectTo(device: Peripheral) {
        connectUseCase(device.address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .let { disposables.add(it) }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}