package com.example.bilibili.presentation.history.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import com.example.bilibili.common.utils.BilibiliAutoTestLogger

/**
 * 历史记录视频列表
 * 展示历史记录，支持长按删除
 */
@Composable
fun HistoryVideoList(
    historyItems: List<HistoryPresenter.HistoryItem>,
    onDeleteItem: (HistoryPresenter.HistoryItem) -> Unit = {}
) {
    var itemToDelete by remember { mutableStateOf<HistoryPresenter.HistoryItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        if (historyItems.isEmpty()) {
            item {
                EmptyHistoryPlaceholder()
            }
        } else {
            items(historyItems) { item ->
                HistoryVideoItem(
                    item = item,
                    onLongClick = {
                        BilibiliAutoTestLogger.logHistoryItemLongPressed()
                        itemToDelete = item
                        showDeleteDialog = true
                    }
                )
            }
        }

        // 底部空白
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // 删除确认对话框
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                itemToDelete = null
            },
            title = { Text("删除历史记录") },
            text = { Text("确定要删除这条历史记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        BilibiliAutoTestLogger.logDeleteButtonClicked()
                        onDeleteItem(itemToDelete!!)
                        BilibiliAutoTestLogger.logHistoryItemDeleted(historyItems.size - 1)
                        showDeleteDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("删除", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 历史记录视频项
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryVideoItem(
    item: HistoryPresenter.HistoryItem,
    onLongClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { /* TODO: 播放视频 */ },
                    onLongClick = onLongClick
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 视频缩略图
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(item.history.watchProgress.toFloat() / 100f)
                                .background(Color(0xFFFF6699))
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
                    text = item.history.videoTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF333333),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // UP主名称
                    Text(
                        text = item.history.upMasterName,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )

                    // 观看时间
                    Text(
                        text = formatWatchTime(item.history.watchTime),
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        }
    }
}

/**
 * 空状态占位符
 */
@Composable
private fun EmptyHistoryPlaceholder() {
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
                imageVector = Icons.Default.History,
                contentDescription = "暂无历史",
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "暂无观看历史",
                fontSize = 16.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

/**
 * 格式化观看时间
 */
private fun formatWatchTime(watchTime: Long): String {
    // TODO: 实现真实的时间格式化逻辑
    return "今天 12:30"
}
