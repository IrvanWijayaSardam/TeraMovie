package com.aminivan.teramovie.api



import com.aminivan.teramovie.model.ResponseGetMovie
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("3/discover/movie")
    suspend fun getAllMovie(
        @Query("api_key") apikey: String = "bf24ed5793db181903bc5570cca59115"
    ) : Response<ResponseGetMovie>

}
