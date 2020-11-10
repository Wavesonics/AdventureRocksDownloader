package com.darkrockstudios.apps.adventurerocksdownloader.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Photo360::class], version = 1)
abstract class AppDatabase: RoomDatabase()
{
	abstract fun photoDao(): Photo360Dao
}
