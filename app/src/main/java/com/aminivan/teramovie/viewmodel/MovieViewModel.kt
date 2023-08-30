package com.aminivan.teramovie.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.aminivan.teramovie.api.ApiService
import com.aminivan.teramovie.model.ResponseGetMovie
import com.aminivan.teramovie.model.ResultsItem
import com.aminivan.teramovie.workers.MovieWorker
import com.aminivan.teramovie.workers.WorkerKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.concurrent.TimeUnit

private const val TAG = "MovieViewModel"

@HiltViewModel
class MovieViewModel @Inject constructor(
    var apiService: ApiService,
    var workManager: WorkManager
) : ViewModel(){

    var movieWorkInfo: LiveData<List<WorkInfo>?> = workManager.getWorkInfosByTagLiveData(WorkerKeys.TAG_SOURCE_DATA)

    fun getMovieWorkStatus(): LiveData<List<WorkInfo>?> = movieWorkInfo


    fun getDataMovie(){
        try {
            doWorkManager()
        }
        catch (e: Exception){
            Log.d(TAG, "getDataMovie: error ${e.message}")
        }
    }

    private fun doWorkManager(){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val periodicWork = OneTimeWorkRequest.Builder(MovieWorker::class.java)
            .setInitialDelay(0, TimeUnit.SECONDS) // Initial delay before the first execution
            .build()

        workManager.enqueue(periodicWork)

        workManager.enqueueUniqueWork(
            WorkerKeys.SYNC_SOURCE_DATA,
            ExistingWorkPolicy.KEEP,
            periodicWork
        )
    }
}