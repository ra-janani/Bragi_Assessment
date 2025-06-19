package com.bragi.blemiddleware.domain.usecase

import com.bragi.blemiddleware.domain.repository.BleRepository
import javax.inject.Inject

class ObserveConnectionStateUseCase @Inject constructor(
    private val repo: BleRepository
) {
    operator fun invoke() = repo.observeConnectionState()
}