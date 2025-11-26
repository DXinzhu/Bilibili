package com.example.bilibili.view

import android.content.Context
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
import com.example.bilibili.presenter.PersonPresenter
import com.example.bilibili.utils.BilibiliAutoTestLogger

/**
 * 个人主页页面
 * 按照MVP模式实现，展示用户的个人主页信息
 */
@Composable
fun PersonTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    val presenter = remember { PersonPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var selectedTab by remember { mutableStateOf("主页") }
    var defaultFavorite by remember { mutableStateOf<PersonPresenter.FavoriteFolder?>(null) }
    var followedCartoons by remember { mutableStateOf<List<Video>>(emptyList()) }
    var recentCoinedVideos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var recentLikedVideos by remember { mutableStateOf<List<Video>>(emptyList()) }

    LaunchedEffect(Unit) {
        BilibiliAutoTestLogger.logPersonTab()
        BilibiliAutoTestLogger.logProfilePageEntered()

        user = presenter.loadUserData()
        defaultFavorite = presenter.getDefaultFavorite()
        followedCartoons = presenter.getFollowedCartoons()
        recentCoinedVideos = presenter.getRecentCoinedVideos()
        recentLikedVideos = presenter.getRecentLikedVideos()

        BilibiliAutoTestLogger.logProfileDataLoaded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        PersonTopBar(
            userName = user?.name ?: "小明",
            uid = user?.uid ?: 0,
            onNavigateBack = onNavigateBack
        )

        // Tab切换栏
        PersonTabBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // 内容区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 默认收藏夹
            item {
                PersonSectionHeader(title = "收藏", count = defaultFavorite?.count ?: 0)
                defaultFavorite?.let { favorite ->
                    FavoriteFolderCard(favorite = favorite, presenter = presenter)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 追漫
            if (followedCartoons.isNotEmpty()) {
                item {
                    PersonSectionHeader(title = "追漫", count = followedCartoons.size)
                }
                items(followedCartoons) { cartoon ->
                    CartoonCard(cartoon = cartoon, presenter = presenter)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 最近投币的视频
            if (recentCoinedVideos.isNotEmpty()) {
                item {
                    PersonSectionHeader(title = "最近投币的视频")
                    Spacer(modifier = Modifier.height(8.dp))
                    VideoCardRow(videos = recentCoinedVideos, presenter = presenter)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 最近点赞的视频
            if (recentLikedVideos.isNotEmpty()) {
                item {
                    PersonSectionHeader(title = "最近点赞的视频")
                    Spacer(modifier = Modifier.height(8.dp))
                    VideoCardRow(videos = recentLikedVideos, presenter = presenter)
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

/**
 * 顶部导航栏
 */
@Composable
fun PersonTopBar(
    userName: String,
    uid: Long,
    onNavigateBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧返回按钮
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black
                )
            }

            // 中间用户名和UID
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (uid > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "UID: $uid",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // 右侧功能按钮
            Row {
                IconButton(onClick = { /* TODO: 搜索功能 */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.Gray
                    )
                }
                IconButton(onClick = { /* TODO: 更多菜单 */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Tab切换栏
 */
@Composable
fun PersonTabBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("主页", "动态", "投稿", "收藏")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                PersonTab(
                    text = tab,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

/**
 * Tab项
 */
@Composable
fun PersonTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6699) else Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(Color(0xFFFF6699), RoundedCornerShape(2.dp))
            )
        }
    }
}

/**
 * 区块标题
 */
@Composable
fun PersonSectionHeader(
    title: String,
    count: Int? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (count != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = count.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* TODO: 查看更多 */ }
        ) {
            Text(
                text = "查看更多",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * 收藏夹卡片
 */
@Composable
fun FavoriteFolderCard(
    favorite: PersonPresenter.FavoriteFolder,
    presenter: PersonPresenter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 点击查看收藏夹 */ }
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 封面图
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${favorite.coverImage}")
                .crossfade(true)
                .build(),
            contentDescription = favorite.name,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )

        // 信息区域
        Column(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 收藏夹名称
            Text(
                text = favorite.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 内容数量和公开状态
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${favorite.count}个内容",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "·",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = if (favorite.isPublic) "公开" else "私密",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 番剧卡片
 */
@Composable
fun CartoonCard(
    cartoon: Video,
    presenter: PersonPresenter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 点击查看番剧 */ }
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 番剧封面
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(160.dp)
        ) {
            if (cartoon.coverImage?.isNotEmpty() == true) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${cartoon.coverImage}")
                        .crossfade(true)
                        .build(),
                    contentDescription = cartoon.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // 更新信息标签（左下角）
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = cartoon.episodeInfo ?: "更新至85话",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // 番剧信息
        Column(
            modifier = Modifier
                .weight(1f)
                .height(160.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = cartoon.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 视频卡片行（横向2个视频）
 */
@Composable
fun VideoCardRow(
    videos: List<Video>,
    presenter: PersonPresenter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        videos.take(2).forEach { video ->
            VideoCardItem(
                video = video,
                presenter = presenter,
                modifier = Modifier.weight(1f)
            )
        }
        // 如果只有一个视频，填充空白
        if (videos.size < 2) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * 视频卡片项
 */
@Composable
fun VideoCardItem(
    video: Video,
    presenter: PersonPresenter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { /* TODO: 点击播放视频 */ }
    ) {
        // 视频封面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
        ) {
            if (video.coverImage?.isNotEmpty() == true) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${video.coverImage}")
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
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

            // 播放量和评论数（左下角）
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "播放量",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = presenter.formatViewCount(video.viewCount),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "评论数",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = presenter.formatCommentCount(video.commentCount),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 时长（右下角）
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = "01:07",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // 视频标题
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = video.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp
        )
    }
}
