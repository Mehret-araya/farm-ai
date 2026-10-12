package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.FarmDao
import com.example.data.local.entity.DiagnosticRecordEntity
import com.example.data.local.entity.FarmTaskEntity
import com.example.data.local.entity.FarmerProfileEntity
import com.example.data.local.entity.SavedAdviceEntity
import com.example.data.local.entity.TrackedCropEntity

@Database(
  entities = [
    FarmerProfileEntity::class,
    TrackedCropEntity::class,
    FarmTaskEntity::class,
    SavedAdviceEntity::class,
    DiagnosticRecordEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun farmDao(): FarmDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "farmai_database.db"
        )
          .fallbackToDestructiveMigration(true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
