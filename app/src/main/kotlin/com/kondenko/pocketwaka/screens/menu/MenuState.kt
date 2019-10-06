package com.kondenko.pocketwaka.screens.menu

import com.kondenko.pocketwaka.screens.State

sealed class MenuState : State<Nothing>(null) {
    object LogOut : MenuState()
}