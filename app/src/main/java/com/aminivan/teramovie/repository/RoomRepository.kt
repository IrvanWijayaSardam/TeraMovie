package com.aminivan.teramovie.repository

import androidx.lifecycle.LiveData
import com.aminivan.teramovie.database.Movie
import com.aminivan.teramovie.database.MovieDao
import javax.inject.Inject

class RoomRepository @Inject constructor(private val movieDao: MovieDao) {

    fun insert(movie: Movie) = movieDao.insert(movie)

    fun deleteAllMovie() = movieDao.deleteAllMovie()

    fun getAllMovie() : List<Movie> = movieDao.getAllMovie()

}