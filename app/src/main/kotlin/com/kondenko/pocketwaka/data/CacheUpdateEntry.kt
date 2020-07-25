package com.kondenko.pocketwaka.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kondenko.pocketwaka.data.CacheUpdateEntry.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class CacheUpdateEntry(
      @PrimaryKey
      @ColumnInfo(name = "table_name")
      val tableName: String,
      @ColumnInfo(name = "updated_at")
      val updatedAt: Long
) {
      companion object {
            const val TABLE_NAME = "cache_update"
      }
}