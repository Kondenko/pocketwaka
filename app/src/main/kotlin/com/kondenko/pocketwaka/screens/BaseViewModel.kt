package com.kondenko.pocketwaka.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel<T> : ViewModel() {

    protected var disposables = CompositeDisposable()

    protected val _state = MutableLiveData<State<T>>()

    val state: LiveData<State<T>> = _state

    protected fun handleError(throwable: Throwable) {
        _state.postValue(State.Failure.Unknown(exception = throwable, isFatal = true))
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

}