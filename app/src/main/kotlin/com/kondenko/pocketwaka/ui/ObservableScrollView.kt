package com.kondenko.pocketwaka.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import io.reactivex.subjects.PublishSubject

/**
 * ScrollView with scrolling callbacks.
 */
class ObservableScrollView : ScrollView{

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val scrolls: PublishSubject<ScrollEvent> = PublishSubject.create<ScrollEvent>()

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(x, y, oldX, oldY)
        scrolls.onNext(ScrollEvent(x, y, oldX, oldY))
    }

    data class ScrollEvent(val x: Int, val y: Int, val oldX: Int, val oldY: Int)

}