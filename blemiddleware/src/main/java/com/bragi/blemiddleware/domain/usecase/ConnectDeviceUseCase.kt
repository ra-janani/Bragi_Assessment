package com.bragi.blemiddleware.domain.usecase

import com.bragi.blemiddleware.domain.repository.BleRepository
import javax.inject.Inject

class ConnectDeviceUseCase @Inject constructor(
  private val repo: BleRepository
) {
  operator fun invoke(address: String) = repo.connect(address)
}