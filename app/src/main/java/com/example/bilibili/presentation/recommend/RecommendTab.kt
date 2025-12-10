package com.example.bilibili.presentation.recommend

import android.content.Context
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bilibili.data.model.User
import com.example.bilibili.data.model.Video
import com.example.bilibili.presentation.recommend.RecommendPresenter
import com.example.bilibili.presentation.recommend.components.*
import com.example.bilibili.presentation.live.LiveTab
import com.example.bilibili.presentation.cartoon.CartoonTab

/**
 * 推荐页面
 * 按照MVP模式实现，展示推荐视频和轮播图
 */
@Composable
fun RecommendTab(
    context: Context,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToVideo: (String) -> Unit = {}
) {
    val presenter = remember { RecommendPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var currentTab by remember { mutableStateOf("推荐") }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        videos = presenter.getRecommendedVideos()
        // 指令20,29: 记录首页激活
        BilibiliAutoTestLogger.logHomePageActive()
    }

    // 根据选中的标签显示不同内容
    when (currentTab) {
        "直播" -> LiveTab(context, onTabSelected = { selectedTab -> currentTab = selectedTab })
        "动画" -> CartoonTab(context, onTabSelected = { selectedTab -> currentTab = selectedTab })
        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                // 顶部工具栏（固定）
                user?.let { TopBar(it, currentTab, onTabSelected = { selectedTab -> currentTab = selectedTab }, onSearchClick = onNavigateToSearch) }

                // 底部滚动内容
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                ) {
                    // 轮播图
                    item {
                        BannerCarousel()
                    }

                    // 视频网格（2列）
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((videos.size / 2 * 250).dp),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(videos) { video ->
                                VideoCard(video, onClick = {
                                    // 指令29: 记录点击第一个视频
                                    if (videos.indexOf(video) == 0) {
                                        BilibiliAutoTestLogger.logFirstVideoClicked()
                                    }
                                    onNavigateToVideo(video.videoId)
                                })
                            }
                        }
                    }

                    // 底部空白
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
