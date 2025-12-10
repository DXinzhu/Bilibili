package com.example.bilibili.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.bilibili.model.User
import com.example.bilibili.view.person.components.*
import com.example.bilibili.view.person.components.*
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.PersonPresenter
import com.example.bilibili.utils.BilibiliAutoTestLogger

/**
 * 个人主页页面
 * 按照MVP模式实现，展示用户的个人主页信息
 */
@Composable
fun PersonTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    val presenter = remember { PersonPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var selectedTab by remember { mutableStateOf("主页") }
    var defaultFavorite by remember { mutableStateOf<PersonPresenter.FavoriteFolder?>(null) }
    var followedCartoons by remember { mutableStateOf<List<Video>>(emptyList()) }
    var recentCoinedVideos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var recentLikedVideos by remember { mutableStateOf<List<Video>>(emptyList()) }

    LaunchedEffect(Unit) {
        BilibiliAutoTestLogger.logPersonTab()
        BilibiliAutoTestLogger.logProfilePageEntered()

        user = presenter.loadUserData()
        defaultFavorite = presenter.getDefaultFavorite()
        followedCartoons = presenter.getFollowedCartoons()
        recentCoinedVideos = presenter.getRecentCoinedVideos()
        recentLikedVideos = presenter.getRecentLikedVideos()

        BilibiliAutoTestLogger.logProfileDataLoaded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        PersonTopBar(
            userName = user?.name ?: "小明",
            uid = user?.uid?.toInt() ?: 0,
            onNavigateBack = onNavigateBack
        )

        // Tab切换栏
        PersonTabBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // 内容区域
        when (selectedTab) {
            "主页" -> {
                PersonInfoSection(
                    user = user,
                    defaultFavorite = defaultFavorite
                )
            }
            "动态" -> {
                // TODO: 动态内容
            }
            "投稿" -> {
                PersonVideoGrid(videos = recentCoinedVideos)
            }
        }
    }
}
