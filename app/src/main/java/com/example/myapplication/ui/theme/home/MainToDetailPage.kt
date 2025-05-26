package com.example.myapplication.ui.theme.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text

class MainToDetailPage : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title")
        val imageId = intent.getIntExtra("imageId", -1)

        setContent {
            Text("clicked item is ...  $title")
        }
    }
}