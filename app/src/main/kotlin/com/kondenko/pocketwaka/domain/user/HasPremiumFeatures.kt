package com.kondenko.pocketwaka.domain.user

import com.kondenko.pocketwaka.data.user.UsersRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single

class HasPremiumFeatures(
      schedulersContainer: SchedulersContainer,
      private val getTokenHeader: UseCaseSingle<Nothing, String>,
      private val usersRepository: UsersRepository
) : UseCaseSingle<Nothing?, Boolean>(schedulersContainer) {

    override fun build(params: Nothing?): Single<Boolean> =
          getTokenHeader.build()
                .flatMap { token ->
                    usersRepository.getCurrentUser(token)
                          .map { it.hasPremiumFeatures }
                }

}