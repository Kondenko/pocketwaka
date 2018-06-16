package com.kondenko.pocketwaka.screens.stats

sealed class ScrollingDirection {
    object Up : ScrollingDirection()
    object Down: ScrollingDirection()
}