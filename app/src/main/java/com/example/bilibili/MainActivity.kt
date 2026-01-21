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
import com.example.bilibili.presentation.action.ActionTab
import com.example.bilibili.presentation.buy.BuyTab
import com.example.bilibili.presentation.collect.CollectTab
import com.example.bilibili.presentation.concern.ConcernTab
import com.example.bilibili.presentation.game.GameTab
import com.example.bilibili.presentation.history.HistoryTab
import com.example.bilibili.presentation.load.LoadTab
import com.example.bilibili.presentation.me.MeTab
import com.example.bilibili.presentation.person.PersonTab
import com.example.bilibili.presentation.recommend.RecommendTab
import com.example.bilibili.presentation.search.SearchTab
import com.example.bilibili.presentation.setting.MessageSettingTab
import com.example.bilibili.presentation.setting.PushSettingTab
import com.example.bilibili.presentation.setting.SettingTab
import com.example.bilibili.presentation.up.UpTab
import com.example.bilibili.presentation.video.VideoTab
import com.example.bilibili.presentation.vip.VipTab
import com.example.bilibili.presentation.common.UnderDevelopmentTab
import com.example.bilibili.presentation.accountprofile.AccountProfileTab

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
    var showMessageSettingTab by remember { mutableStateOf(false) }
    var showPushSettingTab by remember { mutableStateOf(false) }
    var showHistoryTab by remember { mutableStateOf(false) }
    var showCollectTab by remember { mutableStateOf(false) }
    var showSearchTab by remember { mutableStateOf(false) }
    var showVideoTab by remember { mutableStateOf(false) }
    var showGameTab by remember { mutableStateOf(false) }
    var showPersonTab by remember { mutableStateOf(false) }
    var showLoadTab by remember { mutableStateOf(false) }
    var showUpTab by remember { mutableStateOf(false) }
    var showUnderDevelopmentTab by remember { mutableStateOf(false) }
    var showAccountProfileTab by remember { mutableStateOf(false) }
    var currentVideoId by remember { mutableStateOf("") }
    var currentUpMasterId by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // 只在不显示子页面时显示底部导航栏
            if (!showConcernTab && !showVipTab && !showSettingTab && !showMessageSettingTab && !showPushSettingTab && !showHistoryTab && !showCollectTab && !showSearchTab && !showVideoTab && !showGameTab && !showPersonTab && !showLoadTab && !showUpTab && !showUnderDevelopmentTab && !showAccountProfileTab) {
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
                    onClick = { showUnderDevelopmentTab = true }
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
                showUnderDevelopmentTab -> {
                    UnderDevelopmentTab(
                        context = context,
                        onNavigateBack = { showUnderDevelopmentTab = false }
                    )
                }
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
                        onBack = { showGameTab = false },
                        onNavigateToVideo = { videoId ->
                            currentVideoId = videoId
                            showGameTab = false
                            showVideoTab = true
                        }
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
                showUpTab -> {
                    UpTab(
                        context = context,
                        upMasterId = currentUpMasterId,
                        onBack = { showUpTab = false }
                    )
                }
                showConcernTab -> {
                    ConcernTab(
                        context = context,
                        onBack = { showConcernTab = false },
                        onNavigateToUp = { upMasterId ->
                            currentUpMasterId = upMasterId
                            showUpTab = true
                        }
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
                        onBack = { showSettingTab = false },
                        onNavigateToMessageSetting = {
                            showSettingTab = false
                            showMessageSettingTab = true
                        },
                        onNavigateToPushSetting = {
                            showSettingTab = false
                            showPushSettingTab = true
                        },
                        onNavigateToAccountProfile = {
                            showSettingTab = false
                            showAccountProfileTab = true
                        }
                    )
                }
                showMessageSettingTab -> {
                    MessageSettingTab(
                        context = context,
                        onBack = {
                            showMessageSettingTab = false
                            showSettingTab = true
                        }
                    )
                }
                showPushSettingTab -> {
                    PushSettingTab(
                        context = context,
                        onBack = {
                            showPushSettingTab = false
                            showSettingTab = true
                        }
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
                showLoadTab -> {
                    LoadTab(
                        context = context,
                        onNavigateBack = { showLoadTab = false }
                    )
                }
                showAccountProfileTab -> {
                    AccountProfileTab(
                        context = context,
                        onNavigateBack = { showAccountProfileTab = false }
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
                            },
                            onNavigateToUnderDevelopment = { showUnderDevelopmentTab = true },
                            onNavigateToMe = { selectedTab = 3 }
                        )  // 推荐页面
                        1 -> ActionTab(context = context, onNavigateToUnderDevelopment = { showUnderDevelopmentTab = true })     // 动态页面
                        2 -> BuyTab(context = context, onNavigateToUnderDevelopment = { showUnderDevelopmentTab = true })        // 会员购页面
                        3 -> MeTab(
                            context = context,
                            onNavigateToConcern = { showConcernTab = true },
                            onNavigateToVip = { showVipTab = true },
                            onNavigateToSetting = { showSettingTab = true },
                            onNavigateToHistory = { showHistoryTab = true },
                            onNavigateToCollect = { showCollectTab = true },
                            onNavigateToPerson = { showPersonTab = true },
                            onNavigateToLoad = { showLoadTab = true },
                            onNavigateToAccountProfile = { showAccountProfileTab = true },
                            onNavigateToUnderDevelopment = { showUnderDevelopmentTab = true }
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