package com.example.boondocks

import androidx.lifecycle.ViewModel
import com.example.boondocks.data.LightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(lightsRepository: LightsRepository) : ViewModel() {
    val lightsMessageFlow = lightsRepository.lightsMessageFlow

}