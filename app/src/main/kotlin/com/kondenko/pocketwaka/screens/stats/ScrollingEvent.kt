package com.kondenko.pocketwaka.screens.stats

sealed class ScrollDirection {
    object Up : ScrollDirection()
    object Down: ScrollDirection()
}