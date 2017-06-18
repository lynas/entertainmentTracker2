package com.lynas.entertainmenttracker2.activity.tv

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.lynas.entertainmenttracker2.ListType
import com.lynas.entertainmenttracker2.adapter.GeneralAdapter
import com.lynas.entertainmenttracker2.model.TVEpisode
import com.lynas.entertainmenttracker2.service.FireBaseService
import com.lynas.entertainmenttracker2.service.TVShowService
import org.jetbrains.anko.*

class TVEpisodeActivity : AppCompatActivity() {

    val tvService = TVShowService()
    lateinit var episodeList: MutableList<TVEpisode>
    lateinit var listView: ListView
    lateinit var adapter: GeneralAdapter<TVEpisode>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        relativeLayout {
            listView = listView {

            }.lparams {
                alignParentTop()
                alignParentBottom()
                alignParentLeft()
                alignParentRight()
            }
        }
        doThis()

        listView.onItemClick({ _, _, i, _ ->
            val tvEpisodeTapped = episodeList[i]
            tvService.updateEpisode(this@TVEpisodeActivity, tvEpisodeTapped.id, !tvEpisodeTapped.isWatched) {
                tvEpisodeTapped.apply {
                    isWatched = !tvEpisodeTapped.isWatched
                }
                episodeList.clear()
                doThat()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun doThis() {
        tvService.getAll(applicationContext) {
            _, _, el ->
            episodeList = el.filter { it.tvSeasonId == intent.extras.getString("tvSeasonId") }.toMutableList()
            adapter = GeneralAdapter(this@TVEpisodeActivity, episodeList, ListType.TV_EPISODE)
            listView.adapter = adapter
        }
    }

    private fun doThat() {
        tvService.getAll(applicationContext) {
            _, _, el ->
            val items = el.filter { it.tvSeasonId == intent.extras.getString("tvSeasonId") }.toMutableList()
            episodeList.addAll(items)
        }
    }
}
