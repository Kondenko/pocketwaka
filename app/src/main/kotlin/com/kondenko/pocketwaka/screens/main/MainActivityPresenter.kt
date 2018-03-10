package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.DeleteSavedToken
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.BasePresenter
import javax.inject.Inject

@PerApp
class MainActivityPresenter
@Inject constructor(
        private val checkIfUserIsLoggedIn: CheckIfUserIsLoggedIn,
        private val deleteSavedToken: DeleteSavedToken,
        private val refreshAccessToken: RefreshAccessToken
) : BasePresenter<MainView>() {

    override fun attach(view: MainView) {
        super.attach(view)
        refreshAccessToken.execute()
    }

    fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn.execute(
                onSuccess = { isLoggedIn ->
                    if (!isLoggedIn) view?.showLoginScreen()
                    else view?.showStats()
                },
                onError = { error ->
                    view?.onError(error)
                }
        )
    }

    fun logout() {
        deleteSavedToken.execute {
            view?.onLogout()
        }
    }

    override fun detach() {
        super.detach()
        dispose(checkIfUserIsLoggedIn, deleteSavedToken, refreshAccessToken)
    }


}