package com.example.bilibili.view

import android.content.Context
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
import com.example.bilibili.model.HotSearch
import com.example.bilibili.model.SearchHistory
import com.example.bilibili.model.SearchDiscovery
import com.example.bilibili.presenter.SearchPresenter
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
    var searchText by remember { mutableStateOf("B站") }
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
                if (searchText == "游戏解说") {
                    onNavigateToGame(searchText)
                } else {
                    presenter.search(searchText)
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

/**
 * 顶部搜索栏
 */
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 搜索输入框
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        modifier = Modifier.weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 15.sp,
                            color = Color.Black
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 搜索按钮
            Text(
                text = "搜索",
                fontSize = 15.sp,
                color = Color(0xFFFF6699),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSearch() }
            )
        }
    }
}

/**
 * bilibili热搜区域
 */
@Composable
fun HotSearchSection(hotSearches: List<HotSearch>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "bilibili热搜",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "完整榜单",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "完整榜单",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 热搜列表 (2列网格)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height((hotSearches.size / 2 * 48 + 40).dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(hotSearches) { hotSearch ->
                HotSearchItem(hotSearch)
            }
        }
    }
}

/**
 * 热搜条目
 */
@Composable
fun HotSearchItem(hotSearch: HotSearch) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { /* TODO: 点击热搜 */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = hotSearch.keyword,
            fontSize = 13.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // 热搜标签
        if (hotSearch.tag.isNotEmpty()) {
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                shape = RoundedCornerShape(3.dp),
                color = when (hotSearch.tag) {
                    "hot" -> Color(0xFFFF6699)
                    "new" -> Color(0xFFFFB800)
                    else -> Color.Transparent
                },
                modifier = Modifier.size(width = 24.dp, height = 16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = when (hotSearch.tag) {
                            "hot" -> "热"
                            "new" -> "新"
                            else -> ""
                        },
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * 搜索历史区域
 */
@Composable
fun SearchHistorySection(
    searchHistory: List<SearchHistory>,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "搜索历史",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onClear() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "折叠",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 搜索历史标签 (流式布局)
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            content = {
                searchHistory.forEach { history ->
                    SearchHistoryTag(history.keyword)
                }
            }
        )
    }
}

/**
 * 搜索历史标签
 */
@Composable
fun SearchHistoryTag(keyword: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.clickable { /* TODO: 点击历史标签 */ }
    ) {
        Text(
            text = keyword,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * 搜索发现区域
 */
@Composable
fun SearchDiscoverySection(searchDiscoveries: List<SearchDiscovery>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "搜索发现",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "刷新",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = "查看",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 搜索发现列表 (2列网格)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height((searchDiscoveries.size / 2 * 50 + 40).dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchDiscoveries) { discovery ->
                SearchDiscoveryItem(discovery)
            }
        }
    }
}

/**
 * 搜索发现条目
 */
@Composable
fun SearchDiscoveryItem(discovery: SearchDiscovery) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { /* TODO: 点击发现条目 */ },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = discovery.title,
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (discovery.subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = discovery.subtitle,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 反馈按钮
 */
@Composable
fun FeedbackButton() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable { /* TODO: 反馈 */ },
        color = Color.White,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "反馈",
                fontSize = 15.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Feedback,
                contentDescription = "反馈",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
