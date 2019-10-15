package com.kondenko.pocketwaka.analytics

import androidx.core.os.bundleOf

sealed class Event {

    sealed class Login : Event() {
        object ButtonClicked : Login()
        object Successful : Login()
        object Unsuccessful : Login()
        object Canceled : Login()
    }

    object ManualUpdate : Event()

    sealed class Summary : Event() {
        object ConnectRepoClicks : Summary()
    }

    sealed class Menu : Event() {

        object RatingButtonClicked : Menu()

        data class RatingGiven(val rating: Int) : Menu(), HasBundle {
            override fun getBundle() = bundleOf(
                  ::rating.name to rating
            )

        }

        data class FeedbackButtonClicked(val isFromRating: Boolean) : Menu(), HasBundle {
            override fun getBundle() = bundleOf(
                  ::isFromRating.name to isFromRating
            )
        }

        object GithubClicked : Menu()

        object Logout : Menu()

    }

    sealed class EmptyState(open val screen: com.kondenko.pocketwaka.analytics.Screen) : Event() {

        data class Account(override val screen: com.kondenko.pocketwaka.analytics.Screen) : EmptyState(screen), HasBundle {
            override fun getBundle() = bundleOf(
                  ::screen.name to screen.toString()
            )
        }

        data class Screen(override val screen: com.kondenko.pocketwaka.analytics.Screen) : EmptyState(screen), HasBundle {
            override fun getBundle() = bundleOf(
                  ::screen.name to screen.toString()
            )
        }

    }

}