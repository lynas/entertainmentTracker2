package com.lynas.entertainmenttracker2.activity.tv

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType.TYPE_CLASS_NUMBER
import com.lynas.entertainmenttracker2.ListType
import com.lynas.entertainmenttracker2.R
import com.lynas.entertainmenttracker2.adapter.GeneralAdapter
import com.lynas.entertainmenttracker2.model.TVSeason
import com.lynas.entertainmenttracker2.service.TVShowService
import kotlinx.android.synthetic.main.activity_tvseason.*
import org.jetbrains.anko.*
import thebat.lib.validutil.ValidUtils

class TVSeasonActivity : AppCompatActivity() {
    val tvService = TVShowService()
    lateinit var seasonList: MutableList<TVSeason>
    lateinit var selectedTvShowId: String
    lateinit var adapter: GeneralAdapter<TVSeason>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tvseason)
        selectedTvShowId = intent.extras.getString("tvShowId")
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            alertDialog(intent.extras.getString("tvShowName") ?: "Unknown")
        }

        lv_tv_season.onItemClick({ _, _, i, _ ->
            startActivity<TVEpisodeActivity>(
                    "tvSeasonId" to seasonList[i].id
            )
        })

        lv_tv_season.setOnItemLongClickListener { _, _, i, _ ->
            val deleteSeason = seasonList[i]
            alert("Delete ${deleteSeason.seasonName} ??") {
                positiveButton("Yes") {
                    tvService.deleteSeason(this@TVSeasonActivity, deleteSeason.id) {
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
        tvService.getAll(this@TVSeasonActivity) {
            _, sl, _ ->
            seasonList = sl.filter { it.tvShowId == selectedTvShowId }.sortedBy { it.seasonName }.toMutableList()
            adapter = GeneralAdapter(this@TVSeasonActivity, seasonList, ListType.TV_SEASON)
            lv_tv_season.adapter = adapter
        }

    }

    private fun alertDialog(tvShowName: String) {
        alert {
            customView {
                verticalLayout {
                    textView {
                        text = "Add New Season for : $tvShowName"
                        textSize = 20F
                    }
                    val seasonNumber = editText {
                        hint = "Season Number"
                        inputType = TYPE_CLASS_NUMBER
                    }
                    val numberOfEpisode = editText {
                        hint = "Number of Episode"
                        inputType = TYPE_CLASS_NUMBER
                    }
                    positiveButton("Save") {
                        if (seasonList.filter { it.seasonName == "Season ${seasonNumber.text}" }.count() > 0) {
                            toast("Season already exist")
                            return@positiveButton
                        }
                        if (!ValidUtils.validateEditTexts(seasonNumber, numberOfEpisode)) {
                            toast("Field must not be empty")
                            return@positiveButton
                        }
                        tvService.createSeason(this@TVSeasonActivity, selectedTvShowId,
                                seasonNumber.text.toString(), numberOfEpisode.text.toString().toInt()) {
                            refreshListView()
                        }
                    }
                    negativeButton("Cancel") {
                        return@negativeButton
                    }
                }
            }
        }.show()
    }

    private fun refreshListView() {
        tvService.getAll(this@TVSeasonActivity) {
            _, sl, _ ->
            seasonList.clear()
            val sls = sl.filter { it.tvShowId == selectedTvShowId }.sortedBy { it.seasonName }.toMutableList()
            seasonList.addAll(sls)
            adapter.notifyDataSetChanged()

        }

    }

}















