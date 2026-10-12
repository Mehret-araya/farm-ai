package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DiagnosticRecordEntity
import com.example.data.local.entity.FarmTaskEntity
import com.example.data.local.entity.FarmerProfileEntity
import com.example.data.local.entity.SavedAdviceEntity
import com.example.data.local.entity.TrackedCropEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {

  // Farmer Profile
  @Query("SELECT * FROM farmer_profile WHERE id = 1 LIMIT 1")
  fun getFarmerProfile(): Flow<FarmerProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: FarmerProfileEntity)

  // Tracked Crops
  @Query("SELECT * FROM tracked_crops ORDER BY plantingDateMillis DESC")
  fun getAllTrackedCrops(): Flow<List<TrackedCropEntity>>

  @Query("SELECT * FROM tracked_crops WHERE id = :cropId LIMIT 1")
  suspend fun getCropById(cropId: Long): TrackedCropEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCrop(crop: TrackedCropEntity): Long

  @Update
  suspend fun updateCrop(crop: TrackedCropEntity)

  @Query("DELETE FROM tracked_crops WHERE id = :cropId")
  suspend fun deleteCropById(cropId: Long)

  // Farm Tasks & Planting Reminders
  @Query("SELECT * FROM farm_tasks ORDER BY isCompleted ASC, id DESC")
  fun getAllTasks(): Flow<List<FarmTaskEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: FarmTaskEntity): Long

  @Update
  suspend fun updateTask(task: FarmTaskEntity)

  @Query("UPDATE farm_tasks SET isCompleted = :isCompleted WHERE id = :taskId")
  suspend fun setTaskCompleted(taskId: Long, isCompleted: Boolean)

  @Query("DELETE FROM farm_tasks WHERE id = :taskId")
  suspend fun deleteTaskById(taskId: Long)

  // Saved Advice & Journal Notes
  @Query("SELECT * FROM saved_advice ORDER BY timestamp DESC")
  fun getAllSavedAdvice(): Flow<List<SavedAdviceEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAdvice(advice: SavedAdviceEntity): Long

  @Query("DELETE FROM saved_advice WHERE id = :adviceId")
  suspend fun deleteAdviceById(adviceId: Long)

  // Diagnostic Records
  @Query("SELECT * FROM diagnostic_records ORDER BY dateRecorded DESC")
  fun getAllDiagnosticRecords(): Flow<List<DiagnosticRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDiagnosticRecord(record: DiagnosticRecordEntity): Long

  @Query("DELETE FROM diagnostic_records WHERE id = :recordId")
  suspend fun deleteDiagnosticRecord(recordId: Long)
}
