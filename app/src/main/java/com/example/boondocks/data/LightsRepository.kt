package com.example.boondocks.data

import android.util.Log
import com.example.boondocks.data.Constants.ANTARCTICA
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
    suspend fun emitLightSceneJsonMessage(message: String)
    val lightsMessageFlow: SharedFlow<String>
}

@Singleton
class LightsRepositoryImpl @Inject constructor() : LightsRepository {

    private val _lightsMessageFlow = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    override val lightsMessageFlow: SharedFlow<String> = _lightsMessageFlow.asSharedFlow()

    override suspend fun emitLightSceneJsonMessage(message: String) {
        _lightsMessageFlow.emit(message)
        Log.i(ANTARCTICA, "attempting to emit $message")

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
