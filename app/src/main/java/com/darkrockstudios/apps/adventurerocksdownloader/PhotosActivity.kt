package com.darkrockstudios.apps.adventurerocksdownloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import com.darkrockstudios.apps.adventurerocksdownloader.database.AppDatabase
import com.darkrockstudios.apps.adventurerocksdownloader.database.Photo360
import com.log4k.d
import com.log4k.e
import com.log4k.i
import com.log4k.w
import kotlinx.android.synthetic.main.activity_photos.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.inject

class PhotosActivity: AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_photos)
		setSupportActionBar(toolbar)
	}
}