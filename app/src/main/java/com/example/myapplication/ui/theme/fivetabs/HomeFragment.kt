package com.example.myapplication.ui.theme.fivetabs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import com.example.myapplication.domain.CreatorPost
import com.example.myapplication.domain.ProductItem
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapplication.ui.theme.home.MainToDetailPage
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.jvm.java
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.TastePost
import com.example.myapplication.ui.theme.home.HomeFeedViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 2.dp) // 상단 여백을 2dp로 제한
                    ) {
                        val tabs = listOf(R.string.tabs_1, R.string.tabs_2)
                        val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
                        val coroutineScope = rememberCoroutineScope()

                        TabRow(selectedTabIndex = pagerState.currentPage, containerColor = Color.White) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch { pagerState.scrollToPage(index) }
                                    },
                                    text = {
                                        Text(
                                            text = stringResource(id = title),
                                            color = Color.Black
                                        )
                                    }
                                )
                            }
                        }

                        HorizontalPager(state = pagerState) { page ->
                            when (page) {
                                0 -> {
                                    val feedScrollState = rememberLazyListState()
                                    val viewModel: HomeFeedViewModel = hiltViewModel()
                                    ShowMainFeed(feedScrollState, viewModel)
                                }
                                1 -> {
                                    val tasteScrollState = rememberLazyListState()
                                    TasteFeed(tasteScrollState)
                                }
                            }
                        }
                    }
                }
            }
        }
        return view
    }
}


@OptIn(FlowPreview::class)
@Composable
fun ShowMainFeed(scrollState : LazyListState, viewModel: HomeFeedViewModel) {
    val listState = scrollState
    val coroutineScope = rememberCoroutineScope()

    val allPosts = viewModel.allPosts
    val isLoading = viewModel.isLoading
    val endReached = viewModel.endReached
    val showShimmering = viewModel.showShimmering

    // 스크롤 감지 후 추가 로딩
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .map { it ?: 0 }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex >= allPosts.size - 1) {
                    viewModel.loadNextItems()
                }
            }
    }

    //카테고리 피드
    val categoryData = listOf(
        R.string.category_feed_1,R.string.category_feed_2,R.string.category_feed_3,R.string.category_feed_4,
        R.string.category_feed_5,R.string.category_feed_6,R.string.category_feed_7,R.string.category_feed_8,
        R.string.category_feed_9,R.string.category_feed_10
    )

    //카테고리 제목
    val categoryImage = listOf(
        R.drawable.week, R.drawable.clock, R.drawable.findhouse, R.drawable.luck,R.drawable.drawer,
        R.drawable.garage, R.drawable.shampoo, R.drawable.delivery, R.drawable.interior, R.drawable.present
    )

    LazyColumn(state = listState) {
        // 상단 카테고리 LazyRow
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(categoryData.size) { index ->
                    val title = stringResource(id = categoryData[index])
                    val image = categoryImage[index]
                    SquareItem(title = title, imageResId = image, showShimmering = showShimmering)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        // 피드 아이템중 0번째와 1번째를 감지하여 광고 개시
        itemsIndexed(allPosts) { index, post ->
            if (post.isSkeleton || post.post == null) {
                SkeletonItem()
            } else {
                ImageListItem(creatorPost = post.post)
                if (index == 0) ShowPicAds()
                if (index == 1) ShowVidAds()
            }
        }

        // 로딩 중일 때 스켈레톤
        if (isLoading) {
            items(20) {
                SkeletonItem()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListItem(creatorPost: CreatorPost) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val productItems = listOf(
        ProductItem(stringResource(R.string.desk), R.drawable.desktop),
        ProductItem(stringResource(R.string.book_shelf), R.drawable.bookshelf),
        ProductItem(stringResource(R.string.sofa), R.drawable.sofa),
        ProductItem(stringResource(R.string.lamp), R.drawable.lamp),
        ProductItem(stringResource(R.string.table), R.drawable.table_icon),
        ProductItem(stringResource(R.string.mirror_drawers), R.drawable.drawers),
        ProductItem(stringResource(R.string.chair), R.drawable.chair),
        ProductItem(stringResource(R.string.rug), R.drawable.rug),
    )

    val previewItems = productItems.take(4)

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 15.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlideImage(
                    imageModel = creatorPost.pfImage,
                    modifier = Modifier.size(50.dp).aspectRatio(1f).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(creatorPost.id, style = MaterialTheme.typography.bodyMedium)
                    Text(creatorPost.intro, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.follow),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { println("Follow clicked") }
                )
                OptionMenu()
            }
        }

        GlideImage(
            imageModel = creatorPost.postImage,
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
                    text = stringResource(R.string.see_more_items),
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
                            text = stringResource(R.string.tag_item, productItems.size),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { showBottomSheet = false },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
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
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(10))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = stringResource(R.string.price),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            var expanded by remember { mutableStateOf(false) }

                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                                }

                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.report)) },
                                        onClick = {
                                            expanded = false
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


@Composable
fun SkeletonItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .width(100.dp)
                    .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
        )
    }
}

