package com.aminivan.teramovie.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movie: Movie)

    @Query("DELETE FROM Movie")
    fun deleteAllMovie()

    @Query("SELECT * FROM Movie ORDER BY id ASC LIMIT 10")
    fun getAllMovie() : LiveData<List<Movie>>

}