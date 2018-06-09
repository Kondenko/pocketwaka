package com.kondenko.pocketwaka.screens.base.stateful.states

sealed class ViewState {
    object Visible : ViewState()
    object Destroyed : ViewState()
}