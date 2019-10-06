package com.kondenko.pocketwaka.utils.date

data class DateRangeString(val start: String, val end: String) {
        override fun toString(): String {
                return "$start-$end"
        }
}