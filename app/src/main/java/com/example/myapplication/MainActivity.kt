package com.example.myapplication

import android.os.Bundle
import android.view.View.generateViewId
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
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
fun MainScreen(activity : FragmentActivity) {
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
    AndroidView(
        factory = { context ->
            // You don't need to create FragmentContainerView here.  Just a simple FrameLayout.
            android.widget.FrameLayout(context).apply {
                id = generateViewId()
            }
        },
        update = { view ->
            val transaction = supportFragmentManager.beginTransaction()
            val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
            val viewId = view.id

            if (existingFragment == null) {
                val fragment = when (fragmentTag) {
                    "home" -> HomeFragment()
                    "community" -> HomeFragment()
                    "shopping" -> HomeFragment()
                    "interior" -> HomeFragment()
                    "mypage" -> HomeFragment()
                    else -> null
                }

                fragment?.let {
                    transaction.add(viewId, it, fragmentTag)
                    transaction.commit()
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        Text("MainScreen")
    }
}