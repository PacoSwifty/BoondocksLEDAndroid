package com.example.boondocks_led.data

import android.util.Log
import com.example.boondocks_led.data.Constants.TAG
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton


interface LightsRepository {
    suspend fun emitSetLightMessage(message: BoonApiMessage)
    suspend fun emitAllOffMessage(message: BoonApiMessage)
    val lightsMessageFlow: SharedFlow<BoonApiMessage>
}

@Singleton
class LightsRepositoryImpl @Inject constructor() : LightsRepository {

    private val _lightsMessageFlow = MutableSharedFlow<BoonApiMessage>(replay = 0, extraBufferCapacity = 1)
    override val lightsMessageFlow: SharedFlow<BoonApiMessage> = _lightsMessageFlow.asSharedFlow()

    override suspend fun emitSetLightMessage(message: BoonApiMessage) {
        _lightsMessageFlow.emit(message)
        Log.i(TAG, "attempting to emit $message")

    }

    override suspend fun emitAllOffMessage(message: BoonApiMessage) {
        _lightsMessageFlow.emit(message)
    }
}


/**
 * This is just a sample for how to establish repository bindings later on.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindings {
    @Binds
    abstract fun bindRepository(
        impl: LightsRepositoryImpl
    ): LightsRepository
}
