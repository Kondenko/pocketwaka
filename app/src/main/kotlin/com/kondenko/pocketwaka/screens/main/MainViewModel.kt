package com.kondenko.pocketwaka.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.main.FetchRemoteConfigValues
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainState.GoToLogin
import com.kondenko.pocketwaka.screens.main.MainState.ShowData
import com.kondenko.pocketwaka.utils.WakaLog


class MainViewModel(
      defaultTabId: Int,
      private val checkIfUserIsLoggedIn: UseCaseSingle<Nothing, Boolean>,
      private val clearCache: ClearCache,
      private val refreshAccessToken: RefreshAccessToken,
      fetchRemoteConfigValues: FetchRemoteConfigValues
) : ViewModel() {

    private val state = MutableLiveData<MainState>()

    private val selectedTab = MutableLiveData<Int>().apply {
        value = defaultTabId
    }

    init {
        tabChanged(defaultTabId)
        checkIfLoggedIn()
        fetchRemoteConfigValues(
              onSuccess = { WakaLog.d("Remote config values updated") },
              onError = { WakaLog.e("Error updating Remote config values", it) }
        )
    }

    fun state(): LiveData<MainState> = state

    fun tabSelections(): LiveData<Int> = selectedTab

    private fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn(
              onSuccess = { isLoggedIn ->
                  if (isLoggedIn) {
                      refreshAccessToken.invoke()
                      state.postValue(ShowData)
                  } else {
                      state.postValue(GoToLogin)
                  }
              },
              onError = { error ->
                  clearCache(onFinish = { state.postValue(GoToLogin) })
              }
        )
    }

    fun tabChanged(tab: Int) {
        selectedTab.value = tab
    }

    override fun onCleared() {
        checkIfUserIsLoggedIn.dispose()
        refreshAccessToken.dispose()
        super.onCleared()
    }

}