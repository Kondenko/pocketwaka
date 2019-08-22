package com.kondenko.pocketwaka.data.ranges.converter

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.ranges.repository.StatsDomainModel

class RangeDomainModelConverter : ModelConverter<Nothing?, StatsDbModel, StatsDomainModel> {
    override fun convert(model: StatsDbModel, param: Nothing?): StatsDomainModel {
        return model
    }
}