@Composable
fun SquareItem(title: String, imageResId : Int, showShimmering: Boolean) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(70.dp)
            .clickable (enabled = !showShimmering){ //shimmer 효과 중에는 클릭 비활성화
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://ohou.se")
                }
                context.startActivity(intent)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp))
                .placeholder(
                    visible = showShimmering,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            if (!showShimmering) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.height(14.dp).width(60.dp).placeholder(
                visible = showShimmering,
                highlight = PlaceholderHighlight.shimmer(),
                color = Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            if (!showShimmering) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TasteFeed(scrollState: LazyListState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val images = listOf(R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4)
    var selectedImg by remember { mutableStateOf(images.random()) }
    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            selectedImg = images.random() // 스와이프 시 이미지 랜덤하게 배치
            isRefreshing = false
        }
    )

    val allItems = remember { mutableStateListOf<TastePost?>() }
    var isLoading by remember { mutableStateOf(true) }
    var endReached by remember { mutableStateOf(false) }

    // JSON 데이터 로드
    val allJsonPosts by produceState<List<TastePost>>(initialValue = emptyList()) {
        val json = context.assets.open("taste_feed_sample_data.json").bufferedReader().use { it.readText() }
        value = Gson().fromJson(json, object : TypeToken<List<TastePost>>() {}.type)
    }

    // 초기 데이터 로딩
    LaunchedEffect(allJsonPosts) {
        if (allJsonPosts.isNotEmpty() && allItems.isEmpty()) {
            val initialPosts = allJsonPosts.take(8)
            allItems.addAll(initialPosts)
            isLoading = false
        }
    }

    // 무한 스크롤 로직
    LaunchedEffect(scrollState) {
        snapshotFlow { !scrollState.canScrollForward }
            .distinctUntilChanged()
            .collect { isAtBottom ->
                if (isAtBottom && !isLoading && !endReached) {
                    loadMoreData(
                        coroutineScope = coroutineScope,
                        allItems = allItems,
                        allJsonPosts = allJsonPosts,
                        onLoadingChange = { isLoading = it },
                        onEndReached = { endReached = it }
                    )
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = selectedImg),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = stringResource(R.string.come_look),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            val rows = allItems.chunked(2)
            items(rows.size) { index ->
                val rowItems = rows[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEachIndexed { _, post ->
                        Box(modifier = Modifier.weight(1f)) {
                            if (post != null) {
                                TasteGridItem(post = post)
                            } else {
                                SkeletonTasteItem()
                            }
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = Color(0xFF00C7FC) //인디케이터 색상 변경
        )
    }
}

// 추가 데이터 로딩을 위한 함수
private fun loadMoreData(
    coroutineScope: CoroutineScope,
    allItems: SnapshotStateList<TastePost?>,
    allJsonPosts: List<TastePost>,
    onLoadingChange: (Boolean) -> Unit,
    onEndReached: (Boolean) -> Unit
) {
    coroutineScope.launch {
        onLoadingChange(true)

        // 스켈레톤 추가
        repeat(8) { allItems.add(null) }

        delay(300) // 로딩 시간 시뮬레이션

        // 스켈레톤 제거
        repeat(8) { allItems.removeLast() }

        // 다음 데이터 추가
        val currentSize = allItems.count { it != null }
        val nextItems = allJsonPosts.drop(currentSize).take(8)

        if (nextItems.isEmpty()) {
            onEndReached(true)
        } else {
            allItems.addAll(nextItems)
        }

        onLoadingChange(false)
    }
}

@Composable
fun TasteGridItem(post: TastePost, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clip(RoundedCornerShape(8.dp))) {
        Image(
            painter = rememberAsyncImagePainter(post.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(post.webUrl)
                    }
                    context.startActivity(intent)
                },
        )
        Text(
            text = post.title,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SkeletonTasteItem(modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        )
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
                text = { Text(stringResource(R.string.report)) },
                onClick = {
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun ShowPicAds() {
    val adPhotos = listOf(R.drawable.curtain, R.drawable.ebul, R.drawable.pet, R.drawable.winix, R.drawable.sikmul, R.drawable.enuggle)
    val adTitles = listOf(R.string.adtitle1, R.string.adtitle2, R.string.adtitle3, R.string.adtitle4, R.string.adtitle5, R.string.adtitle6)

    LazyRow (
        modifier = Modifier
            .fillMaxWidth()
    ){
        item {
            Text(
                text = stringResource(R.string.today_recommend),
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, bottom = 10.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        items(adPhotos.size) { index ->
            Column(
                modifier = Modifier
                    .width(170.dp)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(id = adPhotos[index]),
                    contentDescription = "Ad photo $index",
                    modifier = Modifier
                        .height(170.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(id = adTitles[index]),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
                Text(
                    text = stringResource(R.string.price),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowVidAds() {
    val videoIds = listOf(
        R.string.video_url,
        R.string.video_url,
        R.string.video_url,
        R.string.video_url
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items (videoIds){ videoId ->
            val url = stringResource(id = videoId)
            VideoPlayer(url)
        }
    }
}

@Composable
fun VideoPlayer(videoId : String) {
    val context = LocalContext.current
    val url = "https://www.youtube.com/embed/$videoId"

    AndroidView(
        factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webChromeClient = WebChromeClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(5.dp))
    )
}