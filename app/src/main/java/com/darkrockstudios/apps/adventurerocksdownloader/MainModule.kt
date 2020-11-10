package com.darkrockstudios.apps.adventurerocksdownloader

import androidx.room.Room
import com.beust.klaxon.Klaxon
import com.darkrockstudios.apps.adventurerocksdownloader.database.AppDatabase
import okhttp3.OkHttpClient
import org.koin.dsl.module

val mainModule = module {
	single {
		Room.databaseBuilder(
				get(),
				AppDatabase::class.java,
				"app-database"
							).build()
	}
	single { OkHttpClient() }
	single { Klaxon() }
}