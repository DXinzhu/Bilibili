package com.example.bilibili.view

import android.content.Context
import com.example.bilibili.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bilibili.model.User
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.CartoonPresenter
import com.example.bilibili.view.cartoon.components.*
import com.example.bilibili.view.recommend.components.TopBar

/**
 * 动画/番剧页面
 * 按照MVP模式实现，展示番剧内容
 */
@Composable
fun CartoonTab(
    context: Context,
    onTabSelected: (String) -> Unit = {}
) {
    val presenter = remember { CartoonPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var featuredCartoon by remember { mutableStateOf<Video?>(null) }
    var rankingCartoons by remember { mutableStateOf<List<Video>>(emptyList()) }
    var selectedRankingTab by remember { mutableStateOf("国创榜") }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        featuredCartoon = presenter.getFeaturedCartoon()
        rankingCartoons = presenter.getRankingCartoons()
        // 指令5: 记录进入动画频道页面
        BilibiliAutoTestLogger.logAnimationChannelClicked()
        BilibiliAutoTestLogger.logAnimationChannelPageEntered()
        // 记录频道内容加载完成
        BilibiliAutoTestLogger.logAnimationChannelDataLoaded()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部工具栏（固定）
        user?.let { TopBar(it, selectedTab = "动画", onTabSelected = onTabSelected) }

        // 底部滚动内容
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 顶部大图区域
            item {
                featuredCartoon?.let { FeaturedCartoonSection(it) }
            }

            // 导航标签栏
            item {
                CartoonNavigationTabs()
            }

            // 热门排行榜标题
            item {
                RankingHeader(selectedTab = selectedRankingTab, onTabSelected = { selectedRankingTab = it })
            }

            // 排行榜内容
            item {
                RankingContent(rankingCartoons)
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
