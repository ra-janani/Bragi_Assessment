package com.bragi.blemiddleware

import com.bragi.blemiddleware.domain.repository.BleRepository
import com.bragi.blemiddleware.domain.usecase.ConnectDeviceUseCase
import com.bragi.blemiddleware.domain.usecase.DiscoverDevicesUseCase
import com.bragi.blemiddleware.domain.usecase.ObserveConnectionStateUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import org.junit.Before
import org.junit.Test
import java.util.UUID

class UseCaseTest {
    private lateinit var repo: BleRepository

    @Before
    fun setUp() {
        repo = mockk(relaxed = true)
    }

    @Test
    fun `DiscoverDevicesUseCase delegates to repo`() {
        val useCase = DiscoverDevicesUseCase(repo)
        val flow = Flowable.just(emptyList<com.bragi.blemiddleware.domain.model.Peripheral>())
        every { repo.scanForPeripherals() } returns flow
        val result = useCase.invoke()
        assert(result === flow)
        verify { repo.scanForPeripherals() }
    }

    @Test
    fun `ConnectDeviceUseCase delegates to repo`() {
        val useCase = ConnectDeviceUseCase(repo)
        val address = "00:11:22:33:44:55"
        val completable = Completable.complete()
        every { repo.connect(address) } returns completable
        val result = useCase.invoke(address)
        assert(result === completable)
        verify { repo.connect(address) }
    }

    @Test
    fun `ObserveConnectionStateUseCase delegates to repo`() {
        val useCase = ObserveConnectionStateUseCase(repo)
        val observable = Observable.just(com.bragi.blemiddleware.domain.model.ConnectionState.CONNECTED)
        every { repo.observeConnectionState() } returns observable
        val result = useCase.invoke()
        assert(result === observable)
        verify { repo.observeConnectionState() }
    }
} 