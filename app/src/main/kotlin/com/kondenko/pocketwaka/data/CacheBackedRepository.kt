package com.kondenko.pocketwaka.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

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
abstract class CacheBackedRepository<Params, ServerModel, DbModel : CacheableModel<*>>(
        private val serverDataProvider: (Params) -> Single<ServerModel>,
        private val cachedDataProvider: (Params) -> Maybe<DbModel>
) {
    /**
     * First returns data from cache if it's available. Then returns data from server and caches it.
     *
     * @param params parameters to fetch data with
     * @param converter a function to convert [ServerModel] to [DbModel]
     */
    fun getData(params: Params, mapper: Observable<ServerModel>.(Params) -> Observable<DbModel>): Observable<DbModel> {
        val cache = getDataFromCache(params)
        val server = getDataFromServer(params)
                .mapper(params)
                .doOnNext { dto: DbModel ->
                    cacheData(dto).subscribeBy(
                            onComplete = { Timber.d("Data cached: ${dto::class}") },
                            onError = { Timber.w(it, "Failed to cache data") }
                    )
                }
                .onErrorResumeNext { error: Throwable ->
                    // Pass the network error down the stream if cache is empty
                    cache.switchIfEmpty(Observable.error(error))
                }
        return Observable.concatArrayDelayError(cache, server)
                .distinctUntilChanged()
    }

    /**
     * Fetches data from server without caching.
     */
    private fun getDataFromServer(params: Params): Observable<ServerModel> =
            serverDataProvider.invoke(params)
                    .toObservable()

    private fun getDataFromCache(params: Params): Observable<DbModel> =
            cachedDataProvider.invoke(params)
                    .toObservable()
                    .map { setIsFromCache(it, true) }

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