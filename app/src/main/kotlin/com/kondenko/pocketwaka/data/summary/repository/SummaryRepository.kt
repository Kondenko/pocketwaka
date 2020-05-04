package com.kondenko.pocketwaka.data.summary.repository

import com.kondenko.pocketwaka.data.ContinuousCacheBackedRepository
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity
import com.kondenko.pocketwaka.data.summary.dao.SummaryDao
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.GrandTotal
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.DateRangeString
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.toObservable

class SummaryRepository(
      summaryService: SummaryService,
      private val summaryDao: SummaryDao,
      workerScheduler: Scheduler,
      reduceModels: (Params, SummaryDbModel, SummaryDbModel) -> SummaryDbModel
) : ContinuousCacheBackedRepository<SummaryRepository.Params, SummaryData, SummaryDbModel>(
      workerScheduler = workerScheduler,
      serverDataProvider = { (tokenHeader, _, range, project, branches): Params ->
          summaryService.getSummaries(tokenHeader, range.start, range.end, project, branches)
                .flatMap {
                    it.summaryData
                          .toObservable()
                          .reduce { t1: SummaryData?, t2: SummaryData -> t1 + t2 }
                          .toSingle()
                }
      },
      continuousCachedDataProvider = {
          it.dateRange.run {
              summaryDao
                    .getSummaries(start.toEpochDay(), end.toEpochDay())
                    .flatMapObservable { it.toObservable() }
          }
      },
      reduceModels = reduceModels
) {

    data class Params(
          val tokenHeader: String,
          val dateRange: DateRange,
          val dateRangeString: DateRangeString,
          val project: String? = null,
          val branches: String? = null
    )

    override fun cacheData(data: SummaryDbModel) = summaryDao.cacheSummary(data)

    override fun setIsFromCache(model: SummaryDbModel, isFromCache: Boolean): SummaryDbModel = model.copy(isFromCache = isFromCache)

}

// TODO Check range usage for correctness
private operator fun SummaryData?.plus(other: SummaryData): SummaryData {
    this ?: return other
    return SummaryData(
          range,
          grandTotal + other.grandTotal,
          projects.merge(other.projects)
    )
}

private operator fun GrandTotal?.plus(other: GrandTotal): GrandTotal {
    this ?: return other
    return GrandTotal(totalSeconds + other.totalSeconds)
}

private fun List<StatsEntity>?.merge(other: List<StatsEntity>): List<StatsEntity> {
    this ?: return other
    return (this + other)
          .groupBy { it.name }
          .map { (_, entities) -> entities.reduce(StatsEntity::plus) }
}

private operator fun StatsEntity?.plus(other: StatsEntity): StatsEntity {
    this ?: return other
    require(this.name == other.name) { "Entities are different" }
    return StatsEntity(name, totalSeconds + other.totalSeconds, null)
}