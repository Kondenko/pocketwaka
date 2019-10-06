package com.kondenko.pocketwaka.data.summary.converters

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.kondenko.pocketwaka.domain.daily.model.ProjectModel
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel

object SummaryListConverter {

    private val summaryFactory = RuntimeTypeAdapterFactory
            .of(SummaryUiModel::class.java)
            .registerSubtype(SummaryUiModel.TimeTracked::class.java)
            .registerSubtype(SummaryUiModel.ProjectsTitle::class.java)
            .registerSubtype(SummaryUiModel.Project::class.java)

    private val projectModelFactory = RuntimeTypeAdapterFactory
            .of(ProjectModel::class.java)
            .registerSubtype(ProjectModel.ProjectName::class.java)
            .registerSubtype(ProjectModel.Branch::class.java)
            .registerSubtype(ProjectModel.Commit::class.java)
            .registerSubtype(ProjectModel.ConnectRepoAction::class.java)
            .registerSubtype(ProjectModel.MoreCommitsAction::class.java)
            .registerSubtype(ProjectModel.NoCommitsLabel::class.java)

    private val gson = GsonBuilder()
            .serializeNulls()
            .registerTypeAdapterFactory(summaryFactory)
            .registerTypeAdapterFactory(projectModelFactory)
            .create()

    private val summaryTypeToken = object : TypeToken<List<SummaryUiModel>>() {}.type

    private val projectsTypeToken = object : TypeToken<List<ProjectModel>>() {}.type

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

    @TypeConverter
    @JvmStatic
    fun projectsToJson(model: List<ProjectModel>): String {
        return gson.toJson(model, projectsTypeToken)
    }

    @TypeConverter
    @JvmStatic
    fun projectsFromJson(model: String): List<ProjectModel> {
        return gson.fromJson(model, projectsTypeToken)
    }

}