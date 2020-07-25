package com.kondenko.pocketwaka.data.user

import com.kondenko.pocketwaka.data.user.model.User
import io.reactivex.Single

class UsersRepository(private val userService: UserService) {

    fun getCurrentUser(token: String): Single<User> =
          userService.getUser(token, UserService.CURRENT).map { it.user }

}