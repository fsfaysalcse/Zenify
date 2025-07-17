package com.faysal.zenify.ui.states

/**
 * Sealed class representing different gesture actions
 */

sealed class GestureAction {
    object Left : GestureAction()
    object Right : GestureAction()
    object Up : GestureAction()
    object Down : GestureAction()

    sealed class Hold : GestureAction() {
        object Left : Hold()
        object Right : Hold()
        object Up : Hold()
        object Down : Hold()
    }
}