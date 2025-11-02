package com.example.bilibili.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.bilibili.model.User
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.RecommendPresenter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 推荐页面
 * 按照MVP模式实现，展示推荐视频和轮播图
 */
@Composable
fun RecommendTab(
    context: Context,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToVideo: (String) -> Unit = {}
) {
    val presenter = remember { RecommendPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var currentTab by remember { mutableStateOf("推荐") }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        videos = presenter.getRecommendedVideos()
    }

    // 根据选中的标签显示不同内容
    when (currentTab) {
        "直播" -> LiveTab(context, onTabSelected = { selectedTab -> currentTab = selectedTab })
        "动画" -> CartoonTab(context, onTabSelected = { selectedTab -> currentTab = selectedTab })
        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                // 顶部工具栏（固定）
                user?.let { TopBar(it, currentTab, onTabSelected = { selectedTab -> currentTab = selectedTab }, onSearchClick = onNavigateToSearch) }

                // 底部滚动内容
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                ) {
                    // 轮播图
                    item {
                        BannerCarousel()
                    }

                    // 视频网格（2列）
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((videos.size / 2 * 250).dp),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(videos) { video ->
                                VideoCard(video, onClick = { onNavigateToVideo(video.videoId) })
                            }
                        }
                    }

                    // 底部空白
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

/**
 * 顶部工具栏
 */
@Composable
fun TopBar(
    user: User,
    selectedTab: String = "推荐",
    onTabSelected: (String) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 第一行：头像、搜索栏、游戏、信件
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 用户头像
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${user.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { /* TODO */ },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 搜索栏
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clickable { onSearchClick() },
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "天后",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 游戏图标
            Box(modifier = Modifier.clickable { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "游戏",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 信件图标（带红点）
            Box(modifier = Modifier.clickable { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "消息",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                // 红点角标
                Surface(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp),
                    color = Color.Red,
                    shape = CircleShape
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "99",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 第二行：导航标签
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(text = "直播", isSelected = selectedTab == "直播", onClick = { onTabSelected("直播") })
            TabItem(text = "推荐", isSelected = selectedTab == "推荐", onClick = { onTabSelected("推荐") })
            TabItem(text = "热门", isSelected = selectedTab == "热门", onClick = { onTabSelected("热门") })
            TabItem(text = "动画", isSelected = selectedTab == "动画", onClick = { onTabSelected("动画") })
            TabItem(text = "影视", isSelected = selectedTab == "影视", onClick = { onTabSelected("影视") })
            TabItem(text = "S15", isSelected = selectedTab == "S15", onClick = { onTabSelected("S15") })
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}

/**
 * 标签项
 */
@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6699) else Color.Gray
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp),
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(1.5.dp)
            ) {}
        }
    }
}

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
    val coroutineScope = rememberCoroutineScope()

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
 * 视频卡片
 */
@Composable
fun VideoCard(video: Video, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        // 视频封面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
        ) {
            if (video.coverImage.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${video.coverImage}")
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 占位符
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "播放",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // 视频信息
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // 视频标题
            Text(
                text = video.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // UP主名称
            Text(
                text = video.upMasterName,
                fontSize = 12.sp,
                color = Color.Gray
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
