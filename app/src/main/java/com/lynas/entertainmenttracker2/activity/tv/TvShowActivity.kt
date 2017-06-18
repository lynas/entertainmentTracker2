package com.lynas.entertainmenttracker2.activity.tv

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.EditText
import com.lynas.entertainmenttracker2.ListType
import com.lynas.entertainmenttracker2.R
import com.lynas.entertainmenttracker2.adapter.GeneralAdapter
import com.lynas.entertainmenttracker2.model.TVShow
import com.lynas.entertainmenttracker2.service.TVShowService
import kotlinx.android.synthetic.main.activity_tv_show.*
import org.jetbrains.anko.*
import thebat.lib.validutil.ValidUtils

class TvShowActivity : AppCompatActivity() {

    val tvService = TVShowService()

    lateinit var shows: MutableList<TVShow>
    lateinit var adapter: GeneralAdapter<TVShow>
    lateinit var tvShowName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            showCreateNewTvShow(view)
        }

        tvShowList.setOnItemClickListener { _, _, i, _ ->
            startActivity<TVSeasonActivity>(
                    "tvShowId" to shows[i].id,
                    "tvShowName" to shows[i].tvShowName
            )
        }

        tvShowList.setOnItemLongClickListener { _, _, i, _ ->
            val deleteTvShow = shows[i]
            alert("Delete ${deleteTvShow.tvShowName} ?? Are U Sure ???") {
                positiveButton("Yes") {
                    tvService.deleteTVShow(this@TvShowActivity, deleteTvShow.id) {
                        toast("Delete done")
                        updateView()
                    }
                }
                negativeButton("No") {
                    toast("No")
                }
            }.show()
            true
        }
    }


    private fun showCreateNewTvShow(view: View) {
        alert("Enter TV Show Name") {
            customView {
                relativeLayout {
                    tvShowName = editText {
                    }.lparams {
                        alignParentLeft()
                        alignParentRight()
                    }
                }
            }
            positiveButton("Save") {
                if (!ValidUtils.validateEditTexts(tvShowName)) {
                    return@positiveButton
                }
                tvService.getByName(this@TvShowActivity, tvShowName.text.toString()) {
                    if (it != null) {
                        toast("TV Show with this name already exist")
                    } else {
                        tvService.create(this@TvShowActivity, tvShowName.text.toString()) {
                            Snackbar.make(view, "TV Show creating successful!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            updateView()
                        }
                    }
                }
            }
            negativeButton("Cancel") {

            }
        }.show()

    }

    override fun onStart() {
        super.onStart()
        tvService.getTvShowList(this@TvShowActivity) {
            shows = it.sortedBy { it.tvShowName }.toMutableList()
            adapter = GeneralAdapter(this@TvShowActivity, shows, ListType.TV_SHOW)
            tvShowList.adapter = adapter
        }
    }

    private fun updateView() {
        tvService.getTvShowList(this@TvShowActivity) {
            shows.clear()
            shows.addAll(it.sortedBy { it.tvShowName }.toMutableList())
            adapter.notifyDataSetChanged()
        }

    }
}
