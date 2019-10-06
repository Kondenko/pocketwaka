package com.kondenko.pocketwaka.di.modules

import androidx.room.Room
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.databaseName
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val persistenceModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, databaseName).build()
    }
}
