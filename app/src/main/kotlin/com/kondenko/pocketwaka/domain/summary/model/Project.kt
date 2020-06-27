package com.kondenko.pocketwaka.domain.summary.model

import com.kondenko.pocketwaka.utils.WakaLog

data class Project(
      val name: String,
      val totalSeconds: Long,
      val isRepoConnected: Boolean = true,
      val branches: Map<String, Branch>,
      val repositoryUrl: String
)


interface ProjectInternalListItem

data class Branch(val name: String, val totalSeconds: Long, val commits: List<Commit>?) : ProjectInternalListItem

data class Commit(val hash: String, val message: String, val totalSeconds: Long) : ProjectInternalListItem

object NoCommitsLabel : ProjectInternalListItem


infix fun Project.mergeBranches(other: Project): Project {
    require(name == other.name) {
        "Projects are different"
    }
    require(totalSeconds == other.totalSeconds) {
        "This project's totalSeconds values should have already been merged when fetching summaries"
        /* See [StatsEntity.plus(StatsEntity)]  */
    }
    return Project(
          name,
          totalSeconds,
          other.isRepoConnected && isRepoConnected,
          branches.merge(other.branches),
          other.repositoryUrl
    ).also {
        val timeByBranches = it.branches.values.sumBy { it.totalSeconds.toInt() }
        if (it.totalSeconds.toInt() != timeByBranches) {
            WakaLog.w("Project's branches time doesn't sum up to project's total time (branches=$timeByBranches, total=$totalSeconds)")
        }
    }
}

private fun Map<String, Branch>.merge(other: Map<String, Branch>): Map<String, Branch> =
      (keys + other.keys).associateWith {
          setOf(this[it], other[it]).filterNotNull().reduce(Branch::merge)
      }.let { HashMap(it) }

private fun Branch.merge(other: Branch?): Branch {
    require(name == other?.name) { "Branches are different" }
    other ?: return this
    return Branch(name, totalSeconds + other.totalSeconds, commits.mergeCommits(other.commits))
}

private fun List<Commit>?.mergeCommits(other: List<Commit>?): List<Commit> =
      ((this ?: emptyList()) + (other ?: emptyList()))
            .groupBy { it.message }
            .map { (_, branches) -> branches.reduce { a, b -> a.merge(b) } }

private fun Commit.merge(other: Commit): Commit {
    require(hash == other.hash) { "Messages are different" }
    require(message == other.message) { "Messages are different" }
    return Commit(hash, message, totalSeconds + other.totalSeconds)
}
