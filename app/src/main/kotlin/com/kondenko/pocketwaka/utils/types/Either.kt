package com.kondenko.pocketwaka.utils.types

data class Either<L, R>(val left: L?, val right: R?) {

    init {
        require(left == null && right != null || left != null && right == null) { "Either should have 1 null and 1 non-null value" }
    }

    override fun toString(): String = left?.toString() ?: right?.toString() ?: throw NullPointerException("Both values of Either ar null")

}

fun <L, R> L.left() = Either<L, R>(left = this, right = null)

fun <L, R> R.right() = Either<L, R>(left = null, right = this)