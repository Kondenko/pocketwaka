package com.kondenko.pocketwaka.data.commits

import com.kondenko.pocketwaka.data.commits.model.CommitServerModel
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import io.reactivex.Observable

class CommitsRepository(private val commitsService: CommitsService) {

    data class Params(val tokenHeader: String, val project: String, val branch: String, val author: String? = null)

    // TODO Cache commits and only fetch from server if they're stale
    fun getData(params: Params): Observable<List<CommitServerModel>> {
        val firstPage = getCommits(params, 1)
        val otherPages = firstPage
              .takeWhile { it.totalPages > 1 }
              .flatMap { firstPageResponse ->
                  Observable.concatEager((2..firstPageResponse.totalPages).map { page -> getCommits(params, page) })
              }
        return Observable.concatArray(firstPage, otherPages)
              .flatMap {
                  if (it.error == null) Observable.just(it) else Observable.error(WakatimeException(it.error))
              }
              .map { it.commits }
    }

    private fun getCommits(params: Params, page: Int) = params.run {
        commitsService.getCommits(tokenHeader, project, author, branch, page)
    }.toObservable()

}