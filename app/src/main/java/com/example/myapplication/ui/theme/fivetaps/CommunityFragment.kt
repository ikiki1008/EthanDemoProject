package com.example.myapplication.ui.theme.fivetaps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

class CommunityFragment : Fragment() {
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                Log.d("Activity", "this is community screen")
                Text("this is a text 22222")
            }
        }
        return view
    }
}