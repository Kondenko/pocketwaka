package com.kondenko.pocketwaka.data.ranges.converter

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel

class RangeDomainModelConverter : ModelConverter<Nothing?, StatsDbModel, StatsDbModel> {
    override fun convert(model: StatsDbModel, param: Nothing?): StatsDbModel {
        return model
    }
}