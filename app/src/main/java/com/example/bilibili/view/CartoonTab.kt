package com.example.bilibili.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.bilibili.model.User
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.CartoonPresenter

/**
 * 动画/番剧页面
 * 按照MVP模式实现，展示番剧内容
 */
@Composable
fun CartoonTab(
    context: Context,
    onTabSelected: (String) -> Unit = {}
) {
    val presenter = remember { CartoonPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var featuredCartoon by remember { mutableStateOf<Video?>(null) }
    var rankingCartoons by remember { mutableStateOf<List<Video>>(emptyList()) }
    var selectedRankingTab by remember { mutableStateOf("国创榜") }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        featuredCartoon = presenter.getFeaturedCartoon()
        rankingCartoons = presenter.getRankingCartoons()
        // 记录进入动画频道页面
        Log.d("BilibiliAutoTest", "ANIMATION_CHANNEL_ENTERED")
        // 记录频道内容加载完成
        Log.d("BilibiliAutoTest", "CHANNEL_CONTENT_LOADED")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部工具栏（固定）
        user?.let { TopBar(it, selectedTab = "动画", onTabSelected = onTabSelected) }

        // 底部滚动内容
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 顶部大图区域
            item {
                featuredCartoon?.let { FeaturedCartoonSection(it) }
            }

            // 导航标签栏
            item {
                CartoonNavigationTabs()
            }

            // 热门排行榜标题
            item {
                RankingHeader(selectedTab = selectedRankingTab, onTabSelected = { selectedRankingTab = it })
            }

            // 排行榜内容
            item {
                RankingContent(rankingCartoons)
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

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
            items(tabs) { tab ->
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

/**
 * 排行榜标题和二级标签
 */
@Composable
fun RankingHeader(selectedTab: String, onTabSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 标题行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "热门排行榜",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO: 更多榜单 */ }
            ) {
                Text(
                    text = "更多榜单",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "更多",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 二级标签（国创榜、番剧榜、资讯榜、热搜榜、会员榜）
        val rankingTabs = listOf("国创榜", "番剧榜", "资讯榜", "热搜榜", "会员榜")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rankingTabs) { tab ->
                Surface(
                    modifier = Modifier.clickable { onTabSelected(tab) },
                    color = if (selectedTab == tab) Color(0xFFFFE5EE) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == tab) Color(0xFFFF6699) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

/**
 * 排行榜内容（3个番剧横向排列）
 */
@Composable
fun RankingContent(cartoons: List<Video>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cartoons) { cartoon ->
            RankingCartoonCard(cartoon)
        }
    }
}

/**
 * 排行榜番剧卡片
 */
@Composable
fun RankingCartoonCard(cartoon: Video) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { /* TODO: 进入番剧详情 */ }
    ) {
        Box {
            // 封面图片
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${cartoon.coverImage}")
                    .crossfade(true)
                    .build(),
                contentDescription = cartoon.title,
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // 排名标签（左上角）
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
                color = when (cartoon.ranking) {
                    1 -> Color(0xFFFFD700) // 金色
                    2 -> Color(0xFFC0C0C0) // 银色
                    3 -> Color(0xFFCD7F32) // 铜色
                    else -> Color(0xFF666666)
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = cartoon.ranking.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 番剧标题
        Text(
            text = cartoon.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // 更新信息
        Text(
            text = cartoon.episodeInfo,
            fontSize = 11.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
