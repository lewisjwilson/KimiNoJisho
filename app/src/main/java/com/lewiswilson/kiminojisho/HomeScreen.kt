package com.lewiswilson.kiminojisho

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.flashcards.FlashcardsHome
import com.lewiswilson.kiminojisho.jishoSearch.SearchPage
import kotlinx.android.synthetic.main.home_screen.*
import java.util.*


class HomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.OverlayTurquoise, true)
        setContentView(R.layout.home_screen)

        item_flashcards.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, FlashcardsHome::class.java)) }
        item_search.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, SearchPage::class.java)) }
        item_mylists.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, MyList::class.java)) }
        item_settings.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, About::class.java)) }

    }
}