package com.example.bilibili.view

import android.content.Context
import android.util.Log
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
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.CollectPresenter

/**
 * 收藏页面
 * 按照MVP模式实现，展示用户的收藏视频
 */
@Composable
fun CollectTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    val presenter = remember { CollectPresenter(context) }
    var collectedVideos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var selectedMainTab by remember { mutableStateOf("收藏") }
    var selectedSubTab by remember { mutableStateOf("视频") }

    LaunchedEffect(Unit) {
        // 记录进入收藏页面
        Log.d("BilibiliAutoTest", "FAVORITE_PAGE_ENTERED")

        // 加载收藏视频数据
        collectedVideos = presenter.getCollectedVideos()

        // 记录数据加载完成
        Log.d("BilibiliAutoTest", "FAVORITE_DATA_LOADED")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部标题栏
        CollectTopBar(
            selectedMainTab = selectedMainTab,
            onMainTabSelected = { selectedMainTab = it },
            onNavigateBack = onNavigateBack
        )

        // 子标签栏
        CollectSubTabBar(
            selectedSubTab = selectedSubTab,
            onSubTabSelected = { selectedSubTab = it }
        )

        // 视频列表
        CollectVideoList(
            videos = collectedVideos,
            presenter = presenter
        )
    }
}

/**
 * 顶部标题栏（收藏/追更）
 */
@Composable
fun CollectTopBar(
    selectedMainTab: String,
    onMainTabSelected: (String) -> Unit,
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧返回按钮
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 中间标签（收藏/追更）
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainTabItem(
                    text = "收藏",
                    isSelected = selectedMainTab == "收藏",
                    onClick = { onMainTabSelected("收藏") }
                )
                MainTabItem(
                    text = "追更",
                    isSelected = selectedMainTab == "追更",
                    onClick = { onMainTabSelected("追更") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 右侧占位，保持对称
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

/**
 * 主标签项
 */
@Composable
fun MainTabItem(
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
            fontSize = 18.sp,
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
 * 子标签栏（收藏夹/视频）
 */
@Composable
fun CollectSubTabBar(
    selectedSubTab: String,
    onSubTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧子标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SubTabItem(
                    text = "收藏夹",
                    isSelected = selectedSubTab == "收藏夹",
                    onClick = { onSubTabSelected("收藏夹") }
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
 * 子标签项
 */
@Composable
fun SubTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFFFE5F0) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6699) else Color.Gray,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
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

/**
 * 收藏视频列表项
 */
@Composable
fun CollectVideoItem(
    video: Video,
    presenter: CollectPresenter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 点击播放视频 */ }
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 左侧视频缩略图
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(90.dp)
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
                // 占位符
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

            // 时长标签（右下角）
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = "03:45", // 默认时长，可以从视频数据中获取
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // 右侧信息
        Column(
            modifier = Modifier
                .weight(1f)
                .height(90.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 视频标题
            Text(
                text = video.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            // 底部信息
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // UP主名称
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "UP主",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = video.upMasterName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // 播放量和评论数
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "播放量",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = presenter.formatViewCount(video.viewCount),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "评论数",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = presenter.formatCommentCount(video.commentCount),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // 更多操作按钮
        IconButton(
            onClick = { /* TODO: 更多操作 */ },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
