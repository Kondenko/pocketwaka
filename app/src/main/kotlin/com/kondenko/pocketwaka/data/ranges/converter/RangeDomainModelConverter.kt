package com.kondenko.pocketwaka.data.ranges.converter

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.data.ranges.repository.StatsDomainModel

class RangeDomainModelConverter : ModelConverter<Nothing?, StatsDto, StatsDomainModel> {
    override fun convert(model: StatsDto, param: Nothing?): StatsDomainModel {
        return model
    }
}