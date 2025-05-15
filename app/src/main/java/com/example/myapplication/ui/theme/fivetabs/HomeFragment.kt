package com.example.myapplication.ui.theme.fivetabs

import android.content.Intent
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
import com.example.myapplication.ui.theme.dataclass.CreatorPost
import com.example.myapplication.ui.theme.dataclass.ProductItem
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapplication.MainToDetailPage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.jvm.java
import androidx.compose.ui.res.stringResource


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
                    Column(modifier = Modifier.fillMaxSize()) {
                        val tabs = listOf(R.string.tabs_1, R.string.tabs_2)
                        val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
                        val coroutineScope = rememberCoroutineScope()
                        val scrollState = rememberLazyListState()

                        TabRow(selectedTabIndex = pagerState.currentPage) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch { pagerState.scrollToPage(index) }
                                    },
                                    text = { Text(text = stringResource(id = title), color = Color.Black) }
                                )
                            }
                        }

                        HorizontalPager(state = pagerState) { page ->
                            when (page) {
                                0 -> ShowMainFeed(scrollState)
                                1 -> TasteFeed()
                            }
                        }
                    }
                }
            }
        }
        return view
    }
}

@Composable
fun ShowMainFeed(scrollState : LazyListState) {
    val context = LocalContext.current
    val allPosts = remember { mutableStateListOf<CreatorPost>() }
    var isLoading by remember { mutableStateOf(false) }
    var endReached by remember { mutableStateOf(false) }
    val listState = scrollState
    val coroutineScope = rememberCoroutineScope()

    val allJsonPosts by produceState<List<CreatorPost>>(initialValue = emptyList()) {
        val json = context.assets.open("sample_data.json")
            .bufferedReader().use { it.readText() }
        value = Gson().fromJson(json, object : TypeToken<List<CreatorPost>>() {}.type)
    }

    //초기 로딩
    LaunchedEffect(Unit) {
        isLoading = true
        allPosts.addAll(allJsonPosts.take(4)) //json 파일에서 우선적으로 4개를 가져와 로드한다
        isLoading = false
    }

    //스크롤 감지 후 추가 로딩
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .map { it ?: 0 }
            .distinctUntilChanged()
            .collect { lastVisibleItemIdex ->
                if (!isLoading && !endReached && lastVisibleItemIdex >= allPosts.size-1) {
                    coroutineScope.launch { //코루틴 스콥으로 다음 화면 런치 시작
                        isLoading = true
                        delay(500)
                        val next = allJsonPosts.drop(allPosts.size).take(4)
                        if (next.isEmpty()) {
                            endReached = true
                        } else {
                            allPosts.addAll(next)
                        }
                        isLoading = false
                    }
                }
            }
    }

    //카테고리 피드
    val categoryData = listOf(
        R.string.category_feed_1,R.string.category_feed_2,R.string.category_feed_3,R.string.category_feed_4,
        R.string.category_feed_5,R.string.category_feed_6,R.string.category_feed_7,R.string.category_feed_8,
        R.string.category_feed_9,R.string.category_feed_10,R.string.category_feed_11,R.string.category_feed_12,
    )

    LazyColumn(state = listState) {
        // 상단 카테고리 LazyRow
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                items(categoryData.size) { index ->
                    val title = stringResource(id = categoryData[index])
                    SquareItem(title = title)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        // 피드 아이템중 0번째와 1번째를 감지하여 광고 개시
        itemsIndexed(allPosts) { index, post ->
            ImageListItem(creatorPost = post)
            if (index == 0) ShowPicAds()
            if (index == 1) ShowVidAds()
        }

        // 로딩 중일 때 스켈레톤
        if (isLoading) {
            items(4) {
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

    // 올바르게 stringResource 사용
    val productItems = listOf(
        ProductItem(stringResource(R.string.fave_shelf), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.desk), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.book_shelf), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.sofa), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.lamp), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.table), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.mirror_drawers), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.chair), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.lug), R.drawable.ic_launcher_background),
        ProductItem(stringResource(R.string.cabinet), R.drawable.ic_launcher_background)
    )

    val previewItems = productItems.take(4)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(creatorPost.pfImage),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(50))
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

        Image(
            painter = rememberAsyncImagePainter(creatorPost.postImage),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        Text(
            text = creatorPost.postText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
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
                    Modifier.fillMaxWidth().padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp)
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
                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10))
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
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasteFeed() {
    val images = listOf(
        R.drawable.pic1,
        R.drawable.pic2,
        R.drawable.pic3,
        R.drawable.pic4,
    )

    var selectedImg by remember { mutableStateOf(images.random()) }
    var isRefreshing by remember { mutableStateOf(false) }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            selectedImg = images.random()
            isRefreshing = false
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 대표 이미지
            Image(
                painter = painterResource(id = selectedImg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(25.dp))

            // 상단 텍스트
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

            // 그리드 피드 (20개)
            val feedItems = (0 until 200).toList()
            val context = LocalContext.current
            feedItems.chunked(2).forEach { rowItems ->
                Row (
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    rowItems.forEach { index ->
                        val imageRes = images[index % images.size]

                        Column (modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(8.dp))) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(8.dp))
                            )
                            Text(
                                text = context.getString(R.string.popular_post_with_rank, index + 1),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                }
            }
            }
        }
    }
}



@Composable
fun SquareItem(title: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(70.dp)
            .clickable { //click -> move to webpage
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse("https://www.google.com")
                }
                context.startActivity(intent)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(5.dp)) //rounded shape of image
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
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
    val adPhotos = List(10) { R.drawable.ic_launcher_background }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        items(adPhotos.size) { index ->
            Image(
                painter = painterResource(id = adPhotos[index]),
                contentDescription = "Ad photo $index",
                modifier = Modifier
                    .size(170.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
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
        modifier = Modifier.fillMaxWidth().height(450.dp).padding(8.dp),
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
        modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(5.dp))
    )
}