package com.ayaanjaved.wednesdaytunes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ayaanjaved.wednesdaytunes.models.ITunesItem

@Dao
interface EntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ITunesItem): Long

    @Query("SELECT * FROM entities WHERE artistName LIKE '%' || :search || '%'")
    fun getList(search: String): List<ITunesItem>

    @Query("SELECT * FROM entities")
    fun getAllList(): List<ITunesItem>
}