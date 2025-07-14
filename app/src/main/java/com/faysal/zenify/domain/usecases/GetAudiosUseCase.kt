package com.faysal.zenify.domain.usecases

import com.faysal.zenify.domain.repository.AudioRepository
import com.faysal.zenify.ui.model.Audio

open class GetAudiosUseCase(
    private val repository: AudioRepository
) {
    suspend operator fun invoke(): List<Audio> {
        return repository.getAllAudio()
    }
}