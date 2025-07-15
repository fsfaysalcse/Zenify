package com.faysal.zenify.di

import com.faysal.zenify.data.repository.AudioRepositoryImpl
import com.faysal.zenify.data.service.MediaLibraryCallback
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.repository.AudioRepository
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.ui.viewModels.MusicViewModel
import org.koin.dsl.module

val appModule = module {
    single<AudioRepository> { AudioRepositoryImpl(get()) }
    single { MusicViewModel(get(), get()) }
    single { GetAudiosUseCase(get()) }
    single { MediaLibraryCallback() }
    single { MusicServiceConnection(get()) }
}
