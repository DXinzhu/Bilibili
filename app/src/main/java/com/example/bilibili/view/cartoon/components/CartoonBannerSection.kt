package com.example.bilibili.view.cartoon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.model.Video

/**
 * 顶部大图番剧区域
 */
@Composable
fun FeaturedCartoonSection(cartoon: Video) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color.White)
            .clickable { /* TODO: 进入番剧详情 */ }
    ) {
        // 封面图片
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${cartoon.coverImage}")
                .crossfade(true)
                .build(),
            contentDescription = cartoon.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 底部渐变遮罩和标题
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomStart)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
        ) {
            Text(
                text = cartoon.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

/**
 * 导航标签栏（找番看、时间表、番剧、国创、少儿、十月新番）
 */
@Composable
fun CartoonNavigationTabs() {
    val tabs = listOf("找番看", "时间表", "番剧", "国创", "少儿", "十月新番")
    var selectedTab by remember { mutableStateOf("找番看") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        color = Color.White
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(tabs.size) { index ->
                val tab = tabs[index]
                Text(
                    text = tab,
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == tab) Color.Black else Color.Gray,
                    modifier = Modifier.clickable { selectedTab = tab }
                )
            }
        }
    }
}
