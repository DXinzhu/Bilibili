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
import com.example.bilibili.presenter.HistoryPresenter

/**
 * 历史记录页面
 * 按照MVP模式实现，展示用户的观看历史
 */
@Composable
fun HistoryTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    val presenter = remember { HistoryPresenter(context) }
    var historyItems by remember { mutableStateOf<List<HistoryPresenter.HistoryItem>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("全部") }

    LaunchedEffect(Unit) {
        historyItems = presenter.getHistoryItems()
    }

    // 根据选中的分类筛选历史记录
    val filteredItems = presenter.filterByCategory(historyItems, selectedCategory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部标题栏
        HistoryTopBar(onNavigateBack = onNavigateBack)

        // 分类栏
        CategoryBar(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // 历史记录列表
        HistoryList(
            items = filteredItems,
            presenter = presenter
        )
    }
}

/**
 * 顶部标题栏
 */
@Composable
fun HistoryTopBar(onNavigateBack: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧返回按钮
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black
                )
            }

            // 中间标题
            Text(
                text = "历史记录",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 右侧功能按钮
            Row {
                IconButton(onClick = { /* TODO: 搜索功能 */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.Gray
                    )
                }
                IconButton(onClick = { /* TODO: 更多菜单 */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * 分类栏
 */
@Composable
fun CategoryBar(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("全部", "视频", "直播", "专栏", "游戏")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            categories.forEach { category ->
                CategoryItem(
                    text = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

/**
 * 分类项
 */
@Composable
fun CategoryItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFFFE5F0) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6699) else Color.Gray,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * 历史记录列表
 */
@Composable
fun HistoryList(
    items: List<HistoryPresenter.HistoryItem>,
    presenter: HistoryPresenter
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // "今天"分组标题
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5F5F5)
            ) {
                Text(
                    text = "今天",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // 历史记录项
        items(items) { item ->
            HistoryListItem(
                item = item,
                presenter = presenter
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFEEEEEE),
                thickness = 0.5.dp
            )
        }

        // 底部空白
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 历史记录列表项
 */
@Composable
fun HistoryListItem(
    item: HistoryPresenter.HistoryItem,
    presenter: HistoryPresenter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 点击播放视频 */ }
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 左侧视频缩略图
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(90.dp)
        ) {
            if (item.video?.coverImage?.isNotEmpty() == true) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${item.video.coverImage}")
                        .crossfade(true)
                        .build(),
                    contentDescription = item.history.videoTitle,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 占位符
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "视频",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // 观看进度条
            if (item.history.watchProgress > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.BottomStart)
                ) {
                    // 背景
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    // 进度
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(item.history.watchProgress)
                            .background(Color(0xFFFF6699))
                    )
                }
            }
        }

        // 右侧信息
        Column(
            modifier = Modifier
                .weight(1f)
                .height(90.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 视频标题
            Text(
                text = item.history.videoTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            // 底部信息
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // UP主名称
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "UP主",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = item.history.upMasterName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // 观看时间
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "时间",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = presenter.formatWatchTime(item.history.watchTime),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // 更多操作按钮
        IconButton(
            onClick = { /* TODO: 更多操作 */ },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
