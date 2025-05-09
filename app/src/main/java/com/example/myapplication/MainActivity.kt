package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.example.myapplication.ui.theme.HomeFragment
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(activity = this)
            }
        }
    }
}

@Composable
fun MainScreen(activity: ComponentActivity) {
    val supportFragmentManager = activity.supportFragmentManager
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
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
                    icon = { Icon(Icons.Filled.Search, contentDescription = "커뮤니티") },
                    label = { Text("커뮤니티") }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = "쇼핑") },
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
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "마이페이지") },
                    label = { Text("마이페이지") }
                )
            }
        }
    ) { innerPadding ->
        // 탭을 누를 시 fragment 소환
        when (selectedTabIndex) {
            0 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "home") {
            }
            1 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "community") {
            }
            2 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "shopping") {
            }
            3 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "interior") {
            }
            4 -> FragmentContainer(supportFragmentManager = supportFragmentManager, fragmentTag = "mypage") {

            }
        }
    }
}

@Composable
fun FragmentContainer(
    supportFragmentManager: FragmentManager,
    fragmentTag: String,
    content: @Composable () -> Unit
) {
    AndroidView(
        factory = { context ->
            FragmentContainerView(context).apply {
                id = android.R.id.content // 임의의 ID 할당
            }
        },
        update = { view ->
            when (fragmentTag) {
                "home" -> {
                    if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(view.id, HomeFragment(), fragmentTag)
                            .commitNow() // 또는 commit()
                    }
                }
                "community" -> {
                    if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(view.id, HomeFragment(), fragmentTag)
                            .commit() // 또는 commit()
                    }
                }
                "shopping" -> {
                    if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(view.id, HomeFragment(), fragmentTag)
                            .commitNow() // 또는 commit()
                    }
                }
                "interior" -> {
                    if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(view.id, HomeFragment(), fragmentTag)
                            .commitNow() // 또는 commit()
                    }
                }
                "mypage" -> {
                    if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(view.id, HomeFragment(), fragmentTag)
                            .commitNow() // 또는 commit()
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        Text("MainScreen Preview")
    }
}