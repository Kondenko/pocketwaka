package com.kondenko.pocketwaka.data.branches

import com.kondenko.pocketwaka.data.Repository
import com.kondenko.pocketwaka.data.branches.model.DurationsServerModel
import com.kondenko.pocketwaka.data.branches.service.DurationsService
import io.reactivex.Single

class DurationsRepository(private val durationsService: DurationsService)
    : Repository<DurationsRepository.Params, Single<DurationsServerModel>> {

    data class Params(val token: String, val date: String, val project: String? = null, val branches: String? = null)

    override fun getData(params: Params) =
            params.run { durationsService.getDurations(token, date, project, branches) }

}