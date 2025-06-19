package com.bragi.blemiddleware.di

import android.content.Context
import com.bragi.blemiddleware.data.repository.AndroidBleGattManager
import com.bragi.blemiddleware.data.repository.AndroidBleScanner
import com.bragi.blemiddleware.data.repository.BleRepositoryImpl
import com.bragi.blemiddleware.domain.repository.BleGattManager
import com.bragi.blemiddleware.domain.repository.BleScanner
import com.bragi.blemiddleware.domain.repository.BleRepository
import com.polidea.rxandroidble3.RxBleClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BleModule {

    @Provides
    @Singleton
    fun provideBleScanner(@ApplicationContext context: Context): BleScanner =
        AndroidBleScanner(RxBleClient.create(context))

    @Provides
    @Singleton
    fun provideBleGattManager(@ApplicationContext context: Context): BleGattManager =
        AndroidBleGattManager(RxBleClient.create(context))

    @Provides
    @Singleton
    fun provideBleRepository(
        scanner: BleScanner,
        gattManager: BleGattManager
    ): BleRepository = BleRepositoryImpl(scanner, gattManager)
}