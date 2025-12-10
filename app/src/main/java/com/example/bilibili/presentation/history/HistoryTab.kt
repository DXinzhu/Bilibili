package com.example.bilibili.presentation.history

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import com.example.bilibili.presentation.history.HistoryPresenter
import com.example.bilibili.presentation.history.components.*
import com.example.bilibili.common.utils.BilibiliAutoTestLogger

/**
 * 历史记录页面
 * 按照MVP模式实现，展示用户的观看历史
 */
@Composable
fun HistoryTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    // 记录日志：用户进入历史记录页面
    LaunchedEffect(Unit) {
        BilibiliAutoTestLogger.logHistoryPageEntered()
        BilibiliAutoTestLogger.logHistoryTabViewed()
    }

    val presenter = remember { HistoryPresenter(context) }
    var historyItems by remember { mutableStateOf<List<HistoryPresenter.HistoryItem>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("全部") }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        historyItems = presenter.getHistoryItems()
        // 记录日志：历史记录数据加载完成
        BilibiliAutoTestLogger.logHistoryDataLoaded(historyItems.size)
    }

    // 根据选中的分类筛选历史记录
    val filteredItems = presenter.filterByCategory(historyItems, selectedCategory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部栏
        HistoryTopBar(onNavigateBack = onNavigateBack)

        // 筛选栏
        HistoryFilterBar(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // 视频列表
        HistoryVideoList(
            historyItems = filteredItems,
            onDeleteItem = { item ->
                historyItems = historyItems.filter { it != item }
                refreshTrigger++
            }
        )
    }
}
