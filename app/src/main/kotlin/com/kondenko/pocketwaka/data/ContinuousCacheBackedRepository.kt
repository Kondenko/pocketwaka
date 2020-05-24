package com.kondenko.pocketwaka.data

import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.doOnComplete
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

/**
 * This class encapsulates the logic of fetching data from a server, caching it,
 * and returning data from cache if the server returns an error.
 *
 * @param Params parameters to be used for fetching data both from the server and from cache
 * @param ServerModel the entity returned from the server
 * @param DbModel the entity to be stored in cache
 *
 * @param serverDataProvider a function to fetch data from the server
 * @param cachedDataProvider a function to fetch data from cache
 *
 */
abstract class ContinuousCacheBackedRepository<Params, ServerModel, DbModel>(
      private val workerScheduler: Scheduler,
      private val serverDataProvider: (Params) -> Single<ServerModel>,
      private val continuousCachedDataProvider: (Params) -> Observable<DbModel>,
      private val reduceModels: (Params, DbModel, DbModel) -> DbModel,
      private val cacheRetrievalTimeoutMs: Long = 3000
) {
    /**
     * First returns data from cache if it's available. Then returns data from server and caches it.
     *
     * @param params parameters to fetch data with
     * @param convert a function to convert [ServerModel] to [DbModel]
     */
    // TODO Replace receiver Single with a parameter for better readablility of function arguments
    fun getData(params: Params, convert: Single<ServerModel>.(Params) -> Observable<DbModel>): Observable<DbModel> {
        val cache: Observable<DbModel> = getDataFromCache(params)
              .onErrorResumeNext(Observable.empty())
        val server: Observable<DbModel> = getDataFromServer(params)
              .convert(params)
              .doOnComplete { dto: List<DbModel> ->
                  dto.takeIf { it.isNotEmpty() }
                        ?.also { WakaLog.v("Reducing a non-empty collection") }
                        ?.reduce { a, b -> reduceModels(params, a, b) }
                        ?.let {
                            cacheData(it)
                                  .subscribeOn(workerScheduler)
                                  .subscribeBy(
                                        onComplete = { WakaLog.v("Data cached: $dto") },
                                        onError = { WakaLog.w("Failed to cache data") }
                                  )
                        }
                        ?: WakaLog.v("An empty collection won't be reduced")
              }
              .onErrorResumeNext { error: Throwable ->
                  // Pass the network error down the stream if cache is empty
                  cache.switchIfEmpty(Observable.error(error))
              }
        return server.distinctUntilChanged()
    }

    /**
     * Fetches data from server without caching.
     */
    private fun getDataFromServer(params: Params): Single<ServerModel> =
          serverDataProvider.invoke(params)

    private fun getDataFromCache(params: Params): Observable<DbModel> =
          continuousCachedDataProvider.invoke(params)
                .map { setIsFromCache(it, true) }
                .timeout(cacheRetrievalTimeoutMs, TimeUnit.MILLISECONDS)

    /**
     * Put [data] into cache
     *
     * @return a [Completable] which completes if [data] have been cached successfully
     */
    protected abstract fun cacheData(data: DbModel): Completable

    /**
     * Sets the [DbModel::isFromCache] field to specify whether the model is obtained
     * from the server or from cache.
     *
     * @return a copy [DbModel] with the [DbModel::isFromCache] field changed
     */
    protected abstract fun setIsFromCache(model: DbModel, isFromCache: Boolean): DbModel

}