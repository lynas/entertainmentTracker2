package com.lynas.entertainmenttracker2.activity.anime

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType.TYPE_CLASS_NUMBER
import android.view.View
import android.widget.EditText
import com.lynas.entertainmenttracker2.ListType
import com.lynas.entertainmenttracker2.R
import com.lynas.entertainmenttracker2.adapter.GeneralAdapter
import com.lynas.entertainmenttracker2.model.Anime
import com.lynas.entertainmenttracker2.service.AnimeService
import kotlinx.android.synthetic.main.activity_anime_home.*
import org.jetbrains.anko.*
import thebat.lib.validutil.ValidUtils

class AnimeHomeActivity : AppCompatActivity() {

    lateinit var newAnimeName: EditText
    lateinit var lastEpisodeWatched: EditText
    val animeService = AnimeService()
    lateinit var animeList: MutableList<Anime>
    lateinit var adapter: GeneralAdapter<Anime>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_home)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            showCreateNewAnime(view)
        }

        lv_anime_list.setOnItemClickListener {
            _, _, i, _ ->
            handleUpdateAnimeLastWatchCount(i)
        }

        lv_anime_list.setOnItemLongClickListener {
            _, _, i, _ ->
            val deleteAnime = animeList[i]
            alert("Delete ${deleteAnime.animeName} ?? Are U Sure ???") {
                positiveButton("Yes") {
                    animeService.delete(this@AnimeHomeActivity, deleteAnime.id) {
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

    private fun handleUpdateAnimeLastWatchCount(i: Int) {
        alert("Set last watched Episode") {
            customView {
                relativeLayout {
                    button("<") {

                    }.lparams {
                        width = 250
                        alignParentLeft()
                    }.onClick {
                        val previousCount = lastEpisodeWatched.text.toString().toInt()
                        if (previousCount <= 1) {
                            return@onClick
                        }
                        lastEpisodeWatched.setText("${previousCount - 1}")
                    }

                    lastEpisodeWatched = editText("${animeList[i].episodeLastWatched}") {
                        inputType = TYPE_CLASS_NUMBER
                    }.lparams {
                        width = 150
                        alignParentTop()
                        centerInParent()
                    }
                    button(">") {

                    }.lparams {
                        width = 250
                        alignParentRight()
                    }.onClick {
                        val previousCount = lastEpisodeWatched.text.toString().toInt()
                        lastEpisodeWatched.setText("${previousCount + 1}")
                    }
                }
            }
            positiveButton("Update") {
                animeService.update(
                        this@AnimeHomeActivity,
                        animeList[i].id,
                        lastEpisodeWatched.text.toString().toInt()) {
                    refreshListView()
                }
            }
            negativeButton("Cancel") {

            }
        }.show()
    }

    override fun onStart() {
        super.onStart()
        animeService.getAll(this@AnimeHomeActivity) {
            list ->
            animeList = list.toMutableList()
            adapter = GeneralAdapter(this@AnimeHomeActivity, animeList, ListType.ANIME)
            lv_anime_list.adapter = adapter
        }
    }

    private fun showCreateNewAnime(view: View) {
        alert("Enter anime name") {
            customView {
                relativeLayout {
                    newAnimeName = editText {


                    }.lparams {
                        alignParentLeft()
                        alignParentRight()
                    }
                }
            }
            positiveButton("Save") {
                if (!ValidUtils.validateEditTexts(newAnimeName)) {
                    toast("Anime name must not be empty")
                    return@positiveButton
                }
                animeService.create(this@AnimeHomeActivity, newAnimeName.text.toString()) {
                    Snackbar.make(view, "New anime creation successful!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    refreshListView()
                }
            }
            negativeButton("Cancel") {

            }
        }.show()

    }

    private fun refreshListView() {
        animeService.getAll(this@AnimeHomeActivity) {
            list ->
            animeList.clear()
            animeList.addAll(list.toMutableList())
            adapter.notifyDataSetChanged()
        }

    }

}
