package com.hasan0525.hteacher.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hasan0525.hteacher.data.local.dao.CurriculumDao
import com.hasan0525.hteacher.data.local.dao.GradeDao
import com.hasan0525.hteacher.data.local.dao.PortfolioDao
import com.hasan0525.hteacher.data.local.dao.QuestionDao
import com.hasan0525.hteacher.data.local.dao.SubjectDao
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.PortfolioAttachmentEntity
import com.hasan0525.hteacher.data.local.entity.PortfolioItemEntity
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import com.hasan0525.hteacher.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        GradeEntity::class,
        CurriculumEntity::class,
        QuestionEntity::class,
        PortfolioItemEntity::class,
        PortfolioAttachmentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HTeacherDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun gradeDao(): GradeDao
    abstract fun curriculumDao(): CurriculumDao
    abstract fun questionDao(): QuestionDao
    abstract fun portfolioDao(): PortfolioDao

    companion object {
        private const val DATABASE_NAME = "h_teacher.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS portfolio_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        category TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        occurredAt INTEGER,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_portfolio_items_category
                    ON portfolio_items(category)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS portfolio_attachments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        portfolioItemId INTEGER NOT NULL,
                        fileName TEXT NOT NULL,
                        mimeType TEXT NOT NULL,
                        localPath TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(portfolioItemId)
                            REFERENCES portfolio_items(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_portfolio_attachments_portfolioItemId
                    ON portfolio_attachments(portfolioItemId)
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var instance: HTeacherDatabase? = null

        fun getInstance(context: Context): HTeacherDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HTeacherDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}
