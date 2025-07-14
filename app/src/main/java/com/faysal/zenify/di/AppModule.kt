package com.faysal.zenify.di

import com.faysal.zenify.data.repository.AudioRepositoryImpl
import com.faysal.zenify.domain.repository.AudioRepository
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.ui.MusicViewModel
import org.koin.dsl.module

val appModule = module {
    single<AudioRepository> { AudioRepositoryImpl(get()) }
    single { MusicViewModel(get()) }
    single { GetAudiosUseCase(get()) }
}
