package com.example.bilibili

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.bilibili.ui.theme.BilibiliTheme
import com.example.bilibili.view.ActionTab
import com.example.bilibili.view.BuyTab
import com.example.bilibili.view.CollectTab
import com.example.bilibili.view.ConcernTab
import com.example.bilibili.view.GameTab
import com.example.bilibili.view.HistoryTab
import com.example.bilibili.view.MeTab
import com.example.bilibili.view.PersonTab
import com.example.bilibili.view.RecommendTab
import com.example.bilibili.view.SearchTab
import com.example.bilibili.view.SettingTab
import com.example.bilibili.view.VideoTab
import com.example.bilibili.view.VipTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BilibiliTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var showConcernTab by remember { mutableStateOf(false) }
    var showVipTab by remember { mutableStateOf(false) }
    var showSettingTab by remember { mutableStateOf(false) }
    var showHistoryTab by remember { mutableStateOf(false) }
    var showCollectTab by remember { mutableStateOf(false) }
    var showSearchTab by remember { mutableStateOf(false) }
    var showVideoTab by remember { mutableStateOf(false) }
    var showGameTab by remember { mutableStateOf(false) }
    var showPersonTab by remember { mutableStateOf(false) }
    var currentVideoId by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // 只在不显示子页面时显示底部导航栏
            if (!showConcernTab && !showVipTab && !showSettingTab && !showHistoryTab && !showCollectTab && !showSearchTab && !showVideoTab && !showGameTab && !showPersonTab) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Gray
                ) {
                // 首页
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "首页"
                        )
                    },
                    label = { Text("首页") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6699),
                        selectedTextColor = Color(0xFFFF6699),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                // 关注
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "关注"
                        )
                    },
                    label = { Text("关注") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6699),
                        selectedTextColor = Color(0xFFFF6699),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                // 中间+按钮
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "发布",
                            tint = Color(0xFFFF6699)
                        )
                    },
                    label = { },
                    selected = false,
                    onClick = { /* TODO: 发布功能 */ }
                )

                // 会员购
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "会员购"
                        )
                    },
                    label = { Text("会员购") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6699),
                        selectedTextColor = Color(0xFFFF6699),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                // 我的
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "我的"
                        )
                    },
                    label = { Text("我的") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6699),
                        selectedTextColor = Color(0xFFFF6699),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val context = LocalContext.current

            // 根据状态显示不同的页面
            when {
                showPersonTab -> {
                    PersonTab(
                        context = context,
                        onNavigateBack = { showPersonTab = false }
                    )
                }
                showGameTab -> {
                    GameTab(
                        context = context,
                        searchQuery = searchQuery,
                        onBack = { showGameTab = false }
                    )
                }
                showVideoTab -> {
                    VideoTab(
                        context = context,
                        videoId = currentVideoId,
                        onBack = { showVideoTab = false }
                    )
                }
                showSearchTab -> {
                    SearchTab(
                        context = context,
                        onBack = { showSearchTab = false },
                        onNavigateToGame = { query ->
                            searchQuery = query
                            showGameTab = true
                        }
                    )
                }
                showConcernTab -> {
                    ConcernTab(
                        context = context,
                        onBack = { showConcernTab = false }
                    )
                }
                showVipTab -> {
                    VipTab(
                        context = context,
                        onBack = { showVipTab = false }
                    )
                }
                showSettingTab -> {
                    SettingTab(
                        context = context,
                        onBack = { showSettingTab = false }
                    )
                }
                showHistoryTab -> {
                    HistoryTab(
                        context = context,
                        onNavigateBack = { showHistoryTab = false }
                    )
                }
                showCollectTab -> {
                    CollectTab(
                        context = context,
                        onNavigateBack = { showCollectTab = false }
                    )
                }
                else -> {
                    when (selectedTab) {
                        0 -> RecommendTab(
                            context = context,
                            onNavigateToSearch = { showSearchTab = true },
                            onNavigateToVideo = { videoId ->
                                currentVideoId = videoId
                                showVideoTab = true
                            }
                        )  // 推荐页面
                        1 -> ActionTab(context = context)     // 动态页面
                        2 -> BuyTab(context = context)        // 会员购页面
                        3 -> MeTab(
                            context = context,
                            onNavigateToConcern = { showConcernTab = true },
                            onNavigateToVip = { showVipTab = true },
                            onNavigateToSetting = { showSettingTab = true },
                            onNavigateToHistory = { showHistoryTab = true },
                            onNavigateToCollect = { showCollectTab = true },
                            onNavigateToPerson = { showPersonTab = true }
                        )  // 我的页面
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderTab(name: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "$name\n(待开发)",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray
            )
        }
    }
}