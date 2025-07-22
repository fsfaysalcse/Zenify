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
import com.faysal.zenify.domain.usecases.AddToQueueNextUseCase
import com.faysal.zenify.domain.usecases.AddToQueueUseCase
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.domain.usecases.IsFavouriteFlowUseCase
import com.faysal.zenify.domain.usecases.RemoveFromFavouritesUseCase
import com.faysal.zenify.domain.usecases.ToggleFavouriteUseCase
import com.faysal.zenify.ui.viewModels.MusicViewModel


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
                @Suppress("UNCHECKED_CAST")
                return MusicViewModel(
                    serviceConnection = serviceConnect,
                    getAudiosUseCase = GetAudiosUseCase(repository),
                    addToQueueUseCase = AddToQueueUseCase(queueRepository),
                    addToQueueNextUseCase = AddToQueueNextUseCase(queueRepository),
                    toggleFavouriteUseCase = ToggleFavouriteUseCase(favouriteRepository),
                    isFavouriteFlowUseCase = IsFavouriteFlowUseCase(favouriteRepository),
                    removeFromFavouritesUseCase = RemoveFromFavouritesUseCase(favouriteRepository),
                    savedStateHandle = SavedStateHandle()
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
