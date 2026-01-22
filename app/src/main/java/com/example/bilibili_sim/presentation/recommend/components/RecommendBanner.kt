package com.example.bilibili_sim.presentation.recommend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay

/**
 * 轮播图组件
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BannerCarousel() {
    val banners = listOf(
        BannerItem("avatar/autumn.jpg", "秋日出游指南"),
        BannerItem("avatar/fruit.jpg", "动漫排名更新"),
        BannerItem("avatar/cloud.jpg", "学会在自然中思考")
    )

    val pagerState = rememberPagerState()

    // 自动轮播
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.White)
            .padding(8.dp)
    ) {
        HorizontalPager(
            count = banners.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            BannerPage(banners[page])
        }

        // 指示器
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Surface(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .padding(2.dp),
                    color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {}
            }
        }
    }
}

/**
 * 轮播图页面
 */
@Composable
fun BannerPage(banner: BannerItem) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* TODO */ }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${banner.imageUrl}")
                .crossfade(true)
                .build(),
            contentDescription = banner.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 标题遮罩
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomStart)
                .background(
                    Color.Black.copy(alpha = 0.4f)
                )
        ) {
            Text(
                text = banner.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * 轮播图数据类
 */
data class BannerItem(
    val imageUrl: String,
    val title: String
)
