package com.hasan0525.hteacher.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "portfolio_attachments",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["portfolioItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("portfolioItemId")]
)
data class PortfolioAttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val portfolioItemId: Long,
    val fileName: String,
    val mimeType: String,
    val localPath: String,
    val createdAt: Long = System.currentTimeMillis()
)
