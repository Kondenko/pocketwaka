package com.kondenko.pocketwaka.data.commits

import com.kondenko.pocketwaka.data.commits.model.CommitServerModel
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import io.reactivex.Observable

class CommitsRepository(private val commitsService: CommitsService) {

    data class Params(val tokenHeader: String, val project: String, val author: String? = null)

    // TODO Cache commits and only fetch from server if they're stale
    fun getData(params: Params): Observable<List<CommitServerModel>> {
        val firstPage = commitsService.getCommits(params.tokenHeader, params.project, params.author, 0).toObservable()
        val otherPages = firstPage.flatMap {
            Observable.concatEager(
                  (1..it.totalPages).map { page ->
                      commitsService.getCommits(params.tokenHeader, params.project, params.author, page)
                            .toObservable()
                  }
            )
        }
        return Observable.concatArray(firstPage, otherPages)
              .flatMap {
                  if (it.error == null) Observable.just(it)
                  else Observable.error(WakatimeException(it.error))
              }.map { it.commits }
    }


}