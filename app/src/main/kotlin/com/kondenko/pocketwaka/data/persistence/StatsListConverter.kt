package com.kondenko.pocketwaka.data.persistence

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.kondenko.pocketwaka.domain.ranges.model.StatsModel

object StatsListConverter {

    private val statsItemFactory = RuntimeTypeAdapterFactory
            .of(StatsModel::class.java)
            .registerSubtype(StatsModel.Info::class.java)
            .registerSubtype(StatsModel.Stats::class.java)
            .registerSubtype(StatsModel.BestDay::class.java)

    private val gson = GsonBuilder()
            .serializeNulls()
            .registerTypeAdapterFactory(statsItemFactory)
            .create()

    private val typeToken = object : TypeToken<List<StatsModel>>() {}.type

    @TypeConverter
    @JvmStatic
    fun toJson(statsModels: List<StatsModel>): String {
        return gson.toJson(statsModels, typeToken)
    }

    @TypeConverter
    @JvmStatic
    fun fromJson(statsModelsJson: String): List<StatsModel> {
        return gson.fromJson(statsModelsJson, typeToken)
    }
}