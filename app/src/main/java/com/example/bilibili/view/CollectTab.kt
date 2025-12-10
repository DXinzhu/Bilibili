package com.example.bilibili.view

import android.content.Context
import android.util.Log
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
import com.example.bilibili.model.Video
import com.example.bilibili.view.collect.components.*
import com.example.bilibili.presenter.CollectPresenter
import com.example.bilibili.utils.BilibiliAutoTestLogger

/**
 * 收藏页面
 * 按照MVP模式实现，展示用户的收藏视频
 */
@Composable
fun CollectTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    val presenter = remember { CollectPresenter(context) }
    var collectedVideos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var selectedMainTab by remember { mutableStateOf("收藏") }
    var selectedSubTab by remember { mutableStateOf("视频") }

    LaunchedEffect(Unit) {
        // 指令7: 记录进入收藏页面
        BilibiliAutoTestLogger.logFavoriteTabClicked()
        BilibiliAutoTestLogger.logFavoritePageEntered()

        // 加载收藏视频数据
        collectedVideos = presenter.getCollectedVideos()

        // 指令7,25: 记录数据加载完成
        BilibiliAutoTestLogger.logFavoriteDataLoaded(collectedVideos.size)
        BilibiliAutoTestLogger.logFavoriteCountDisplayed(collectedVideos.size)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部栏
        CollectTopBar(
            selectedMainTab = selectedMainTab,
            selectedSubTab = selectedSubTab,
            onNavigateBack = onNavigateBack
        )

        // 分类栏
        CollectCategoryBar(
            selectedMainTab = selectedMainTab,
            selectedSubTab = selectedSubTab,
            onMainTabSelected = { selectedMainTab = it },
            onSubTabSelected = { selectedSubTab = it }
        )

        // 视频列表
        CollectVideoList(
            videos = collectedVideos,
            presenter = presenter
        )
    }
}
