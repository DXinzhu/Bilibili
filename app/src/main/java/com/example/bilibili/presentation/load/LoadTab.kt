package com.example.bilibili.presentation.load

import android.content.Context
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
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
import com.example.bilibili.presentation.load.LoadPresenter
import com.example.bilibili.presentation.load.components.*

/**
 * 离线缓存页面
 * 按照MVP模式实现，展示用户的缓存视频列表
 */
@Composable
fun LoadTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    val presenter = remember { LoadPresenter(context) }
    var cacheItems by remember { mutableStateOf<List<LoadPresenter.CacheItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        cacheItems = presenter.loadCacheVideos()
        // 指令13: 记录进入离线缓存页面
        BilibiliAutoTestLogger.logOfflineCachePageEntered()
        // 记录缓存列表加载完成
        BilibiliAutoTestLogger.logCacheListLoaded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部标题栏
        LoadTopBar(onNavigateBack = onNavigateBack)

        // 筛选区域
        LoadFilterSection()

        // 缓存内容网格
        LoadContentGrid(cacheItems = cacheItems)
    }
}
