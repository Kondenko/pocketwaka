package com.kondenko.pocketwaka.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import timber.log.Timber


/**
 * Created by Kondenko on 02.11.2017.
 */
open class LogFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag(this.toString()).i("onAttach($context: Context?)")
    }

    override fun onPause() {
        super.onPause()
        Timber.tag(this.toString()).i("onPause()")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag(this.toString()).i("onViewCreated($view: View?, $savedInstanceState: Bundle?)")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.tag(this.toString()).i("onActivityCreated($savedInstanceState: Bundle?)")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(this.toString()).i("onCreate($savedInstanceState: Bundle?)")
    }

    override fun onStart() {
        super.onStart()
        Timber.tag(this.toString()).i("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(this.toString()).i("onResume()")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.tag(this.toString()).i("onDetach()")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.tag(this.toString()).i("onCreateView($inflater: LayoutInflater?, $container: ViewGroup?, $savedInstanceState: Bundle?): View?")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        Timber.tag(this.toString()).i("onAttachFragment($childFragment: Fragment?)")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag(this.toString()).i("onDestroyView()")
    }

    override fun onStop() {
        super.onStop()
        Timber.tag(this.toString()).i("onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(this.toString()).i("onDestroy()")
    }

}