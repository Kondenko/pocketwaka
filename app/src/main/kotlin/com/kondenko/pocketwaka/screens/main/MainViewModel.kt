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

    private fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn(
                onSuccess = { isLoggedIn ->
                    if (isLoggedIn) {
                        refreshAccessToken.invoke()
                        state.value = ShowData
                    } else {
                        state.value = ShowLoginScreen
                    }
                },
                onError = { error ->
                    clearCache(onFinish = {
                        state.value = LogOut
                    })
                }
        )
    }

    override fun onCleared() {
        checkIfUserIsLoggedIn.dispose()
        refreshAccessToken.dispose()
        super.onCleared()
    }

}