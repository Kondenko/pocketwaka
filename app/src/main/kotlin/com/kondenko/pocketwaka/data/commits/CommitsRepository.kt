package com.kondenko.pocketwaka.data.commits

import android.content.Context
import com.kondenko.pocketwaka.data.commits.model.Commit
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import io.reactivex.Single

class CommitsRepository(private val context: Context, private val commitsService: CommitsService) {

    data class Params(val tokenHeader: String, val project: String, val author: String? = null, val page: Int? = null)

    fun getData(params: Params): Single<List<Commit>> =
            commitsService.getCommits(params.tokenHeader, params.project, params.author, params.page)
                  .flatMap {
                      if (it.error == null) Single.just(it)
                      else Single.error(WakatimeException(it.error))
                  }
                  .map { it.commits }

}