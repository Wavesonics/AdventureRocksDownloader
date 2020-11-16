package com.darkrockstudios.apps.adventurerocksdownloader

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_photos.*

class PhotosActivity: AppCompatActivity()
{
	private val storage by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_photos)
		setSupportActionBar(toolbar)

		if(!storage.getBoolean(Data.KEY_INTRO_SEEN, false))
		{
			startActivity(Intent(this, IntroActivity::class.java))
		}
	}
}