package com.example.myapplication.ui.theme.fivetaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch



class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 110.dp)
                ) {
                    val tabs = listOf("홈", "오늘의 취향")
                    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
                    val coroutineScope = rememberCoroutineScope()

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.scrollToPage(index) //클릭시 애니메이션 효과 X
                                    }
                                },
                                text = { Text(
                                    text = title,
                                    color = Color.Black) },
                                selectedContentColor = Color.Black
                            )
                        }
                    }
                    HorizontalPager(state = pagerState) {
                        page ->
                        when (page) {
                            0 -> MainFeed()
                            1 -> TasteFeed()
                        }
                    }
                }
            }
        }
        return view
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasteFeed() {
    val itemList = listOf(
        Pair("https://developer.android.com/images/brand/Android_Robot.png", "안드로이드"),
        Pair("https://upload.wikimedia.org/wikipedia/commons/4/47/React.svg", "리액트"),
        Pair("https://upload.wikimedia.org/wikipedia/commons/1/18/ISO_C%2B%2B_Logo.svg", "C++"),
        Pair("https://upload.wikimedia.org/wikipedia/commons/6/6a/JavaScript-logo.png", "자바스크립트")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(itemList) { (imageUrl, label) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = label,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

}

@Composable
fun MainFeed() {
    val scrollState = rememberLazyListState()
    val data = List(10) { "아이템 ${it + 1}" }

    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
        ) {
            items(data.size) { index ->
                SquareItem(title = data[index])
                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        LazyColumn(state = scrollState) {
            items(100) {
                ImageListItem(it)
            }
        }
    }
}

@Composable
fun SquareItem(title: String) {
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://developer.android.com/images/brand/Android_Robot.png"),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}


@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter("https://developer.android.com/images/brand/Android_Robot.png"),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item $index", style = MaterialTheme.typography.bodyMedium)
    }
}