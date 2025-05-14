package com.example.myapplication.ui.theme.fivetaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch

class CommunityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp)) {

                    val tabs = listOf("꿀템발견", "#채널", "집들이", "집사진")
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
                                        pagerState.scrollToPage(index)
                                    }
                                },
                                text = {
                                    Text(
                                        text = title,
                                        color = if (pagerState.currentPage == index) Color.Black else Color.Gray
                                    )
                                },
                                selectedContentColor = Color.Black
                            )
                        }
                    }

                    HorizontalPager(state = pagerState) { page ->
                        when (page) {
                            0 -> ItemFoundFeed()
                            1 -> ChannelFeed()
                            2 -> HouseVisitingFeed()
                            3 -> HousePicFeed()
                        }
                    }
                }
            }
        }
        return view
    }
}

@Composable
fun ItemFoundFeed() {
    val scrollState = rememberLazyListState()
    val data = listOf("전체", "인기", "살까말까", "상품후기", "꿀템수다")
    var selectedIndex by remember { mutableStateOf(0) }

    LazyColumn(state = scrollState) {
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 16.dp)
            ) {
                items(data.size) { index ->
                    RoundedTabButton(
                        title = data[index],
                        isSelected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
fun RoundedTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF0D1A45) else Color.White
    val contentColor = if (isSelected) Color.White else Color.Black
    val borderColor = if (isSelected) Color(0xFF0D1A45) else Color.LightGray

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChannelFeed() {
    Text(text = "this is channel feed")
}

@Composable
fun HouseVisitingFeed() {
    Text(text = "this is house visiting feed")
}

@Composable
fun HousePicFeed() {
    Text(text = "this is house pic feed")
}
