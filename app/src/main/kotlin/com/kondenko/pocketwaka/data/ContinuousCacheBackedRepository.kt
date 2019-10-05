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
     * @param converter a function to convert [ServerModel] to [DbModel]
     */
    fun getData(params: Params, map: Single<ServerModel>.(Params) -> Observable<DbModel>): Observable<DbModel> {
        val cache = getDataFromCache(params)
                .onErrorResumeNext(Observable.empty())
        val server = getDataFromServer(params)
                .map(params)
                .doOnNext { WakaLog.d("Mapped a server model: $it") }
                .doOnComplete { dto: List<DbModel> ->
                    dto.reduce { a, b -> reduceModels(params, a, b) }.let {
                        cacheData(it)
                                .subscribeOn(workerScheduler)
                                .subscribeBy(
                                        onComplete = { WakaLog.d("Data cached: $dto") },
                                        onError = { WakaLog.w("Failed to cache data") }
                                )
                    }
                }
                .onErrorResumeNext { error: Throwable ->
                    WakaLog.w("Error retrieving data from server")
                    // Pass the network error down the stream if cache is empty
                    cache.switchIfEmpty(Observable.error(error))
                }
        return server
                .distinctUntilChanged()
                .doOnComplete { WakaLog.d("Repo observable completed") }
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
                    .doOnNext { WakaLog.d("Retrieved a model from cache: $it") }
                    .doOnError { WakaLog.d("Error retrieving a model from cache: $it") }

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