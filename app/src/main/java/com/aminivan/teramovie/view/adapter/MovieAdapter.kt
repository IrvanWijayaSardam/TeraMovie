package com.aminivan.teramovie.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aminivan.teramovie.database.Movie
import com.aminivan.teramovie.databinding.MovieItemBinding
import com.aminivan.teramovie.helper.MovieDiffCallback

class MovieAdapter() : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private val listMovie = ArrayList<Movie>()

    fun setListMovie(listMovies: List<Movie>) {
        val diffCallback = MovieDiffCallback(this.listMovie, listMovies)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listMovie.clear()
        this.listMovie.addAll(listMovies)
        diffResult.dispatchUpdatesTo(this)
    }

    class MovieViewHolder(private val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie){
            with(binding){
                tvItemTitle.text = movie.title
                tvItemDate.text = movie.date
                tvItemDescription.text = movie.overview
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listMovie.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(listMovie[position])
    }

}