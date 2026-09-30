package com.hasan0525.hteacher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id DESC")
    fun observeAll(): Flow<List<QuestionEntity>>

    @Query(
        """
        SELECT * FROM questions
        WHERE subjectId = :subjectId
        AND (:questionType IS NULL OR questionType = :questionType)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        ORDER BY id DESC
        """
    )
    fun observeFiltered(
        subjectId: Long,
        questionType: String?,
        difficulty: String?
    ): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Update
    suspend fun update(question: QuestionEntity)

    @Delete
    suspend fun delete(question: QuestionEntity)
}
