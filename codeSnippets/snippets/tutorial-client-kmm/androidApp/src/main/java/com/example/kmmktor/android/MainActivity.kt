package com.example.kmmktor.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kmmktor.Greeting
import android.widget.TextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "Loading..."
        mainScope.launch {
            kotlin.runCatching {
                Greeting().getHtml()
            }.onSuccess {
                tv.text = it
            }.onFailure {
                tv.text = "Error: ${it.localizedMessage}"
            }
        }
    }
}
