package com.kondenko.pocketwaka.data

interface Repository<P, T> {

    fun getData(params: P): T

}