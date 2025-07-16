package com.faysal.zenify.domain.model

sealed class UiTypes {
    object Album : UiTypes()
    object Artist : UiTypes()
    object Folder : UiTypes()
}