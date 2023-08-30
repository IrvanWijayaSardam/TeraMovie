package com.aminivan.teramovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aminivan.teramovie.database.Movie
import com.aminivan.teramovie.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    var roomRepository: RoomRepository
): ViewModel() {
    suspend fun insert(movie: Movie) = withContext(Dispatchers.IO){
        roomRepository.insert(movie)
    }

    fun getAllMovie(): LiveData<List<Movie>> = roomRepository.getAllMovie()

}