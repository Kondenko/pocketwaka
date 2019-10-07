package com.kondenko.pocketwaka.data.commits

import android.content.Context
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.commits.model.CommitsServiceModel
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import io.reactivex.Single

class CommitsRepository(private val context: Context, private val commitsService: CommitsService) {

    data class Params(val tokenHeader: String, val project: String, val author: String? = null, val page: Int? = null)

    fun getData(params: Params): Single<CommitsServiceModel> =
            commitsService.getCommits(params.tokenHeader, params.project, params.author, params.page)

    fun getUnknownBranchName() = context.getString(R.string.commits_branch_name_unknown)

}