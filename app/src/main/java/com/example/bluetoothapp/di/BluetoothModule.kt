package com.example.bluetoothapp.di

import com.example.bluetoothapp.BluetoothManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
    
    @Provides
    @Singleton
    fun provideBluetoothManager(): BluetoothManager {
        return BluetoothManager.getInstance()
    }
} 