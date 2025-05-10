package com.example.myapplication

import android.content.ContentValues.TAG
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.view.View.generateViewId
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search // Import Search icon
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf // Import mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.example.myapplication.ui.theme.fivetaps.CommunityFragment
import com.example.myapplication.ui.theme.fivetaps.HomeFragment
import com.example.myapplication.ui.theme.fivetaps.InteriorFragment
import com.example.myapplication.ui.theme.fivetaps.MyPageFragment
import com.example.myapplication.ui.theme.fivetaps.ShoppingFragment


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
fun MainScreen(activity : FragmentActivity) {
    val supportFragmentManager = activity.supportFragmentManager
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { isActive = false },
                active = isActive,
                onActiveChange = { isActive = it},
                placeholder = { Text("최저가 상품을 고려 하세요")},
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "geomsaek" )},
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = ""}) {
                            Icon(Icons.Filled.Clear, contentDescription = "datki")
                        }
                    }
                }
            ) {
                //비활성화 시키기
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "홈") },
                    label = { Text("홈") }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = "커뮤니티") },
                    label = { Text("커뮤니티") }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "쇼핑") },
                    label = { Text("쇼핑") }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "인테리어/생활") },
                    label = { Text("인테리어") }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "마이페이지") },
                    label = { Text("마이페이지") }
                )
            }
        }
    ) { innerPadding ->
        // 탭을 누를 시 fragment 소환
        when (selectedTabIndex) {
            0 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "home")
            1 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "community")
            2 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "shopping")
            3 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "interior")
            4 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "mypage")
        }
    }
}

@Composable
fun FragmentContainer(
    supportFragmentManager: FragmentManager,
    fragmentTag: String,
) {
    val containerId = remember { generateViewId() }

    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                id = containerId
            }
        },
        update = { view ->
            val transaction = supportFragmentManager.beginTransaction()

            val fragment = when (fragmentTag) {
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