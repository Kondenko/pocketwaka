package com.kondenko.pocketwaka.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainState.*


class MainViewModel(
        private val checkIfUserIsLoggedIn: CheckIfUserIsLoggedIn,
        private val clearCache: ClearCache,
        private val refreshAccessToken: RefreshAccessToken
) : ViewModel() {

    private val state = MutableLiveData<MainState>()

    init {
        checkIfLoggedIn()
    }

    fun states(): LiveData<MainState> = state

    fun logout() {
        clearCache(onFinish = { state.value = LogOut })
    }

    private fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn(
                onSuccess = { isLoggedIn ->
                    if (isLoggedIn) {
                        refreshAccessToken.invoke()
                        state.value = ShowStats
                    } else {
                        state.value = ShowLoginScreen
                    }
                },
                onError = { error ->
                    state.value = Error(error)
                    logout()
                }
        )
    }

    override fun onCleared() {
        checkIfUserIsLoggedIn.dispose()
        clearCache.dispose()
        refreshAccessToken.dispose()
        super.onCleared()
    }

}