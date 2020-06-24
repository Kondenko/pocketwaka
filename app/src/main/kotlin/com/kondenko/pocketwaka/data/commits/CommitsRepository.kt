package com.kondenko.pocketwaka.data.commits

import com.kondenko.pocketwaka.Tags.COMMITS
import com.kondenko.pocketwaka.data.commits.dao.CommitsDao
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel
import com.kondenko.pocketwaka.data.commits.model.CommitServerModel
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy


private typealias CommitsObservable = Observable<List<CommitServerModel>>

class CommitsRepository(
      private val commitsService: CommitsService,
      private val commitsDao: CommitsDao
) {

    data class Params(val tokenHeader: String, val project: String, val branch: String, val author: String? = null)

    fun getData(params: Params): CommitsObservable {
        return getFromCache(params)
//              .onErrorResumeNext(getFromServer(params)) TODO Bring back
    }

    private fun getFromServer(params: Params): CommitsObservable {
        WakaLog.d(COMMITS, "[SERVER] Getting from server (params=$params)")
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
              .doOnComplete { WakaLog.d(COMMITS, "[SERVER] Got all commits from server") }
              .doOnNext {
                  it.saveToCache(params.project).subscribeBy(
                        onComplete = { WakaLog.d(COMMITS, "[CACHE] Saved ${it.size} commits to cache") },
                        onError = { WakaLog.e("[CACHE] Failed to cache commits", it) }
                  )
              }
    }

    private fun getFromCache(params: Params) = params.run {
        WakaLog.d(COMMITS, "[CACHE] Getting from cache (params=$params)")
        // TODO Add timeout
        commitsDao.get(/*project, branch*/)
              .map { it.map { it.toServerModel() } }
              .doOnNext { WakaLog.d(COMMITS, "[CACHE] Got ${it.size} commits from cache") }
              .take(1)
    }

    private fun List<CommitServerModel>.saveToCache(project: String) =
          map { it.toDbModel(project) }.let { commits -> commitsDao._insert(commits) /* TODO Use public method */ }

    private fun CommitServerModel.toDbModel(project: String) = CommitDbModel(hash, project, branch, message, authorDate, totalSeconds)

    private fun CommitDbModel.toServerModel() = CommitServerModel(hash, branch, message, authorDate, totalSeconds)

    private fun getCommits(params: Params, page: Int) = params.run {
        commitsService.getCommits(tokenHeader, project, author, branch, page)
    }.toObservable()

}