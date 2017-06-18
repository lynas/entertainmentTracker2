package com.lynas.entertainmenttracker2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.lynas.entertainmenttracker2.activity.about.AboutActivity
import com.lynas.entertainmenttracker2.activity.anime.AnimeHomeActivity
import com.lynas.entertainmenttracker2.activity.movie.MovieHomeActivity
import com.lynas.entertainmenttracker2.activity.tv.TvShowActivity
import com.lynas.entertainmenttracker2.service.TVShowService
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.concurrent.fixedRateTimer

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonTvShow.onClick {
            startActivity<TvShowActivity>()
        }

        buttonAnime.onClick {
            startActivity<AnimeHomeActivity>()
        }

        buttonMovie.onClick {
            startActivity<MovieHomeActivity>()
        }
        val toolBar = findViewById(R.id.toolbar) as? Toolbar
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_sync) {
            handleSync()
        } else if (item?.itemId == R.id.action_about) {
            startActivity<AboutActivity>()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleSync() {
        val dialog = progressDialog(message = "Please wait a bit…", title = "Sync in progress")
        dialog.show()
        val fixedRateTimer = fixedRateTimer(name = "hello-timer",
                initialDelay = 100, period = 1000) {
            dialog.incrementProgressBy(2)
        }
        TVShowService().sync(this) {
            dialog.progress = 100
            fixedRateTimer.cancel()
            dialog.hide()
            toast("Sync complete")
        }
    }

}
