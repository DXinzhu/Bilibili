package com.example.bilibili_sim.presentation.load.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

/**
 * 离线缓存筛选区域
 * 包含排序选项和筛选选项
 */
@Composable
fun LoadFilterSection() {
    var selectedFilter by remember { mutableStateOf("全部") }
    var selectedSort by remember { mutableStateOf("时间排序") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 筛选选项行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    text = "全部",
                    isSelected = selectedFilter == "全部",
                    onClick = { selectedFilter = "全部" }
                )
                FilterChip(
                    text = "视频",
                    isSelected = selectedFilter == "视频",
                    onClick = { selectedFilter = "视频" }
                )
                FilterChip(
                    text = "番剧",
                    isSelected = selectedFilter == "番剧",
                    onClick = { selectedFilter = "番剧" }
                )
                FilterChip(
                    text = "课程",
                    isSelected = selectedFilter == "课程",
                    onClick = { selectedFilter = "课程" }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 排序选项行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "排序方式",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortChip(
                        text = "时间排序",
                        isSelected = selectedSort == "时间排序",
                        onClick = { selectedSort = "时间排序" }
                    )
                    SortChip(
                        text = "大小排序",
                        isSelected = selectedSort == "大小排序",
                        onClick = { selectedSort = "大小排序" }
                    )
                }
            }
        }
    }
}

/**
 * 筛选标签
 */
@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFFFE5F0) else Color(0xFFF5F5F5),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6699) else Color(0xFF666666),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * 排序标签
 */
@Composable
private fun SortChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = if (isSelected) Color(0xFFFF6699) else Color(0xFF999999)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color(0xFFFF6699),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
