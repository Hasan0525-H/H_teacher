package com.hasan0525.hteacher.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "curricula",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = GradeEntity::class,
            parentColumns = ["id"],
            childColumns = ["gradeId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("subjectId"),
        Index("gradeId")
    ]
)
data class CurriculumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val gradeId: Long,
    val title: String,
    val localFileUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
