package com.kondenko.pocketwaka.data.summary.service

import android.content.Context
import com.google.gson.Gson
import com.kondenko.pocketwaka.data.summary.model.server.Summary
import com.kondenko.pocketwaka.jsonToServiceModel
import io.reactivex.Single

class MockSummaryService(val context: Context, val gson: Gson) : SummaryService {

    override fun getSummaries(tokenHeaderValue: String, start: String, end: String, project: String?, branches: String?): Single<Summary> =
            gson.jsonToServiceModel(context, "summary")

}