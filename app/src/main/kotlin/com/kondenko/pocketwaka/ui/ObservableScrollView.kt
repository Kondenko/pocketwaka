package com.kondenko.pocketwaka.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * A [ScrollView] with scrolling callbacks.
 */
class ObservableScrollView
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ScrollView(context, attrs, defStyleAttr, defStyleRes) {

    data class ScrollEvent(val x: Int, val y: Int, val oldX: Int, val oldY: Int)

    private val scrolls: PublishSubject<ScrollEvent> = PublishSubject.create<ScrollEvent>()

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(x, y, oldX, oldY)
        scrolls.onNext(ScrollEvent(x, y, oldX, oldY))
    }

    fun scrolls(): Observable<ScrollEvent> = scrolls

}