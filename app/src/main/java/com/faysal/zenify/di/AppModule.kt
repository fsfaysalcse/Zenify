package com.faysal.zenify.di

import androidx.media3.common.util.UnstableApi
import androidx.room.Room
import com.faysal.zenify.data.database.ZenifyDatabase
import com.faysal.zenify.data.datastore.PlaybackStateManager
import com.faysal.zenify.data.repository.AudioRepositoryImpl
import com.faysal.zenify.data.repository.FavouriteRepositoryImpl
import com.faysal.zenify.data.repository.QueueRepositoryImpl
import com.faysal.zenify.data.service.MediaLibraryCallback
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.repository.AudioRepository
import com.faysal.zenify.domain.repository.FavouriteRepository
import com.faysal.zenify.domain.repository.QueueRepository
import com.faysal.zenify.domain.usecases.AddMultipleToQueueUseCase
import com.faysal.zenify.domain.usecases.AddToFavouritesUseCase
import com.faysal.zenify.domain.usecases.AddToQueueNextUseCase
import com.faysal.zenify.domain.usecases.AddToQueueUseCase
import com.faysal.zenify.domain.usecases.ClearFavouritesUseCase
import com.faysal.zenify.domain.usecases.ClearQueueUseCase
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.domain.usecases.GetFavouritesCountUseCase
import com.faysal.zenify.domain.usecases.GetFavouritesUseCase
import com.faysal.zenify.domain.usecases.GetQueueItemsUseCase
import com.faysal.zenify.domain.usecases.GetQueueSizeUseCase
import com.faysal.zenify.domain.usecases.IsFavouriteFlowUseCase
import com.faysal.zenify.domain.usecases.IsFavouriteUseCase
import com.faysal.zenify.domain.usecases.MoveQueueItemUseCase
import com.faysal.zenify.domain.usecases.RemoveFromFavouritesUseCase
import com.faysal.zenify.domain.usecases.RemoveFromQueueUseCase
import com.faysal.zenify.domain.usecases.ToggleFavouriteUseCase
import com.faysal.zenify.ui.viewModels.MusicViewModel
import com.faysal.zenify.ui.viewModels.FavouriteViewModel
import com.faysal.zenify.ui.viewModels.QueueViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module


val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ZenifyDatabase::class.java,
            ZenifyDatabase.DATABASE_NAME
        ).build()
    }

    single { get<ZenifyDatabase>().queueDao() }
    single { get<ZenifyDatabase>().favouriteDao() }
}

val repositoryModule = module {
    single<QueueRepository> { QueueRepositoryImpl(get()) }
    single<FavouriteRepository> { FavouriteRepositoryImpl(get()) }
    single<AudioRepository> { AudioRepositoryImpl(get()) }
}

val useCaseModule = module {
    factory { GetQueueItemsUseCase(get()) }
    factory { AddToQueueUseCase(get()) }
    factory { AddToQueueNextUseCase(get()) }
    factory { AddMultipleToQueueUseCase(get()) }
    factory { RemoveFromQueueUseCase(get()) }
    factory { MoveQueueItemUseCase(get()) }
    factory { ClearQueueUseCase(get()) }
    factory { GetQueueSizeUseCase(get()) }

    factory { GetFavouritesUseCase(get()) }
    factory { AddToFavouritesUseCase(get()) }
    factory { RemoveFromFavouritesUseCase(get()) }
    factory { IsFavouriteUseCase(get()) }
    factory { IsFavouriteFlowUseCase(get()) }
    factory { ToggleFavouriteUseCase(get()) }
    factory { ClearFavouritesUseCase(get()) }
    factory { GetFavouritesCountUseCase(get()) }

    factory { GetAudiosUseCase(get()) }
}

@UnstableApi
val serviceModule = module {
    single { MusicServiceConnection(androidContext()) }
    single { PlaybackStateManager(androidContext()) }
    single { MediaLibraryCallback() }
}

@UnstableApi
val viewModelModule = module {
    viewModel { MusicViewModel(get(), get(), get(),get(),get(),get(),get(),get()) }
    viewModel { QueueViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { FavouriteViewModel(get(), get(), get(), get(), get(), get(), get()) }
}

val appModules = listOf(
    databaseModule,
    repositoryModule,
    useCaseModule,
    serviceModule,
    viewModelModule
)
