package com.example.myapplication.ui.theme.home

import com.example.myapplication.ui.theme.dataclass.CreatorPost

data class HomeMainFeedUiModel(
    val post: CreatorPost?,
    val isSkeleton: Boolean = false
)