package com.kondenko.pocketwaka.ui

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.ScrollView
import io.reactivex.subjects.PublishSubject

/**
 * ScrollView with scrolling callbacks.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ObservableScrollView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : ScrollView(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    val scrolls: PublishSubject<ScrollEvent> = PublishSubject.create<ScrollEvent>()

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(x, y, oldX, oldY)
        scrolls.onNext(ScrollEvent(x, y, oldX, oldY))
    }

    data class ScrollEvent(val x: Int, val y: Int, val oldX: Int, val oldY: Int)

}