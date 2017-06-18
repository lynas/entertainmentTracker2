package com.lynas.entertainmenttracker2.activity.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.below
import org.jetbrains.anko.centerHorizontally
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textView

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        relativeLayout {
            textView {
                id = 1
                text = "This application created by"
            }.lparams {
                topMargin = 100
                centerHorizontally()
            }
            textView {
                id = 2
                text = "LynAs Sazzad"
            }.lparams {
                below(1)
                centerHorizontally()
            }
            textView {
                id = 3
                text = "Contact Info"
            }.lparams {
                below(2)
                centerHorizontally()
            }
            textView {
                id = 4
                text = "szlynas@gmail.com"
            }.lparams {
                below(3)
                centerHorizontally()
            }

        }
    }
}
