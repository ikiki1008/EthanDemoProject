package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.fivetabs.*

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
    var isActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { isActive = false },
                active = isActive,
                onActiveChange = { isActive = it },
                placeholder = { Text(stringResource(R.string.search_text)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = null)
                        }
                    }
                }
            ) {
                // 비활성화 시키기
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab1)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                    label = {Text(stringResource(R.string.tab2)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab3)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab4)) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab5)) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
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