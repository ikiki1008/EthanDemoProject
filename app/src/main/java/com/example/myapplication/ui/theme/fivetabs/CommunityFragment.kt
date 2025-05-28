package com.example.myapplication.ui.theme.fivetabs

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.ui.theme.community.CommunityViewModel
import com.example.myapplication.ui.theme.community.PostDetailActivity
import com.example.myapplication.ui.theme.community.PostingActivity
import com.example.myapplication.domain.CommunityPost
import com.skydoves.landscapist.glide.GlideImage
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunityFragment : Fragment() {

    private  lateinit var viewModel : CommunityViewModel

    private val postingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.reloadPostsFromFile()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CommunityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                CommunityScreen(
                    viewModel = viewModel,
                    onPostClick = { openPostingActivity() }
                )
            }
        }
    }

    private fun openPostingActivity() {
        val intent = Intent(requireContext(), PostingActivity::class.java)
        postingLauncher.launch(intent)
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onPostClick: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val posts by viewModel.posts.collectAsState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf(
        R.string.community_tab_1,
        R.string.community_tab_2,
        R.string.community_tab_3,
        R.string.community_tab_4
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = pagerState.currentPage, containerColor = Color.White) {
                tabs.forEachIndexed { index, titleResId ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } },
                        text = {
                            Text(
                                text = stringResource(id = titleResId),
                                color = if (pagerState.currentPage == index) Color.Black else Color.Gray
                            )
                        }
                    )
                }
            }

            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> ItemFoundFeed(viewModel, lazyListState)
                    1 -> ChannelFeed(viewModel)
                    2 -> HouseVisitingFeed()
                    3 -> HousePicFeed()
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 20.dp)
        ) {
            Button(
                onClick = { showBottomSheet = true },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                modifier = Modifier.height(50.dp)
            ) {
                AnimatedContent(targetState = isAtTop, label = "FAB") { top ->
                    Text(
                        text = stringResource(id = if (top) R.string.post_on else R.string.post_off),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                val items = listOf(R.string.picture, R.string.video, R.string.community)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(items.size) { index ->
                        val label = items[index]
                        OutlinedButton(
                            onClick = {
                                showBottomSheet = false
                                if (label == R.string.community) {
                                    onPostClick()
                                }
                            },
                            modifier = Modifier.padding(end = 12.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(text = stringResource(id = label))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ItemFoundFeed(
    viewModel: CommunityViewModel,
    lazyListState: LazyListState
) {
    val stringIds = listOf(
        R.string.all,
        R.string.popular,
        R.string.buy_or_not,
        R.string.item_review,
        R.string.honey_item_comm
    )
    var selectedIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val allPosts by viewModel.posts.collectAsState()
    val showShimmer by remember { viewModel.showShimmering }

    val filteredPosts = when (selectedIndex) {
        2 -> allPosts.filter { it.genre == context.getString(R.string.buy_or_not) }
        3 -> allPosts.filter { it.genre == context.getString(R.string.item_review) }
        4 -> allPosts.filter { it.genre == context.getString(R.string.honey_item_comm) }
        else -> allPosts
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                if (showShimmer) {
                    items(5) {
                        RoundedTabBtnShimer()
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                } else {
                    itemsIndexed(stringIds) { index, id ->
                        val title = stringResource(id = id)
                        RoundedTabButton(
                            title = title,
                            isSelected = selectedIndex == index,
                            onClick = { selectedIndex = index }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }

        if (showShimmer) {
            items(5) {
                PostCardShimmer() // 수직 스크롤용 카드 셰이프는 여기
            }
        } else {
            items(filteredPosts) { post ->
                PostCardShape(post = post)
            }
        }
    }
}

@Composable
fun PostCardShimmer() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
            .shimmer(shimmerInstance)
            .background(Color.LightGray, RoundedCornerShape(10.dp))
    )
}

@Composable
fun RoundedTabBtnShimer() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 36.dp) // 실제 버튼 크기에 맞게
            .clip(RoundedCornerShape(50))
            .shimmer(shimmerInstance) // shimmer는 background보다 앞에 있어야 함
            .background(Color.Gray.copy(alpha = 0.3f)) // shimmer가 보이도록 alpha 낮춤
    )
}

@Composable
fun PostCardShape(post: CommunityPost) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                val intent = Intent(context, PostDetailActivity::class.java).apply {
                    putExtra("postId", post.id)
                    putExtra("title", post.title)
                    putExtra("pfp", post.pfp)
                    putExtra("intro", post.intro)
                    putExtra("post", post.post)
                    putExtra("postPic", post.postPic)
                    putExtra("postGenre", post.genre)
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(contentColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = post.genre,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        color = Color.DarkGray
                    )
                }

                Text(
                    text = post.title,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = post.id,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            GlideImage(
                imageModel = post.postPic,
                contentDescription = null,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(10))
            )
        }
    }
}

@Composable
fun RoundedTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val selectedBackgroundColor = colorResource(id = R.color.ohouse_color)

    val backgroundColor = if (isSelected) selectedBackgroundColor else Color.LightGray
    val contentColor = if (isSelected) Color.White else Color.White
    val borderColor = if (isSelected) Color.Transparent else Color.Transparent

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
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Thin),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChannelFeed(viewModel: CommunityViewModel) {
    val challengeItems = remember { viewModel.loadChallengeDatas() }
    val hashTagItems by viewModel.hashTagDatas.collectAsState()
    val scrollState = rememberLazyListState()
    val showShimmer by remember { viewModel.showShimmering }

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxSize()
    ) {

        item {
            if (showShimmer) {

                // 챌린지 채널 텍스트 shimmer
                ShimmerTextBox(
                    width = 120.dp,
                    height = 18.dp,
                    padding = PaddingValues(16.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) { ChannelCardShimmer() }
                }
            } else {

                Text(
                    text = stringResource(R.string.challenge_channel),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Left,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(challengeItems.chunked(2)) { chunk ->
                        Column(
                            modifier = Modifier.width(300.dp)
                        ) {
                            chunk.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GlideImage(
                                        imageModel = item.pic,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = item.title, fontSize = 16.sp)
                                        Text(
                                            text = item.subTitle,
                                            fontSize = 12.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showShimmer) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ShimmerTextBox(width = 140.dp, height = 18.dp, padding = PaddingValues(16.dp))
            }

            items(2) {
                ShimmerHashTagSection()
            }

        } else {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.popular_channels_now),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Left,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(hashTagItems) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = item.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = item.subText, fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(item.img1, item.img2, item.img3, item.img4).forEach { img ->
                            GlideImage(
                                imageModel = img,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 85.dp, height = 140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ChannelCardShimmer() {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(2) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.width(150.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = 0.3f)))

        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.width(100.dp)
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray.copy(0.3f)))

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()) {
            repeat(4) {
                Box(
                    modifier = Modifier.size(width = 85.dp, height = 140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(0.4f))
                )
            }
        }
    }
}

@Composable
fun ShimmerTextBox(width: Dp, height: Dp, padding: PaddingValues) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    Box(
        modifier = Modifier
            .padding(padding)
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray.copy(0.3f))
            .shimmer(shimmer)
    )
}

@Composable
fun ShimmerHashTagSection() {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shimmer(shimmer)
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(0.3f))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(0.3f))
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .size(width = 85.dp, height = 140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(0.3f))
                )
            }
        }
    }
}


@Composable
fun HouseVisitingFeed() {
    Text("This is house visiting feed")
}

@Composable
fun HousePicFeed() {
    Text("This is house pic feed")
}
