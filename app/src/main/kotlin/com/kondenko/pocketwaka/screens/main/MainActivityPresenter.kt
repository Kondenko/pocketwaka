package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.DeleteSavedToken
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.base.BasePresenter


class MainActivityPresenter(
        private val checkIfUserIsLoggedIn: CheckIfUserIsLoggedIn,
        private val deleteSavedToken: DeleteSavedToken,
        private val refreshAccessToken: RefreshAccessToken
) : BasePresenter<MainView>() {

    override fun attach(view: MainView) {
        super.attach(view)
        checkIfLoggedIn()
    }

    private fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn.invoke(
                onSuccess = { isLoggedIn ->
                    if (isLoggedIn) {
                        refreshAccessToken.invoke()
                        view?.showStats()
                    } else {
                        view?.showLoginScreen()
                    }
                },
                onError = { error ->
                    view?.showError(error)
                }
        )
    }

    fun logout() {
        deleteSavedToken.invoke(onFinish = { view?.onLogout() })
    }

    override fun detach() {
        super.detach()
        dispose(checkIfUserIsLoggedIn, deleteSavedToken, refreshAccessToken)
    }


}