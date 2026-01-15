package com.example.bilibili.presentation.accountprofile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * 账号资料列表项组件
 * 用于展示账号资料的各个字段
 */
@Composable
fun ProfileListItem(
    label: String,
    value: String,
    showAvatar: Boolean = false,
    avatarUrl: String = "",
    showQrCode: Boolean = false,
    showChevron: Boolean = true,
    isPlaceholder: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧标签
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 右侧内容
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            when {
                // 显示头像
                showAvatar -> {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/$avatarUrl")
                            .crossfade(true)
                            .build(),
                        contentDescription = "头像",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFFF6699), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                // 显示二维码图标
                showQrCode -> {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "二维码",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                // 显示文本值
                else -> {
                    Text(
                        text = value,
                        fontSize = 15.sp,
                        color = if (isPlaceholder) Color.Gray else Color.Black,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
        }

        // 右侧箭头
        if (showChevron) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "进入",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // 分隔线
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = Color(0xFFF0F0F0),
        thickness = 1.dp
    )
}
