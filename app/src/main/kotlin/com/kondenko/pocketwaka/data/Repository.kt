package com.kondenko.pocketwaka.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

abstract class Repository<Params, ServerModel, DbModel : CacheableModel<*>>(
        private val serverDataProvider: (Params) -> Single<ServerModel>,
        private val cachedDataProvider: (Params) -> Maybe<DbModel>
) {

    fun getData(params: Params, converter: (Params, ServerModel) -> DbModel?): Observable<DbModel> {
        val cache = getDataFromCache(params)
        val server = getDataFromServer(params)
                .flatMap {
                    val dto = converter(params, it)
                    if (dto != null) Observable.just(dto)
                    else Observable.error(NullPointerException("Converted DTO is null"))
                }
                .doOnNext { dto: DbModel ->
                    cacheData(dto).subscribeBy(
                            onComplete = { Timber.d("Data cached: $dto") },
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

    private fun getDataFromServer(params: Params): Observable<ServerModel> =
            serverDataProvider.invoke(params)
                    .toObservable()

    private fun getDataFromCache(params: Params): Observable<DbModel> =
            cachedDataProvider.invoke(params)
                    .toObservable()
                    .map { setIsFromCache(it, true) }

    protected abstract fun cacheData(data: DbModel): Completable

    protected abstract fun setIsFromCache(model: DbModel, isFromCache: Boolean): DbModel

}