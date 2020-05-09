package com.kondenko.pocketwaka.domain.summary.model

data class Project(
      val name: String,
      val totalSeconds: Long,
      val isRepoConnected: Boolean = false, // TODO Make sure it is set and works
      val branches: Map<String, Branch>,
      val repositoryUrl: String?
)

interface ProjectInternalListItem

data class Branch(val name: String, val totalSeconds: Long, val commits: List<Commit>?) : ProjectInternalListItem

data class Commit(val hash: String, val message: String, val totalSeconds: Long) : ProjectInternalListItem


// TODO Merge at UI level
infix fun Project.mergeBranches(other: Project): Project {
    require(name == other.name) {
        "Projects are different"
    }
    require(totalSeconds == other.totalSeconds) {
        /* See [StatsEntity.plus(StatsEntity)]  */
        "This project's totalSeconds values should have already been merged when fetching summaries"
    }
    return Project(
          name,
          totalSeconds,
          other.isRepoConnected && isRepoConnected,
          branches.merge(other.branches).also {
              val oldNumberOfCommits = branches.values.sumBy { it.commits?.size ?: 0 }
              val newNumberOfCommits = other.branches.values.sumBy { it.commits?.size ?: 0 }
              val mergedNumberOfCommits = it.values.sumBy { it.commits?.size ?: 0 }
              assert(mergedNumberOfCommits >= oldNumberOfCommits + newNumberOfCommits) { "Size decreased after merging branches" }
          },
          other.repositoryUrl
    )
}

private fun Map<String, Branch>.merge(other: Map<String, Branch>): Map<String, Branch> =
      (keys + other.keys).associateWith {
          setOf(this[it], other[it]).filterNotNull().reduce(Branch::merge)
      }.let { HashMap(it) }

private fun Branch.merge(other: Branch?): Branch {
    require(name == other?.name) { "Branches are different" }
    other ?: return this
    // TODO Don't sum seconds?
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
