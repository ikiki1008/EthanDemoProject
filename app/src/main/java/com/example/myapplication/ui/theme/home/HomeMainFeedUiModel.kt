package com.example.myapplication.ui.theme.home

import com.example.myapplication.domain.CreatorPost

data class HomeMainFeedUiModel(
    val post: CreatorPost?,
    val isSkeleton: Boolean = false
)