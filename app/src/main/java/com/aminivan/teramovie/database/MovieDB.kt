package com.aminivan.teramovie.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Movie::class], version = 1)
abstract class MovieDB : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        private const val NUMBER_OF_THREADS = 4

        @Volatile
        private var INSTANCE: MovieDB? = null
        val databaseWriteExecutor: ExecutorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS) // using for creating a database but not in main thread
//     that can interrupt our ui, so we have to create database in background

        //     that can interrupt our ui, so we have to create database in background
        fun getDatabase(context: Context): MovieDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(MovieDB::class.java) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDB::class.java,
                    "teramovie_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}