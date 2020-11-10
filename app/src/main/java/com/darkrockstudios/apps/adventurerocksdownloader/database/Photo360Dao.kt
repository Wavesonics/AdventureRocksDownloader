package com.darkrockstudios.apps.adventurerocksdownloader.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface Photo360Dao
{
	@Query("SELECT * FROM photo360")
	fun getAll(): List<Photo360>

	@Query("SELECT * FROM photo360")
	fun loadAll(): LiveData<List<Photo360>>

	@Insert
	fun insert(vararg entries: Photo360)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(entries: List<Photo360>)

	@Delete
	fun delete(entry: Photo360)
}