package com.example.bilibili.view.collect.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.CollectPresenter

/**
 * 收藏次级标签栏（合集和视频切换）
 */
@Composable
fun CollectSecondaryBar(
    selectedSubTab: String,
    onSubTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧子标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SubTabItem(
                    text = "合集",
                    isSelected = selectedSubTab == "合集",
                    onClick = { onSubTabSelected("合集") }
                )
                SubTabItem(
                    text = "视频",
                    isSelected = selectedSubTab == "视频",
                    onClick = { onSubTabSelected("视频") }
                )
            }

            // 右侧图标
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { /* TODO: 搜索功能 */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* TODO: 列表视图切换 */ }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "列表",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * 收藏视频列表
 */
@Composable
fun CollectVideoList(
    videos: List<Video>,
    presenter: CollectPresenter
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(videos) { video ->
            CollectVideoItem(
                video = video,
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
