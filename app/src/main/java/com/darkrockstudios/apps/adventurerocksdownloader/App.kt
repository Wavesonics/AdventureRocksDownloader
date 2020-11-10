package com.darkrockstudios.apps.adventurerocksdownloader

import android.app.Application
import com.log4k.Level
import com.log4k.Log4k
import com.log4k.android.AndroidAppender
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application()
{
	override fun onCreate()
	{
		super.onCreate()

		Log4k.add(Level.Verbose, ".*", AndroidAppender())

		startKoin {
			androidContext(this@App)
			modules(mainModule)
		}
	}
}