package com.example.bilibili.presentation.live

import android.content.Context
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.data.model.LiveStream
import com.example.bilibili.presentation.live.components.*
import com.example.bilibili.presentation.recommend.components.TopBar
import com.example.bilibili.data.model.User
import com.example.bilibili.presentation.live.LivePresenter

/**
 * 直播页面
 * 按照MVP模式实现，展示直播内容
 */
@Composable
fun LiveTab(
    context: Context,
    onTabSelected: (String) -> Unit = {}
) {
    val presenter = remember { LivePresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var tmallLive by remember { mutableStateOf<LiveStream?>(null) }
    var followedLives by remember { mutableStateOf<List<LiveStream>>(emptyList()) }
    var recommendedLives by remember { mutableStateOf<List<LiveStream>>(emptyList()) }
    var followedLiveCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        tmallLive = presenter.getTmallLiveStream()
        followedLives = presenter.getFollowedLiveStreams()
        recommendedLives = presenter.getRecommendedLiveStreams()
        followedLiveCount = presenter.getFollowedLiveCount()
        // 指令31: 记录进入直播标签页
        BilibiliAutoTestLogger.logLiveTabEntered()
        // 记录直播推荐列表加载完成
        BilibiliAutoTestLogger.logLiveRecommendLoaded()
        // 记录找到第一个直播
        if (recommendedLives.isNotEmpty()) {
            BilibiliAutoTestLogger.logFirstLiveFound()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部工具栏（固定，直播tab选中）
        user?.let { TopBar(it, selectedTab = "直播", onTabSelected = onTabSelected) }

        // 底部滚动内容
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 天猫双十一直播
            item {
                tmallLive?.let { TmallLiveSection(it) }
            }

            // 我的关注
            item {
                if (followedLives.isNotEmpty()) {
                    FollowedUpMastersSection(followedLives[0], followedLiveCount)
                }
            }

            // 推荐标签栏
            item {
                RecommendedLiveHeader()
            }

            // 直播网格（2列）
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((recommendedLives.size / 2 * 280).dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recommendedLives) { live ->
                        LiveStreamCard(live, onViewerCountDisplay = {
                            // 指令31: 记录显示直播观看人数
                            BilibiliAutoTestLogger.logLiveViewerCountDisplayed(live.getFormattedViewerCount())
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

/**
 * 顶部工具栏（直播页选中状态）
 */
