package com.example.myapplication

import MainViewModel
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.fivetabs.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("Activity", "MainScreen")

        setContent {
            MyApplicationTheme {
                MainScreen(activity = this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(activity: FragmentActivity) {
    val supportFragmentManager = activity.supportFragmentManager
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    val viewModel: MainViewModel = viewModel()
    val helloText by viewModel.helloText

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { /* open menu */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_text),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(painter = painterResource(id = R.drawable.home), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = { Text(stringResource(R.string.tab1)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(painter = painterResource(id = R.drawable.chat), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = { Text(stringResource(R.string.tab2)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(painter = painterResource(id = R.drawable.shopping_bag), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = { Text(stringResource(R.string.tab3)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    icon = { Icon(painter = painterResource(id = R.drawable.table_icon), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = { Text(stringResource(R.string.tab4)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
                    icon = { Icon(painter = painterResource(id = R.drawable.user), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = { Text(stringResource(R.string.tab5)) }
                )
            }
        }
    ) { innerPadding ->
        // ✅ BoxScope로 감싸야 align 사용 가능
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Column {
                Text(
                    text = helloText,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black)
            }

            when (selectedTabIndex) {
                0 -> FragmentContainer(supportFragmentManager, "home")
                1 -> FragmentContainer(supportFragmentManager, "community")
                2 -> FragmentContainer(supportFragmentManager, "shopping")
                3 -> FragmentContainer(supportFragmentManager, "interior")
                4 -> FragmentContainer(supportFragmentManager, "mypage")
            }
        }
    }
}


@Composable
fun FragmentContainer(
    supportFragmentManager: FragmentManager,
    fragmentTag: String,
) {
    val containerId = remember { android.view.View.generateViewId() }

    AndroidView(
        factory = { context ->
            FrameLayout(context).apply { id = containerId }
        },
        update = {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment: Fragment? = when (fragmentTag) {
                "home" -> HomeFragment()
                "community" -> CommunityFragment()
                "shopping" -> ShoppingFragment()
                "interior" -> InteriorFragment()
                "mypage" -> MyPageFragment()
                else -> null
            }
            fragment?.let {
                transaction.replace(containerId, it, fragmentTag)
                transaction.commitAllowingStateLoss()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val fragmentActivity = LocalContext.current as FragmentActivity
    MyApplicationTheme {
        MainScreen(activity = fragmentActivity)
    }
}