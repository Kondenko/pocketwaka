package com.kondenko.pocketwaka.screens.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.screens.State
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel<T> : ViewModel() {

    protected var disposables = CompositeDisposable()

    protected val stateLiveData = MutableLiveData<State<T>>()

    protected val state: State<T>?
        get() = stateLiveData.value

    fun state(): LiveData<State<T>> = stateLiveData

    protected open fun setState(state: State<T>) {
        stateLiveData.postValue(state)
    }

    protected fun handleError(throwable: Throwable) {
        setState(State.Failure.Unknown(exception = throwable, isFatal = true))
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

}