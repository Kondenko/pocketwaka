package com.kondenko.pocketwaka.data.commits

import com.kondenko.pocketwaka.Tags.COMMITS
import com.kondenko.pocketwaka.data.commits.dao.CommitsDao
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel
import com.kondenko.pocketwaka.data.commits.model.CommitServerModel
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable


private typealias CommitsObservable = Observable<List<CommitServerModel>>

class CommitsRepository(
      private val commitsService: CommitsService,
      private val commitsDao: CommitsDao
) {

    data class Params(val tokenHeader: String, val project: String, val branch: String, val author: String? = null)

    fun getData(params: Params): CommitsObservable {
        val server = getFromServer(params)
        return getFromCache(params)
              .switchIfEmpty(getFromServer(params))
              .onErrorResumeNext(server)
    }

    private fun getFromServer(params: Params): CommitsObservable {
        WakaLog.d(COMMITS, "{SERVER] Getting from server (params=$params)")
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
              .doOnNext { WakaLog.d(COMMITS, "{SERVER] Got ${it.size} commits from server") }
              .doOnNext { it.saveToCache(params.project) }
    }

    private fun getFromCache(params: Params): CommitsObservable = params.run {
        WakaLog.d(COMMITS, "{SERVER] Getting from cache (params=$params)")
        commitsDao.get(project, branch)
              .map { it.toServerModel() }
              .toList()
              .doOnSuccess { WakaLog.d(COMMITS, "[CACHE} Got ${it.size} commits from cache") }
              .toObservable()
    }

    private fun List<CommitServerModel>.saveToCache(project: String): Completable {
        return this
              .toObservable()
              .map { it.toDbModel(project) }
              .toList()
              .doOnSuccess { commitsDao.insert(it) }
              .doOnSuccess { WakaLog.d(COMMITS, "[CACHE} Saved ${it.size} commits to cache") }
              .ignoreElement()
    }

    private fun CommitServerModel.toDbModel(project: String) = CommitDbModel(hash, project, branch, message, authorDate, totalSeconds)

    private fun CommitDbModel.toServerModel() = CommitServerModel(hash, branch, message, authorDate, totalSeconds)

    private fun getCommits(params: Params, page: Int) = params.run {
        commitsService.getCommits(tokenHeader, project, author, branch, page)
    }.toObservable()

}