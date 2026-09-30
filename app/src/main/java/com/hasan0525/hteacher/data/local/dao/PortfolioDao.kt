package com.hasan0525.hteacher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hasan0525.hteacher.data.local.entity.PortfolioAttachmentEntity
import com.hasan0525.hteacher.data.local.entity.PortfolioItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_items ORDER BY createdAt DESC")
    fun observeItems(): Flow<List<PortfolioItemEntity>>

    @Query("SELECT * FROM portfolio_attachments ORDER BY createdAt ASC")
    fun observeAttachments(): Flow<List<PortfolioAttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItem(item: PortfolioItemEntity): Long

    @Update
    suspend fun updateItem(item: PortfolioItemEntity)

    @Delete
    suspend fun deleteItem(item: PortfolioItemEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAttachment(attachment: PortfolioAttachmentEntity): Long

    @Delete
    suspend fun deleteAttachment(attachment: PortfolioAttachmentEntity)
}
