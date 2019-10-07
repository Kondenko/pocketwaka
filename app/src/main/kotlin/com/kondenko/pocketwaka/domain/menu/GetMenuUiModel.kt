package com.kondenko.pocketwaka.domain.menu

import com.kondenko.pocketwaka.data.menu.MenuRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single

class GetMenuUiModel(schedulers: SchedulersContainer, private val menuRepository: MenuRepository) : UseCaseSingle<Nothing, MenuUiModel>(schedulers) {

    override fun build(params: Nothing?): Single<MenuUiModel> {
        return menuRepository.getGithubUrl()
                .map { MenuUiModel(it) }
    }

}