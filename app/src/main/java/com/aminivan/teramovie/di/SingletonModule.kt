package com.aminivan.teramovie.di

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aminivan.teramovie.api.ApiService
import com.aminivan.teramovie.database.MovieDB
import com.aminivan.teramovie.database.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    private val interceptor: HttpLoggingInterceptor
        get(){
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level  = HttpLoggingInterceptor.Level.BODY
            }
        }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .addInterceptor(interceptor).build()

    @Singleton
    @Provides
    fun provideMasterService(): ApiService =
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)


    @Singleton
    @Provides
    fun createDB(context: Application): MovieDB =
        MovieDB.getDatabase(context)

    @Singleton
    @Provides
    fun getUsersDao(kelolaDB: MovieDB): MovieDao =
        kelolaDB.movieDao()

    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(workerFactory: HiltWorkerFactory): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}