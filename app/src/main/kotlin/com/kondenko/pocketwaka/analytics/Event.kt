package com.kondenko.pocketwaka.analytics

import android.os.Bundle
import com.kondenko.pocketwaka.utils.extensions.toBundle

sealed class Event {

    sealed class Login : Event() {
        object ButtonClicked : Login()
        object Successful : Login()
        object Unsuccessful : Login()
        object Canceled : Login()
    }

    sealed class Summary : Event() {

        object ManualUpdate : Summary()

        data class RepoConnected(val completed: Boolean) : Summary(), HasBundle {
            override fun getBundle(): Bundle = toBundle()
        }

    }

    sealed class Menu : Event() {

        object RatingButtonClicked : Menu()

        data class RatingGiven(val rating: Int) : Menu(), HasBundle {
            override fun getBundle(): Bundle = toBundle()
        }

        data class FeedbackButtonClicked(val isFromRating: Boolean) : Menu(), HasBundle {
            override fun getBundle(): Bundle = toBundle()
        }

        object GithubClicked : Menu()

        object Logout : Menu()

    }

    sealed class EmptyState(open val screen: Screen) : Event() {

        data class Account(override val screen: Screen) : EmptyState(screen), HasBundle {
            override fun getBundle(): Bundle = toBundle()
        }

        data class Screen(override val screen: Screen) : EmptyState(screen), HasBundle {
            override fun getBundle(): Bundle = toBundle()
        }

    }

}

