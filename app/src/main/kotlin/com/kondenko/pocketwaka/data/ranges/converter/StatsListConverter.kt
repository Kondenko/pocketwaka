package com.kondenko.pocketwaka.data.ranges.converter

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel

object StatsListConverter {

    private val statsItemFactory = RuntimeTypeAdapterFactory
            .of(StatsUiModel::class.java)
            .registerSubtype(StatsUiModel.Info::class.java)
            .registerSubtype(StatsUiModel.Stats::class.java)
            .registerSubtype(StatsUiModel.BestDay::class.java)

    private val gson = GsonBuilder()
            .serializeNulls()
            .registerTypeAdapterFactory(statsItemFactory)
            .create()

    private val typeToken = object : TypeToken<List<StatsUiModel>>() {}.type

    @TypeConverter
    @JvmStatic
    fun toJson(statsUiModels: List<StatsUiModel>): String {
        return gson.toJson(statsUiModels, typeToken)
    }

    @TypeConverter
    @JvmStatic
    fun fromJson(statsModelsJson: String): List<StatsUiModel> {
        return gson.fromJson(statsModelsJson, typeToken)
    }

}