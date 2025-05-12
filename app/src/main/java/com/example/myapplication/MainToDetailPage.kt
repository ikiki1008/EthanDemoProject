package com.example.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.State
import androidx.activity.ComponentActivity

class MainToDetailPage : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title")
        val imageId = intent.getIntExtra("imageId", -1)

        setContent {
            Text("clicked item is ...  $title" )
        }
    }
}