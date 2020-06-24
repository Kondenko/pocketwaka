package com.kondenko.pocketwaka.data.commits.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class CommitDbModel(
      @PrimaryKey
      @ColumnInfo(name = "hash")
      val hash: String,
      @ColumnInfo(name = "project")
      val project: String,
      @ColumnInfo(name = "branch")
      val branch: String,
      @ColumnInfo(name = "message")
      val message: String,
      @ColumnInfo(name = "author_date")
      val authorDate: String,
      @ColumnInfo(name = "total_seconds")
      val totalSeconds: Int
) {
    companion object {
        const val TABLE_NAME = "commits"
    }
}