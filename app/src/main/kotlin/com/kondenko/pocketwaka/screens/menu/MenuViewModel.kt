package com.kondenko.pocketwaka.screens.menu

import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.screens.base.BaseViewModel

class MenuViewModel(private val clearCache: ClearCache) : BaseViewModel<MenuState>() {

    fun logout() {
        clearCache(onFinish = { _state.value = MenuState.LogOut })
    }

}