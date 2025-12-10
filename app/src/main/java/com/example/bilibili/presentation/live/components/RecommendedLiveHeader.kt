package com.example.bilibili.presentation.live.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.bilibili.data.model.LiveStream
import com.example.bilibili.data.model.User
import com.example.bilibili.presentation.live.LivePresenter
import com.example.bilibili.common.utils.BilibiliAutoTestLogger

@Composable
fun RecommendedLiveHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabItem(text = "推荐", isSelected = true)
        Spacer(modifier = Modifier.width(24.dp))
        TabItem(text = "人气", isSelected = false)
        Spacer(modifier = Modifier.width(24.dp))
        TabItem(text = "颜值", isSelected = false)
        Spacer(modifier = Modifier.width(24.dp))
        TabItem(text = "英雄联盟", isSelected = false)
        Spacer(modifier = Modifier.width(24.dp))
        TabItem(text = "虚拟主播", isSelected = false)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = "网格",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.ViewAgenda,
            contentDescription = "列表",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun TabItem(text: String, isSelected: Boolean) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) Color(0xFFFF6699) else Color.Gray
    )
}

/**
 * 直播卡片
 */
