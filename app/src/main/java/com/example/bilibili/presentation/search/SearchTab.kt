package com.example.bilibili.presentation.search

import android.content.Context
import android.util.Log
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilibili.data.model.HotSearch
import com.example.bilibili.presentation.search.components.*
import com.example.bilibili.data.model.SearchHistory
import com.example.bilibili.data.model.SearchDiscovery
import com.example.bilibili.presentation.search.SearchPresenter
import com.google.accompanist.flowlayout.FlowRow

/**
 * 搜索页面
 * 包含搜索栏、热搜、搜索历史、搜索发现
 */
@Composable
fun SearchTab(
    context: Context,
    onBack: () -> Unit = {},
    onNavigateToGame: (String) -> Unit = {}
) {
    val presenter = remember { SearchPresenter(context) }
    var searchText by remember { mutableStateOf("") }
    var hotSearches by remember { mutableStateOf<List<HotSearch>>(emptyList()) }
    var searchHistory by remember { mutableStateOf<List<SearchHistory>>(emptyList()) }
    var searchDiscoveries by remember { mutableStateOf<List<SearchDiscovery>>(emptyList()) }

    LaunchedEffect(Unit) {
        hotSearches = presenter.loadHotSearches()
        searchHistory = presenter.loadSearchHistory()
        searchDiscoveries = presenter.loadSearchDiscoveries()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部搜索栏
        SearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBack = onBack,
            onSearch = {
                if (searchText.contains("游戏") || searchText.contains("解说")) {
                    // 指令4,21,23: 记录搜索完成
                    BilibiliAutoTestLogger.logSearchCompleted(searchText)
                    onNavigateToGame(searchText)
                } else {
                    presenter.search(searchText)
                    BilibiliAutoTestLogger.logSearchCompleted(searchText)
                }
            }
        )

        // 滚动内容
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            // bilibili热搜区域
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HotSearchSection(hotSearches)
            }

            // 搜索历史区域
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SearchHistorySection(searchHistory, onClear = { presenter.clearHistory() })
            }

            // 搜索发现区域
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SearchDiscoverySection(searchDiscoveries)
            }

            // 反馈按钮
            item {
                Spacer(modifier = Modifier.height(24.dp))
                FeedbackButton()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}