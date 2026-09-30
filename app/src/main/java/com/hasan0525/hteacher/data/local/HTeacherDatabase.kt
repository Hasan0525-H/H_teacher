package com.hasan0525.hteacher.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hasan0525.hteacher.data.local.dao.CurriculumDao
import com.hasan0525.hteacher.data.local.dao.GradeDao
import com.hasan0525.hteacher.data.local.dao.QuestionDao
import com.hasan0525.hteacher.data.local.dao.SubjectDao
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import com.hasan0525.hteacher.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        GradeEntity::class,
        CurriculumEntity::class,
        QuestionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HTeacherDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun gradeDao(): GradeDao
    abstract fun curriculumDao(): CurriculumDao
    abstract fun questionDao(): QuestionDao

    companion object {
        private const val DATABASE_NAME = "h_teacher.db"

        @Volatile
        private var instance: HTeacherDatabase? = null

        fun getInstance(context: Context): HTeacherDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HTeacherDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
    }
}
