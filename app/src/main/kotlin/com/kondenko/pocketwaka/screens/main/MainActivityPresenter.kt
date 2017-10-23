package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.DeleteSavedToken
import com.kondenko.pocketwaka.screens.BasePresenter
import javax.inject.Inject

@PerApp
class MainActivityPresenter
@Inject constructor(private val checkIfUserIsLoggedIn: CheckIfUserIsLoggedIn, private val deleteSavedToken: DeleteSavedToken) : BasePresenter<MainView>() {

    fun logout() {
        deleteSavedToken.execute {
            view?.onLogout()
        }
    }

    fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn.execute(
                onSuccess = { isLoggedIn ->
                    if (!isLoggedIn) view?.showLoginScreen()
                },
                onError = { error ->
                    view?.onError(error)
                }
        )
    }

    override fun detach() {
        super.detach()
        dispose(checkIfUserIsLoggedIn, deleteSavedToken)
    }


}