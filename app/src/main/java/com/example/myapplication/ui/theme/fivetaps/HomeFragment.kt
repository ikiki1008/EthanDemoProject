package com.example.myapplication.ui.theme.fivetaps

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.ui.Alignment
import com.example.myapplication.CreatorPost
import com.example.myapplication.ProductItem
import kotlinx.coroutines.coroutineScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.MainToDetailPage

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
                        .padding(top = 5.dp)
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

@Composable
fun MainFeed() {
    val scrollState = rememberLazyListState()
    val creatorPosts = listOf(
        CreatorPost(
            pfImage = R.drawable.ic_launcher_background,
            id = "d.alsom",
            intro = "달솜/알록달록 파스텔톤 집꾸미기",
            postImage = R.drawable.ic_launcher_background,
            postText = "오늘은 엉망진창으로 쌍아둔 짐 정리도 하고" +
                    "침구 정리를 좀 해야할 거 같아요." +
                    "벌써부터 슬쩍 귀찮음이 몰려오지만 ㅋㅋ" +
                    "열심히 이겨내 보겠습니다!"
        ),
        CreatorPost(
            pfImage = R.drawable.ic_launcher_background,
            id = "bludia_nw",
            intro = "오늘의집 스페셜 크리에이터 유튜버",
            postImage = R.drawable.ic_launcher_background,
            postText = "#오감리뷰 프로그램을 통해 제품을 제공받아 직접 사용하고 스타일링한 후기 입니다." +
                    "오늘의 집 레이어 수납장겸 장식장겸 책장겸? 어쩌구저쩌구 집에 가고 싶네"
        ),
        CreatorPost(
            pfImage = R.drawable.ic_launcher_background,
            id = "bludia_nw",
            intro = "오늘의집 스페셜 크리에이터 유튜버",
            postImage = R.drawable.ic_launcher_background,
            postText = "#오감리뷰 프로그램을 통해 제품을 제공받아 직접 사용하고 스타일링한 후기 입니다." +
                    "오늘의 집 레이어 수납장겸 장식장겸 책장겸? 어쩌구저쩌구 집에 가고 싶네"
        ),
        CreatorPost(
            pfImage = R.drawable.ic_launcher_background,
            id = "bludia_nw",
            intro = "오늘의집 스페셜 크리에이터 유튜버",
            postImage = R.drawable.ic_launcher_background,
            postText = "#오감리뷰 프로그램을 통해 제품을 제공받아 직접 사용하고 스타일링한 후기 입니다." +
                    "오늘의 집 레이어 수납장겸 장식장겸 책장겸? 어쩌구저쩌구 집에 가고 싶네"
        ),
        CreatorPost(
            pfImage = R.drawable.ic_launcher_background,
            id = "bludia_nw",
            intro = "오늘의집 스페셜 크리에이터 유튜버",
            postImage = R.drawable.ic_launcher_background,
            postText = "#오감리뷰 프로그램을 통해 제품을 제공받아 직접 사용하고 스타일링한 후기 입니다." +
                    "오늘의 집 레이어 수납장겸 장식장겸 책장겸? 어쩌구저쩌구 집에 가고 싶네"
        )
    )

    LazyColumn(state = scrollState) {
        items(creatorPosts) { post ->
            ImageListItem(creatorPost = post)
        }
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
fun SquareItem(title: String) {
    Column(
        modifier = Modifier.width(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListItem(creatorPost: CreatorPost) {
    val productItems = listOf(
        ProductItem("페이브 선반", R.drawable.ic_launcher_background),
        ProductItem("리버서블 책상", R.drawable.ic_launcher_background),
        ProductItem("모던 책장", R.drawable.ic_launcher_background),
        ProductItem("럭셔리 소파", R.drawable.ic_launcher_background),
        ProductItem("빈티지 램프", R.drawable.ic_launcher_background),
        ProductItem("우드 테이블", R.drawable.ic_launcher_background),
        ProductItem("거울 서랍장", R.drawable.ic_launcher_background),
        ProductItem("의자", R.drawable.ic_launcher_background),
        ProductItem("러그", R.drawable.ic_launcher_background),
        ProductItem("장식장", R.drawable.ic_launcher_background)
    )

    val previewItems = productItems.take(4)
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = creatorPost.pfImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(creatorPost.id, style = MaterialTheme.typography.bodyMedium)
                    Text(creatorPost.intro, style = MaterialTheme.typography.bodySmall)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "팔로우",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { println("팔로우 버튼 클릭됨") }
                )
                OptionMenu()
            }
        }

        Image(
            painter = painterResource(id = creatorPost.postImage),
            contentDescription = "Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        Text(
            text = creatorPost.postText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            previewItems.forEach { item ->
                Image(
                    painter = painterResource(id = item.imageId),
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 3.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .clickable {
                            //각 이미지 클릭 시 새 화면 이동
                            val intent = Intent(context, MainToDetailPage::class.java).apply {
                                putExtra("title", item.title)
                                putExtra("imageId", item.imageId)
                            }
                            context.startActivity(intent)
                        },
                    contentScale = ContentScale.Crop
                )
            }

            if (productItems.size > 4) {
                Text(
                    text = "상품 더보기 >",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .clickable { showBottomSheet = true }
                        .padding(start = 12.dp)
                )
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 1.dp)
                    ) {
                        Text(
                            text = "태그 상품 (${productItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { showBottomSheet = false },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기"
                            )
                        }
                    }

                    productItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = item.imageId),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "390,000", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            var expanded by remember { mutableStateOf(false) }

                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                                }

                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text("신고하기") },
                                        onClick = {
                                            expanded = false
                                            println("신고하기 클릭됨")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//report btn
@Composable
fun OptionMenu() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = {expanded = true}) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
        }

        DropdownMenu(expanded = expanded,
            onDismissRequest = {expanded = false}) {
            DropdownMenuItem(
                text = { Text("신고하기") },
                onClick = {
                    expanded = false
                    println("신고하기 클릭됐음")
                }
            )
        }
    }
}
