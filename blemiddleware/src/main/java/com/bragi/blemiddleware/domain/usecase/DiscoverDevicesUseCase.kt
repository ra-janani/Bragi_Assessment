package com.bragi.blemiddleware.domain.usecase

import com.bragi.blemiddleware.domain.repository.BleRepository
import javax.inject.Inject

class DiscoverDevicesUseCase @Inject constructor(
  private val repo: BleRepository
) {
  operator fun invoke() = repo.scanForPeripherals()
}