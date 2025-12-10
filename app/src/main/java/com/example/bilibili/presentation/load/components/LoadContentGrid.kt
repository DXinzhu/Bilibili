package com.example.bilibili.presentation.load.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

/**
 * 离线缓存内容网格
 * 展示缓存的视频列表
 */
@Composable
fun LoadContentGrid(cacheItems: List<LoadPresenter.CacheItem>) {
    if (cacheItems.isEmpty()) {
        EmptyCacheView()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            items(cacheItems) { item ->
                CacheVideoItem(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 空缓存提示视图
 */
@Composable
private fun EmptyCacheView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = "暂无缓存",
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "这里还什么都没有呢~",
                fontSize = 16.sp,
                color = Color(0xFF999999)
            )
            Text(
                text = "快去发现你喜欢的内容并缓存吧",
                fontSize = 14.sp,
                color = Color(0xFFCCCCCC)
            )
        }
    }
}

/**
 * 缓存视频项
 */
@Composable
private fun CacheVideoItem(item: LoadPresenter.CacheItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: 播放视频 */ }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 视频缩略图
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

                // 缓存状态标签
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    val isCompleted = item.cacheState.cacheStatus == com.example.bilibili.data.model.CacheStatus.COMPLETED
                    Surface(
                        color = if (isCompleted)
                            Color(0xFF00C853) else Color(0xFFFF9800),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isCompleted)
                                "已缓存" else "${(item.cacheState.cacheProgress * 100).toInt()}%",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // 视频信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 视频标题
                Text(
                    text = item.video.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF333333),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // UP主名称
                    Text(
                        text = item.video.upMasterName,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 视频大小
                        Text(
                            text = formatFileSize(item.cacheState.cacheSize),
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )

                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )

                        // 缓存时间
                        Text(
                            text = formatCacheTime(item.cacheState.downloadTime),
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 格式化文件大小
 */
private fun formatFileSize(sizeInBytes: Long): String {
    return when {
        sizeInBytes < 1024 -> "${sizeInBytes}B"
        sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024}KB"
        sizeInBytes < 1024 * 1024 * 1024 -> "${sizeInBytes / (1024 * 1024)}MB"
        else -> "${sizeInBytes / (1024 * 1024 * 1024)}GB"
    }
}

/**
 * 格式化缓存时间
 */
private fun formatCacheTime(downloadTime: Long): String {
    // TODO: 实现真实的时间格式化逻辑
    return "今天"
}
