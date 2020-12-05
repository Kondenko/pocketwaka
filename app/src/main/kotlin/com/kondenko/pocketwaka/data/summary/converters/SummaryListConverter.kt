package com.kondenko.pocketwaka.data.summary.converters

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel

object SummaryListConverter {

    private val summaryFactory = RuntimeTypeAdapterFactory
          .of(SummaryUiModel::class.java)
          .registerSubtype(SummaryUiModel.TimeTracked::class.java)
          .registerSubtype(SummaryUiModel.ProjectsTitle::class.java)
          .registerSubtype(SummaryUiModel.ProjectItem::class.java)

    private val gson = GsonBuilder()
          .serializeNulls()
          .registerTypeAdapterFactory(summaryFactory)
          .create()

    private val summaryTypeToken = object : TypeToken<List<SummaryUiModel>>() {}.type

    @TypeConverter
    @JvmStatic
    fun summaryToJson(model: List<SummaryUiModel>): String {
        return gson.toJson(model, summaryTypeToken)
    }

    @TypeConverter
    @JvmStatic
    fun summaryFromJson(statsModelsJson: String): List<SummaryUiModel> {
        return gson.fromJson(statsModelsJson, summaryTypeToken)
    }

}