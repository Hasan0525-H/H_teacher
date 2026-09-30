package com.hasan0525.hteacher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hasan0525.hteacher.data.local.entity.AttendanceEntity
import com.hasan0525.hteacher.data.local.entity.GradeRecordEntity
import com.hasan0525.hteacher.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentToolsDao {
    @Query("SELECT * FROM students ORDER BY name COLLATE NOCASE ASC")
    fun observeStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM attendance ORDER BY dateEpochDay DESC, studentId ASC")
    fun observeAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM grade_records ORDER BY createdAt DESC")
    fun observeGradeRecords(): Flow<List<GradeRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStudent(student: StudentEntity): Long

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGradeRecord(record: GradeRecordEntity): Long

    @Delete
    suspend fun deleteGradeRecord(record: GradeRecordEntity)
}
