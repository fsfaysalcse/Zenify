package com.faysal.zenify.ui.mock

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.repository.FakeAudioRepository
import com.faysal.zenify.domain.repository.FakeFavouriteRepository
import com.faysal.zenify.domain.repository.FakeQueueRepository
import com.faysal.zenify.domain.usecases.AddToFavouritesUseCase
import com.faysal.zenify.domain.usecases.AddToQueueNextUseCase
import com.faysal.zenify.domain.usecases.AddToQueueUseCase
import com.faysal.zenify.domain.usecases.ClearQueueUseCase
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.domain.usecases.GetFavouritesUseCase
import com.faysal.zenify.domain.usecases.GetQueueItemsUseCase
import com.faysal.zenify.domain.usecases.IsFavouriteFlowUseCase
import com.faysal.zenify.domain.usecases.MoveQueueItemUseCase
import com.faysal.zenify.domain.usecases.RemoveFromFavouritesUseCase
import com.faysal.zenify.domain.usecases.RemoveFromQueueUseCase
import com.faysal.zenify.domain.usecases.ToggleFavouriteUseCase
import com.faysal.zenify.ui.viewModels.FavouriteViewModel
import com.faysal.zenify.ui.viewModels.MusicViewModel
import com.faysal.zenify.ui.viewModels.QueueViewModel


@OptIn(UnstableApi::class)
@Composable
fun rememberFakeMusicViewModel(): MusicViewModel {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = FakeAudioRepository()
                val queueRepository = FakeQueueRepository()
                val favouriteRepository = FakeFavouriteRepository()
                val serviceConnect = MusicServiceConnection(context)
                val playlistDataStore = com.faysal.zenify.data.datastore.PlaylistDataStore(context)
                @Suppress("UNCHECKED_CAST")
                return MusicViewModel(
                    serviceConnection = serviceConnect,
                    getAudiosUseCase = GetAudiosUseCase(repository),
                    addToQueueUseCase = AddToQueueUseCase(queueRepository),
                    addToQueueNextUseCase = AddToQueueNextUseCase(queueRepository),
                    toggleFavouriteUseCase = ToggleFavouriteUseCase(favouriteRepository),
                    isFavouriteFlowUseCase = IsFavouriteFlowUseCase(favouriteRepository),
                    removeFromFavouritesUseCase = RemoveFromFavouritesUseCase(favouriteRepository),
                    savedStateHandle = SavedStateHandle(),
                    playlistDataStore = playlistDataStore,
                    playbackStateManager = com.faysal.zenify.data.datastore.PlaybackStateManager(context),
                ) as T
            }
        }
    }

    val viewModel: MusicViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        viewModel.loadAudios()
    }

    return viewModel
}



@OptIn(UnstableApi::class)
@Composable
fun rememberFakeFavouriteViewModel(): FavouriteViewModel {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val queueRepository = FakeQueueRepository()
                val favouriteRepository = FakeFavouriteRepository()
                val serviceConnect = MusicServiceConnection(context)
                @Suppress("UNCHECKED_CAST")
                return FavouriteViewModel(
                    serviceConnection = serviceConnect,
                    addToQueueUseCase = AddToQueueUseCase(queueRepository),
                    toggleFavouriteUseCase = ToggleFavouriteUseCase(favouriteRepository),
                    isFavouriteFlowUseCase = IsFavouriteFlowUseCase(favouriteRepository),
                    removeFromFavouritesUseCase = RemoveFromFavouritesUseCase(favouriteRepository),
                    addToFavouritesUseCase = AddToFavouritesUseCase(favouriteRepository),
                    getFavouritesUseCase = GetFavouritesUseCase(favouriteRepository)
                ) as T
            }
        }
    }

    val viewModel: FavouriteViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        viewModel.loadDummyMusic()
    }

    return viewModel
}


@UnstableApi
@Composable
fun rememberFakeQueueViewModel(): QueueViewModel {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val queueRepository = FakeQueueRepository()
                @Suppress("UNCHECKED_CAST")
                return QueueViewModel(
                    serviceConnection = MusicServiceConnection(context),
                    addToQueueUseCase = AddToQueueUseCase(queueRepository),
                    addToQueueNextUseCase = AddToQueueNextUseCase(queueRepository),
                    removeFromQueueUseCase = RemoveFromQueueUseCase(queueRepository),
                    moveQueueItemUseCase = MoveQueueItemUseCase(queueRepository),
                    clearQueueUseCase = ClearQueueUseCase(queueRepository),
                    getQueueItemsUseCase = GetQueueItemsUseCase(queueRepository),
                ) as T
            }
        }
    }

    return viewModel(factory = factory)
}