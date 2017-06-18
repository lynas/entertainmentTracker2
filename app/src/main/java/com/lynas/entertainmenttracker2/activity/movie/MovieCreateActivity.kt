package com.lynas.entertainmenttracker2.activity.movie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import com.lynas.entertainmenttracker2.calendarToDate
import com.lynas.entertainmenttracker2.service.MovieService
import com.lynas.entertainmenttracker2.validateEditTexts
import org.jetbrains.anko.*
import java.util.*

class MovieCreateActivity : AppCompatActivity() {

    lateinit var saveButton: Button
    lateinit var movieName: EditText
    lateinit var releaseDatePicker: DatePicker
    val movieService = MovieService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        relativeLayout {
            saveButton = button("Save") {
                id = 1
            }.lparams {
                alignParentTop()
                alignParentLeft()
                alignParentRight()
            }


            movieName = editText {
                hint = "Movie Name"
                id = 2
            }.lparams {
                alignParentLeft()
                alignParentRight()
                below(1)
            }


            textView("Set Release Date") {
                id = 3
                textSize = 20F
            }.lparams {
                alignParentLeft()
                alignParentRight()
                below(2)

            }




            releaseDatePicker = datePicker {
                id = 4
            }.lparams {
                below(3)
                alignParentLeft()
                alignParentRight()
            }


        }

        saveButton.onClick {
            if (!validateEditTexts(movieName)) {
                toast("Movie Name must not be empty")
                return@onClick
            }
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, releaseDatePicker.year)
            cal.set(Calendar.MONTH, releaseDatePicker.month)
            cal.set(Calendar.DAY_OF_MONTH, releaseDatePicker.dayOfMonth)
            movieService.create(this@MovieCreateActivity, movieName.text.toString(), calendarToDate(cal).time) {
                toast("Move create success")
                onBackPressed()
            }

        }
    }
}
