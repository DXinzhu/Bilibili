package com.example.bilibili.view

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
import com.example.bilibili.model.LiveStream
import com.example.bilibili.model.User
import com.example.bilibili.presenter.LivePresenter

/**
 * 直播页面
 * 按照MVP模式实现，展示直播内容
 */
@Composable
fun LiveTab(context: Context) {
    val presenter = remember { LivePresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var tmallLive by remember { mutableStateOf<LiveStream?>(null) }
    var followedLives by remember { mutableStateOf<List<LiveStream>>(emptyList()) }
    var recommendedLives by remember { mutableStateOf<List<LiveStream>>(emptyList()) }
    var followedLiveCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        tmallLive = presenter.getTmallLiveStream()
        followedLives = presenter.getFollowedLiveStreams()
        recommendedLives = presenter.getRecommendedLiveStreams()
        followedLiveCount = presenter.getFollowedLiveCount()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部工具栏（固定，直播tab选中）
        user?.let { LiveTopBar(it) }

        // 底部滚动内容
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 天猫双十一直播
            item {
                tmallLive?.let { TmallLiveSection(it) }
            }

            // 我的关注
            item {
                if (followedLives.isNotEmpty()) {
                    FollowedUpMastersSection(followedLives[0], followedLiveCount)
                }
            }

            // 推荐标签栏
            item {
                RecommendedLiveHeader()
            }

            // 直播网格（2列）
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((recommendedLives.size / 2 * 280).dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recommendedLives) { live ->
                        LiveStreamCard(live)
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

/**
 * 顶部工具栏（直播页选中状态）
 */
@Composable
fun LiveTopBar(user: User) {
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
                    .clickable { /* TODO */ },
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
                        text = "我好像在哪见过你",
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
                            text = "199",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 第二行：导航标签（直播选中）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(text = "直播", isSelected = true)
            TabItem(text = "推荐", isSelected = false)
            TabItem(text = "热门", isSelected = false)
            TabItem(text = "动画", isSelected = false)
            TabItem(text = "影视", isSelected = false)
            TabItem(text = "S15", isSelected = false)
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
 * 天猫双十一直播区域
 */
@Composable
fun TmallLiveSection(liveStream: LiveStream) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        // 直播封面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { /* TODO: 进入直播间 */ }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${liveStream.coverImage}")
                    .crossfade(true)
                    .build(),
                contentDescription = liveStream.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 左上角直播标签
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                color = Color.Red.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "直播中",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // 右上角音量和静音图标
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "音量",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 标题和更多直播按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = liveStream.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Surface(
                modifier = Modifier.clickable { /* TODO */ },
                color = Color(0xFFFFE5EE),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "更多直播",
                    color = Color(0xFFFF6699),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * 我的关注区域
 */
@Composable
fun FollowedUpMastersSection(liveStream: LiveStream, liveCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        // 标题行
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "我的关注",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${liveCount}人正在直播",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Surface(
                modifier = Modifier.clickable { /* TODO */ },
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "全部",
                        fontSize = 12.sp,
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

        Spacer(modifier = Modifier.height(12.dp))

        // UP主信息
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: 进入直播间 */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // UP主头像
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${liveStream.upMasterAvatar}")
                        .crossfade(true)
                        .build(),
                    contentDescription = liveStream.upMasterName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // 直播中小图标
                Surface(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd),
                    color = Color.Red,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "直播中",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // UP主名称和直播信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = liveStream.upMasterName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = liveStream.title,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 推荐标签栏
 */
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

/**
 * 直播卡片
 */
@Composable
fun LiveStreamCard(liveStream: LiveStream) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* TODO: 进入直播间 */ }
    ) {
        // 直播封面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${liveStream.coverImage}")
                    .crossfade(true)
                    .build(),
                contentDescription = liveStream.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 左上角直播标签
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp),
                color = Color.Red.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "直播",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // 右下角观看人数
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = "观看",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = liveStream.getFormattedViewerCount(),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // 直播信息
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // UP主头像
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${liveStream.upMasterAvatar}")
                    .crossfade(true)
                    .build(),
                contentDescription = liveStream.upMasterName,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 标题和UP主名
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = liveStream.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = liveStream.upMasterName,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
