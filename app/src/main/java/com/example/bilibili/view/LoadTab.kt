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
import com.example.bilibili.presenter.LoadPresenter

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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部标题栏
        LoadTopBar(onNavigateBack = onNavigateBack)

        // 缓存视频列表
        if (cacheItems.isEmpty()) {
            // 空状态提示
            EmptyCacheView()
        } else {
            // 缓存视频列表
            CacheVideoList(
                items = cacheItems,
                presenter = presenter
            )
        }
    }
}

/**
 * 顶部标题栏
 */
@Composable
fun LoadTopBar(onNavigateBack: () -> Unit = {}) {
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
                text = "离线缓存",
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
                IconButton(onClick = { /* TODO: 设置功能 */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * 空缓存提示视图
 */
@Composable
fun EmptyCacheView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 提示图标
        Icon(
            imageVector = Icons.Default.VideoLibrary,
            contentDescription = "暂无缓存",
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 提示文字
        Text(
            text = "这里还什么都没有呢 ~",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "快去发现你喜欢的内容并缓存吧",
            fontSize = 14.sp,
            color = Color.LightGray
        )
    }
}

/**
 * 缓存视频列表
 */
@Composable
fun CacheVideoList(
    items: List<LoadPresenter.CacheItem>,
    presenter: LoadPresenter
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 缓存统计信息
        item {
            val summary = presenter.getCacheSummary(items)
            CacheSummaryCard(summary = summary)
        }

        // 缓存视频项
        items(items) { item ->
            CacheVideoItem(
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
 * 缓存统计卡片
 */
@Composable
fun CacheSummaryCard(summary: LoadPresenter.CacheSummary) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = summary.totalSizeFormatted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6699)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "总大小",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp),
                color = Color.LightGray
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${summary.totalCount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6699)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "缓存个数",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp),
                color = Color.LightGray
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${summary.completedCount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6699)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "已完成",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 缓存视频列表项
 */
@Composable
fun CacheVideoItem(
    item: LoadPresenter.CacheItem,
    presenter: LoadPresenter
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
            if (item.video.coverImage.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${item.video.coverImage}")
                        .crossfade(true)
                        .build(),
                    contentDescription = item.video.title,
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
            if (item.cacheState.watchProgress > 0) {
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
                            .fillMaxWidth(item.cacheState.watchProgress)
                            .background(Color(0xFFFF6699))
                    )
                }
            }

            // 缓存状态标签
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = item.getDisplayQuality(),
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
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
                text = item.getDisplayTitle(),
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
                        text = item.getDisplayUPName(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // 缓存信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 缓存大小
                    Text(
                        text = item.getDisplaySize(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    // 缓存状态
                    Text(
                        text = item.getDisplayStatus(),
                        fontSize = 12.sp,
                        color = Color(0xFFFF6699)
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