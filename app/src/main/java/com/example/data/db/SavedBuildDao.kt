package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedBuildDao {

  @Query("SELECT * FROM saved_builds ORDER BY savedTimestamp DESC")
  fun getAllSavedBuilds(): Flow<List<SavedBuildEntity>>

  @Query("SELECT * FROM saved_builds WHERE id = :id LIMIT 1")
  suspend fun getBuildById(id: String): SavedBuildEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBuild(build: SavedBuildEntity)

  @Update
  suspend fun updateBuild(build: SavedBuildEntity)

  @Delete
  suspend fun deleteBuild(build: SavedBuildEntity)

  @Query("DELETE FROM saved_builds WHERE id = :id")
  suspend fun deleteById(id: String)
}
