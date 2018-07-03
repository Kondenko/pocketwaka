package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.View
import com.kondenko.pocketwaka.screens.base.stateful.states.ViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

abstract class ModelFragment<M : Parcelable> : Fragment() {

    val TAG = "ModelFragment"

    private val viewStateSubject = PublishSubject.create<ViewState>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewStateSubject.onNext(ViewState.Visible)
    }

    override fun onDestroyView() {
        viewStateSubject.onNext(ViewState.Destroyed)
        super.onDestroyView()
    }

    fun subscribeToModelChanges(modelObservable: Observable<M>){
        viewStateSubject
                .takeWhile { it === ViewState.Visible }
                .flatMap { modelObservable }
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onModelChanged)
    }

    protected abstract fun onModelChanged(model: M)

}
