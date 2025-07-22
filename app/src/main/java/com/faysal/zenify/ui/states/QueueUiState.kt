package com.faysal.zenify.ui.states

data class QueueUiState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = true,
    val error: String? = null,
    val message: String? = null
)