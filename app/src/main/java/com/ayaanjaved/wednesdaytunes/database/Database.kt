package com.ayaanjaved.wednesdaytunes.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ayaanjaved.wednesdaytunes.models.ITunesItem

@androidx.room.Database(
    entities = [ITunesItem::class],
    version = 1
)
abstract class Database : RoomDatabase(){
    abstract fun getDao(): EntityDao

    companion object{
        private var instance: com.ayaanjaved.wednesdaytunes.database.Database?= null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {instance = it}
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, Database::class.java, "database.db").build()

    }
}