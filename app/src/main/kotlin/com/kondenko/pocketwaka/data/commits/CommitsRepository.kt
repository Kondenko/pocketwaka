package com.kondenko.pocketwaka.data.commits

import com.kondenko.pocketwaka.Tags.COMMITS
import com.kondenko.pocketwaka.data.commits.dao.CommitsDao
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel
import com.kondenko.pocketwaka.data.commits.model.CommitServerModel
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import com.kondenko.pocketwaka.utils.types.KOptional
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlin.math.roundToLong


private typealias CommitsObservable = Observable<List<CommitServerModel>>

class CommitsRepository(
      private val commitsService: CommitsService,
      private val commitsDao: CommitsDao,
      private val timeProvider: DateProvider
) {

    data class Params(val tokenHeader: String, val project: String, val branch: String, val author: String? = null)

    fun getData(params: Params): CommitsObservable {
        return getFromCache(params)
              .flatMap { commitsOptional ->
                  if (commitsOptional.isNotEmpty()) {
                      Observable.just(commitsOptional.get())
                            .doOnNext { WakaLog.d(COMMITS, "[CACHE] Got ${it?.size} commits from cache") }
                  } else {
                      getFromServer(params)
                            .doOnComplete { WakaLog.d(COMMITS, "[SERVER] Got all commits from server") }
                  }
              }
    }

    private fun getFromServer(params: Params): CommitsObservable {
        // WakaLog.d(COMMITS, "[SERVER] Getting from server (params=$params)")
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
              .doOnNext { latestCommits ->
                  // WakaLog.d(COMMITS, "[CACHE] Ready to cache ${latestCommits.size} commits")
                  latestCommits.saveToCache(params.project)
                        // .doOnSubscribe { WakaLog.d(COMMITS, "[CACHE] Subscribed to cache ${latestCommits.size} commits") }
                        .subscribeBy(
                              onComplete = { WakaLog.d(COMMITS, "[CACHE] Cached ${latestCommits.size} commits") },
                              onError = { WakaLog.e("[CACHE] Failed to cache commits", it) }
                        )
              }
    }

    private fun getFromCache(params: Params) = params.run {
        val currentTime = timeProvider.getCurrentTimeSec().roundToLong()
        // WakaLog.d(COMMITS, "[CACHE] Getting from cache (time=${Date(currentTime)})")
        // TODO Add timeout
        commitsDao.get(project, branch, currentTime)
              .map { it.map { it.toServerModel() } }
              .firstElement()
              .map {
                  it.takeIf { it.isNotEmpty() }?.let { KOptional.of(it) }
              }
              .defaultIfEmpty(KOptional.empty())
              .onErrorReturnItem(KOptional.empty())
              .toObservable()
    }

    private fun List<CommitServerModel>.saveToCache(project: String) =
          map { it.toDbModel(project) }.let { commits ->
              val currentTime = timeProvider.getCurrentTimeSec().roundToLong()
              // val cacheLifetime = cacheLifetimeCommitsSec
              // WakaLog.d(COMMITS, "Cached commits at ${Date(currentTime)}, next invalidation at ${Date(currentTime + cacheLifetime)}")
              commitsDao.insert(commits, currentTime)
          }

    private fun CommitServerModel.toDbModel(project: String) = CommitDbModel(hash, project, branch, message, authorDate, totalSeconds)

    private fun CommitDbModel.toServerModel() = CommitServerModel(hash, branch, message, authorDate, totalSeconds)

    private fun getCommits(params: Params, page: Int) = params.run {
        commitsService.getCommits(tokenHeader, project, author, branch, page)
    }.toObservable()

}