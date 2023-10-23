package com.aminivan.teramovie.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aminivan.teramovie.R
import com.aminivan.teramovie.api.ApiService
import com.aminivan.teramovie.database.Movie
import com.aminivan.teramovie.database.MovieDB
import com.aminivan.teramovie.database.MovieDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "MovieWorker"
@HiltWorker
class MovieWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val movieDao: MovieDao,
    val apiService: ApiService,
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: executed")
        return try {
            val response = apiService.getAllMovie()
            if (response.isSuccessful) {
                if (response.body()!!.totalResults!! > 0) {
                    val data = response.body()!!.results

                    MovieDB.databaseWriteExecutor.execute {
                        movieDao.deleteAllMovie()
                        for (i in 0 until data!!.size) {
                            movieDao.insert(Movie(0, data[i]!!.title, data[i]!!.releaseDate, data[i]!!.overview))
                        }
                    }

                    // Show notification with data[0]!!.title
                    if (data!!.isNotEmpty()) {
                        showNotification(data[0]!!.title!!)
                    }
                }

                val intent = Intent("data_updated")
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(title: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "MovieNotif"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("New Movie")
            .setContentText(title)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}
