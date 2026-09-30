package com.hasan0525.hteacher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurriculumDao {
    @Query("SELECT * FROM curricula ORDER BY id DESC")
    fun observeAll(): Flow<List<CurriculumEntity>>

    @Query("SELECT * FROM curricula WHERE subjectId = :subjectId AND gradeId = :gradeId ORDER BY id DESC")
    fun observeBySubjectAndGrade(
        subjectId: Long,
        gradeId: Long
    ): Flow<List<CurriculumEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(curriculum: CurriculumEntity): Long

    @Update
    suspend fun update(curriculum: CurriculumEntity)

    @Delete
    suspend fun delete(curriculum: CurriculumEntity)
}
