package com.hasan0525.hteacher.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "curriculum_units",
    foreignKeys = [
        ForeignKey(
            entity = CurriculumEntity::class,
            parentColumns = ["id"],
            childColumns = ["curriculumId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("curriculumId")]
)
data class CurriculumUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val curriculumId: Long,
    val title: String,
    val sortOrder: Int = 0
)
