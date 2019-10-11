package com.kondenko.pocketwaka.screens.menu

import com.kondenko.pocketwaka.domain.menu.MenuUiModel
import com.kondenko.pocketwaka.screens.State

sealed class MenuState(override val data: MenuUiModel?) : State<MenuUiModel>(data) {
    data class RateApp(override val data: MenuUiModel) : MenuState(data)
    data class SendFeedback(override val data: MenuUiModel) : MenuState(data)
    data class OpenGithub(override val data: MenuUiModel) : MenuState(data)
    object LogOut : MenuState(null)
}