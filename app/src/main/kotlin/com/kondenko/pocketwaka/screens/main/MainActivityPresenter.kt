package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.main.IsUserLoggedIn
import com.kondenko.pocketwaka.screens.BasePresenter
import javax.inject.Inject

@PerApp
class MainActivityPresenter @Inject constructor(private val isUserLoggedIn: IsUserLoggedIn) : BasePresenter<MainView>() {

    fun checkIfLoggedIn() {
        isUserLoggedIn.execute(null,
                { isLoggedIn ->
                    if (!isLoggedIn) view?.showLoginScreen()
                },
                { error ->
                    view?.onError(error)
                }
        )
    }

    override fun detach() {
        super.detach()
        dispose(isUserLoggedIn)
    }


}