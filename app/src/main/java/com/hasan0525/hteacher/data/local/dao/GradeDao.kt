package com.hasan0525.hteacher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades ORDER BY sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<GradeEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(grade: GradeEntity): Long

    @Update
    suspend fun update(grade: GradeEntity)

    @Delete
    suspend fun delete(grade: GradeEntity)
}
