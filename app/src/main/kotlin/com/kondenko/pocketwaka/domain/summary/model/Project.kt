package com.kondenko.pocketwaka.domain.summary.model

data class Project(
      val name: String,
      val totalSeconds: Float,
      val isRepoConnected: Boolean,
      val branches: List<Branch>
)

// TODO Merge at UI level
operator fun Project.plus(other: Project): Project {
    require(name == other.name) {
        "Projects are different"
    }
    require(totalSeconds == other.totalSeconds) {
        /* See [SummaryRepositoryKt.plus(StatsEntity, StatsEntity)]  */
        "Project totalSeconds values should have already been merged when fetching summaries"
    }
    return Project(
          name,
          totalSeconds,
          isRepoConnected && other.isRepoConnected,
          branches.merge(other.branches)
    )
}

private fun List<Branch>.merge(other: List<Branch>): List<Branch> =
      (this + other)
            .groupBy { it.name }
            .map { (_, branches) -> branches.reduce { a, b -> a.merge(b) } }

private fun Branch.merge(other: Branch): Branch {
    require(name == other.name) { "Branches are different" }
    return Branch(
          name,
          totalSeconds + other.totalSeconds,
          commits.mergeCommits(other.commits)
    )
}

private fun List<Commit>.mergeCommits(other: List<Commit>): List<Commit> =
      (this + other)
            .groupBy { it.message }
            .map { (_, branches) -> branches.reduce { a, b -> a.merge(b) } }

private fun Commit.merge(other: Commit): Commit {
    require(hash == other.hash) { "Messages are different" }
    require(message == other.message) { "Messages are different" }
    return Commit(hash, message, totalSeconds + other.totalSeconds)
}
