package com.kondenko.pocketwaka.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

abstract class Repository<ServiceModel, Dto : CacheableModel<*>, DomainModel : CacheableModel<*>>(
        private val serviceResponseConverter: Converter<ServiceModel, Dto>,
        private val dtoConverter: Converter<Dto, DomainModel>
) {

    abstract class Params()

    fun getStats(params: Params): Observable<DomainModel> {
        val cache = getDataFromCache(params)
        val server = getDataFromServer(params)
                .onErrorResumeNext { error: Throwable ->
                    // Pass the network error down the stream if cache is empty
                    cache.switchIfEmpty(Observable.error(error))
                }
        return Observable.concatArrayDelayError(cache, server)
                .distinctUntilChanged()
                .map { dtoConverter.convert(it) }
    }


    protected abstract fun getData(params: Params): Single<ServiceModel>

    protected abstract fun getCachedData(params: Params): Maybe<Dto>

    protected abstract fun cacheData(data: Dto): Completable

    private fun getDataFromCache(params: Params): Observable<Dto> =
            getCachedData(params)
                    .toObservable()
                    .map { setIsFromCache(it, true) }

    private fun getDataFromServer(params: Params): Observable<Dto> =
            getData(params)
                    .map { serviceResponseConverter.convert(it) }
                    .doOnSuccess {
                        cacheData(it).subscribeBy(
                                onComplete = { Timber.d("Data cached: $it") },
                                onError = { Timber.w(it, "Failed to cache data") }
                        )
                    }
                    .toObservable()

    protected abstract fun setIsFromCache(model: Dto, isFromCache: Boolean): Dto

}