package com.hasan0525.hteacher.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "portfolio_items",
    indices = [Index("category")]
)
data class PortfolioItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val title: String,
    val description: String = "",
    val occurredAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
