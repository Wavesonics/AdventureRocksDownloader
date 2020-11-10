package com.darkrockstudios.apps.adventurerocksdownloader.database

import android.net.Uri
import android.os.Environment
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.log4k.e
import java.io.File

@Entity
data class Photo360(
		@PrimaryKey val id: Int,
		@ColumnInfo val title: String = "",
		@ColumnInfo val description: String = "",
		@ColumnInfo val date_taken: String = "",
		@ColumnInfo val location: String = "",
		@ColumnInfo val map: String = "",
		@ColumnInfo val photo_url: String = "",
		@ColumnInfo val preview: String = "",
		@ColumnInfo val config_file: String = ""
				   )
{
	private fun fileName(): String
	{
		val photoUri = Uri.parse(photo_url)
		return photoUri.lastPathSegment ?: throw IllegalArgumentException("photo URI invalid")
	}

	fun file(): File
	{
		val photosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
		val arDir = File(photosDir, "AdventureRocks")
		if(!arDir.exists())
		{
			if(!arDir.mkdir()) e("Failed to make AR directory")
		}
		val fileName = "${id}_${fileName()}"
		return File(arDir, fileName)
	}

	fun isDownloaded(): Boolean = file().exists()
}