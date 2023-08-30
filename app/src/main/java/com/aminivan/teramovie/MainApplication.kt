package com.aminivan.teramovie

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

private const val TAG = "MainApplication"

@HiltAndroidApp
class MainApplication: Application(), Configuration.Provider {


    @Inject
    lateinit var workConfiguration: Configuration

    override fun getWorkManagerConfiguration(): Configuration {
        Log.d(TAG, "getWorkManagerConfiguration: Started")
        return workConfiguration
    }

}