package com.kondenko.pocketwaka.screens.base.stateful.states


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.kondenko.pocketwaka.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.layout_stats_state_error.*


class FragmentErrorState : androidx.fragment.app.Fragment() {

    val TAG = "FragmentErrorState"

    private val publishSubject = PublishSubject.create<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.layout_stats_state_error, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RxView.clicks(button_errorstate_retry).subscribeWith(publishSubject)
    }

    fun setMessage(message: CharSequence) {
        textivew_errorstate_message.text = message
    }

    fun retryClicks(): Observable<Any> = publishSubject


}
