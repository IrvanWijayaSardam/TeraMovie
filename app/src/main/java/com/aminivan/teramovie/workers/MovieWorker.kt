package com.aminivan.teramovie.workers

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
                    val dataMovie = response.body()!!.results
                    MovieDB.databaseWriteExecutor.execute {
                        movieDao.deleteAllMovie()
                        var data = response.body()!!.results
                        for (i in 0 until response.body()!!.results!!.size!!) {
                            movieDao.insert(Movie(0, data?.get(i)!!.title, data?.get(i)!!.releaseDate,data?.get(i)!!.overview))
                        }
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
}
