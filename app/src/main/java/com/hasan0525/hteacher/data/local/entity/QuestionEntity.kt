package com.hasan0525.hteacher.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CurriculumEntity::class,
            parentColumns = ["id"],
            childColumns = ["curriculumId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("subjectId"),
        Index("curriculumId"),
        Index("questionType"),
        Index("difficulty")
    ]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val curriculumId: Long? = null,
    val questionType: String,
    val difficulty: String,
    val questionText: String,
    val answerText: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
