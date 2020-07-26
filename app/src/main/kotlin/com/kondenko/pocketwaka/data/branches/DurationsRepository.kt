package com.kondenko.pocketwaka.data.branches

import com.kondenko.pocketwaka.data.branches.model.DurationsServerModel
import com.kondenko.pocketwaka.data.branches.service.DurationsService
import io.reactivex.Single

class DurationsRepository(private val durationsService: DurationsService) {

    data class Params(val token: String, val date: String, val project: String? = null, val branches: String? = null)

    fun getData(params: Params): Single<DurationsServerModel> = params.run {
        durationsService.getDurations(token, date, project, branches)
    }

}