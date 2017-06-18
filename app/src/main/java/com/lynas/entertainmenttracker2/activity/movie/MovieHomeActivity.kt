package com.lynas.entertainmenttracker2.activity.movie

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.lynas.entertainmenttracker2.ListType
import com.lynas.entertainmenttracker2.R
import com.lynas.entertainmenttracker2.adapter.GeneralAdapter
import com.lynas.entertainmenttracker2.model.Movie
import com.lynas.entertainmenttracker2.service.MovieService
import kotlinx.android.synthetic.main.activity_movie_home.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MovieHomeActivity : AppCompatActivity() {

    lateinit var movies: MutableList<Movie>
    lateinit var adapter: GeneralAdapter<Movie>

    val movieService = MovieService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_home)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { _ ->
            startActivity<MovieCreateActivity>()
        }


        movie_list.setOnItemClickListener { _, _, i, _ ->
            val movieTapped = movies[i]
            movieService.update(this@MovieHomeActivity, movieTapped.id, !movieTapped.watched) {
                refreshListView()
            }
        }

        movie_list.setOnItemLongClickListener { _, _, i, _ ->
            val deleteMovie = movies[i]
            alert("Delete ${deleteMovie.name} ??") {
                positiveButton("Yes") {
                    movieService.delete(this@MovieHomeActivity, deleteMovie.id) {
                        refreshListView()
                    }
                }
                negativeButton("No") {
                    toast("No")
                }
            }.show()
            true
        }

    }

    override fun onStart() {
        super.onStart()
        movieService.getAll(this@MovieHomeActivity) {
            movieList ->
            movies = movieList.filter { true }.toMutableList()
            adapter = GeneralAdapter(this@MovieHomeActivity, movies, ListType.MOVIE)
            movie_list.adapter = adapter
        }
    }

    private fun refreshListView() {
        movieService.getAll(this@MovieHomeActivity) {
            movieList ->
            movies.clear()
            movies.addAll(movieList)
            adapter.notifyDataSetChanged()
        }

    }

}
