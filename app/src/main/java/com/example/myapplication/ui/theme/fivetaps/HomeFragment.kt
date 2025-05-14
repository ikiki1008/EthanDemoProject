package com.example.myapplication.ui.theme.fivetaps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.ui.Alignment
import com.example.myapplication.ui.theme.dataclass.CreatorPost
import com.example.myapplication.ui.theme.dataclass.ProductItem
import kotlinx.coroutines.coroutineScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.myapplication.MainToDetailPage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import kotlin.jvm.java

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
                val scrollState = rememberLazyListState()
                var showBottomSheet by remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState()
                val context = LocalContext.current

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        val tabs = listOf("홈", "오늘의 취향")
                        val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
                        val coroutineScope = rememberCoroutineScope()

                        TabRow(selectedTabIndex = pagerState.currentPage) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch { pagerState.scrollToPage(index) }
                                    },
                                    text = { Text(text = title, color = Color.Black) }
                                )
                            }
                        }

                        HorizontalPager(state = pagerState) { page ->
                            when (page) {
                                0 -> LoadMoreFeed()
                                1 -> TasteFeed()
                            }
                        }
                    }

                    // 고정된 하단 버튼
                    val isAtTop by remember {
                        derivedStateOf { scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset < 10 }
                    }

                    Button(
                        onClick = { showBottomSheet = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                            .height(50.dp)
                    ) {
                        Text(text = if (isAtTop) "+ 글쓰기" else "+")
                    }

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState
                        ) {
                            val options = listOf("사진", "동영상", "커뮤니티")
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                items(options.size) { index ->
                                    val label = options[index]
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable {
                                                showBottomSheet = false
                                                val intent = Intent(context, MainToDetailPage::class.java).apply {
                                                    putExtra("category", label)
                                                }
                                                context.startActivity(intent)
                                            }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Text(text = label)
                                    }
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

@Composable
fun MainFeed(scrollState: LazyListState) {
    val context = LocalContext.current
    val creatorPosts by produceState<List<CreatorPost>>(initialValue = emptyList()) {
        val json = context.assets.open("sample_data.json")
            .bufferedReader().use { it.readText() }
        val gson = Gson()
        value = gson.fromJson(json, object : TypeToken<List<CreatorPost>>() {}.type)
    }

    val data = listOf(
        "8시라이브", "오늘의딜", "바이너리샵", "집들이",
        "패키지할인", "행운출첵", "가드닝게임", "챌린지참여",
        "게러지세일", "오마트", "리모델링", "입주청소"
    )

    LazyColumn(state = scrollState) {
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                items(data.size) { index ->
                    SquareItem(title = data[index])
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        itemsIndexed(creatorPosts) { index, post ->
            ImageListItem(creatorPost = post)
            if (index == 0) ShowPicAds()
            if (index == 1) ShowVidAds()
        }
    }
}

suspend fun loadPostsFromAssets(context: Context): List<CreatorPost> = withContext(Dispatchers.IO) {
    val json = context.assets.open("sample_data.json").bufferedReader().use { it.readText() }
    Gson().fromJson(json, object : TypeToken<List<CreatorPost>>() {}.type)
}

@Composable
fun LoadMoreFeed() {
    val context = LocalContext.current
    val allPosts = remember { mutableStateListOf<CreatorPost>() }
    var isLoading by remember { mutableStateOf(false) }
    var endReached by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 초기 로딩
    LaunchedEffect(Unit) {
        isLoading = true
        val loaded = loadPostsFromAssets(context)
        allPosts.addAll(loaded.take(4))
        isLoading = false
    }

    // 스크롤 감지 후 추가 로딩
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .map { it ?: 0 }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (!isLoading && !endReached && lastVisibleItemIndex >= allPosts.size - 1) {
                    coroutineScope.launch {
                        isLoading = true
                        delay(500) // 부드러운 UX를 위한 로딩 시간
                        val loaded = loadPostsFromAssets(context)
                        val next = loaded.drop(allPosts.size).take(4)
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

    LazyColumn(state = listState) {
        items(allPosts) { post ->
            ImageListItem(creatorPost = post)
        }

        if (isLoading) {
            items(4) {
                SkeletonItem()
            }
        }
    }
}

@Composable
fun ImageListItem(creatorPost: CreatorPost) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(creatorPost.pfImage),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(creatorPost.id, style = MaterialTheme.typography.bodyLarge)
                Text(creatorPost.intro, style = MaterialTheme.typography.bodySmall)
            }
        }

        Image(
            painter = rememberAsyncImagePainter(creatorPost.postImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(creatorPost.postText, style = MaterialTheme.typography.bodyMedium)
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
                text = "평수별 인기 집들이 보러 오세요",
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
                                text = "인기 집들이 #${index + 1}",
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ImageListItem(creatorPost: CreatorPost) {
//    val productItems = listOf(
//        ProductItem("페이브 선반", R.drawable.ic_launcher_background),
//        ProductItem("리버서블 책상", R.drawable.ic_launcher_background),
//        ProductItem("모던 책장", R.drawable.ic_launcher_background),
//        ProductItem("럭셔리 소파", R.drawable.ic_launcher_background),
//        ProductItem("빈티지 램프", R.drawable.ic_launcher_background),
//        ProductItem("우드 테이블", R.drawable.ic_launcher_background),
//        ProductItem("거울 서랍장", R.drawable.ic_launcher_background),
//        ProductItem("의자", R.drawable.ic_launcher_background),
//        ProductItem("러그", R.drawable.ic_launcher_background),
//        ProductItem("장식장", R.drawable.ic_launcher_background)
//    )
//
//    val previewItems = productItems.take(4)
//    var showBottomSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState()
//    val context = LocalContext.current
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 15.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Image(
//                    painter = rememberAsyncImagePainter(creatorPost.pfImage),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(RoundedCornerShape(50))
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Column {
//                    Text(creatorPost.id, style = MaterialTheme.typography.bodyMedium)
//                    Text(creatorPost.intro, style = MaterialTheme.typography.bodySmall)
//                }
//            }
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(
//                    text = "팔로우",
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier
//                        .padding(end = 8.dp)
//                        .clickable { println("팔로우 버튼 클릭됨") }
//                )
//                OptionMenu()
//            }
//        }
//
//        // 게시글 이미지 (웹 URL로부터 로드)
//        Image(
//            painter = rememberAsyncImagePainter(creatorPost.postImage),
//            contentDescription = "Post Image",
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f),
//            contentScale = ContentScale.Crop
//        )
//
//        Text(
//            text = creatorPost.postText,
//            style = MaterialTheme.typography.bodySmall,
//            modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 10.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            previewItems.forEach { item ->
//                Image(
//                    painter = painterResource(id = item.imageId),
//                    contentDescription = item.title,
//                    modifier = Modifier
//                        .size(50.dp)
//                        .padding(end = 3.dp)
//                        .clip(RoundedCornerShape(5.dp))
//                        .clickable {
//                            val intent = Intent(context, MainToDetailPage::class.java).apply {
//                                putExtra("title", item.title)
//                                putExtra("imageId", item.imageId)
//                            }
//                            context.startActivity(intent)
//                        },
//                    contentScale = ContentScale.Crop
//                )
//            }
//
//            if (productItems.size > 4) {
//                Text(
//                    text = "상품 더보기 >",
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.bodySmall,
//                    modifier = Modifier
//                        .clickable { showBottomSheet = true }
//                        .padding(start = 12.dp)
//                )
//            }
//        }
//
//        if (showBottomSheet) {
//            ModalBottomSheet(
//                onDismissRequest = { showBottomSheet = false },
//                sheetState = sheetState
//            ) {
//                Column(
//                    Modifier
//                        .fillMaxWidth()
//                        .padding(1.dp)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 1.dp)
//                    ) {
//                        Text(
//                            text = "태그 상품 (${productItems.size})",
//                            style = MaterialTheme.typography.titleMedium,
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                        IconButton(
//                            onClick = { showBottomSheet = false },
//                            modifier = Modifier.align(Alignment.CenterEnd)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Close,
//                                contentDescription = "닫기"
//                            )
//                        }
//                    }
//
//                    productItems.forEach { item ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(10.dp),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                Image(
//                                    painter = painterResource(id = item.imageId),
//                                    contentDescription = item.title,
//                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10))
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Column {
//                                    Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
//                                    Text(text = "390,000", style = MaterialTheme.typography.bodySmall)
//                                }
//                            }
//
//                            var expanded by remember { mutableStateOf(false) }
//
//                            Box {
//                                IconButton(onClick = { expanded = true }) {
//                                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
//                                }
//
//                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                                    DropdownMenuItem(
//                                        text = { Text("신고하기") },
//                                        onClick = {
//                                            expanded = false
//                                            println("신고하기 클릭됨")
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


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
        "LFsS-9lT0Rk",
        "LFsS-9lT0Rk",
        "LFsS-9lT0Rk",
        "LFsS-9lT0Rk"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().height(450.dp).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items (videoIds){ videoId ->
            VideoPlayer(videoId)
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