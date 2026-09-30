package com.hasan0525.hteacher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hasan0525.hteacher.data.local.entity.CurriculumUnitEntity
import com.hasan0525.hteacher.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurriculumIndexDao {
    @Query("SELECT * FROM curriculum_units ORDER BY curriculumId, sortOrder, id")
    fun observeUnits(): Flow<List<CurriculumUnitEntity>>

    @Query("SELECT * FROM lessons ORDER BY unitId, sortOrder, id")
    fun observeLessons(): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUnit(unit: CurriculumUnitEntity): Long

    @Update
    suspend fun updateUnit(unit: CurriculumUnitEntity)

    @Delete
    suspend fun deleteUnit(unit: CurriculumUnitEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)
}
