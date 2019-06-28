package com.kondenko.pocketwaka.di.modules

import android.content.Context
import androidx.room.Room
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.databaseName
import org.koin.dsl.module

object PersistenceModule {

    fun create(appContext: Context) = module {
        single {
            Room.databaseBuilder(appContext, AppDatabase::class.java, databaseName).build()
        }
        single {
            get<AppDatabase>().statsDao()
        }
    }

}