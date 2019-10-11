package com.kondenko.pocketwaka.domain.menu

import com.kondenko.pocketwaka.data.android.DeviceInfoProvider
import com.kondenko.pocketwaka.data.menu.MenuRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles

class GetMenuUiModel(
        schedulers: SchedulersContainer,
        private val menuRepository: MenuRepository,
        private val deviceInfoProvider: DeviceInfoProvider
) : UseCaseSingle<Nothing, MenuUiModel>(schedulers) {

    override fun build(params: Nothing?): Single<MenuUiModel> {
        val deviceInfo = deviceInfoProvider.getDeviceInfo()
        val emailText = getInitialEmailText(deviceInfo)
        val emailSubject = menuRepository.getSupportEmailSubject()
        return Singles.zip(menuRepository.getGithubUrl(), menuRepository.getSupportEmail()) { github, email ->
            MenuUiModel(
                    githubUrl = github.item,
                    supportEmail = email.item,
                    emailSubject = emailSubject,
                    initialEmailText = emailText
            )
        }
    }

    private fun getInitialEmailText(deviceInfo: DeviceInfo) = deviceInfo.run {
        "<br><br><br>Helpful info:<br>$deviceName<br>$osVersion<br>$appVersion"
    }

}