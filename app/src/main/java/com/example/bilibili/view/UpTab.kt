package com.example.bilibili.view

import android.content.Context
import com.example.bilibili.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
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
import com.example.bilibili.model.UPMaster
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.UpPresenter

/**
 * UP主主页页面
 * 展示UP主的详细信息和投稿视频
 */
@Composable
fun UpTab(
    context: Context,
    upMasterId: String,
    onBack: () -> Unit
) {
    val presenter = remember { UpPresenter(context) }
    var upMaster by remember { mutableStateOf<UPMaster?>(null) }
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=主页, 1=动态, 2=投稿
    var selectedVideoCategory by remember { mutableStateOf(0) } // 0=视频, 1=图文, 2=私人致享

    LaunchedEffect(upMasterId) {
        upMaster = presenter.getUPMasterById(upMasterId)
        videos = presenter.getUPMasterVideos(upMasterId)
        // 指令12,19: 记录进入UP主主页
        upMaster?.let {
            BilibiliAutoTestLogger.logUploaderPageEntered(it.name)
            BilibiliAutoTestLogger.logUploaderDataLoaded()
            // 指令12: 记录显示粉丝数
            BilibiliAutoTestLogger.logFansCountDisplayed(presenter.formatCount(it.fansCount))
        }
    }

    upMaster?.let { up ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 顶部导航栏 - 固定
            UpTopBar(onBack = onBack)

            // 内容区域 - 可滚动
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // 用户头像区
                item {
                    UpHeaderSection(
                        upMaster = up,
                        presenter = presenter,
                        onFollowClick = {
                            upMaster = up.copy(
                                isFollowed = !up.isFollowed,
                                fansCount = if (!up.isFollowed) up.fansCount + 1 else up.fansCount - 1
                            )
                        }
                    )
                }

                // 用户信息区
                item {
                    UpInfoSection(upMaster = up)
                }

                // 充电区域和小店入口
                item {
                    UpChargeSection()
                }

                // Tab导航栏
                item {
                    UpTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                // 投稿分类标签 (仅在投稿Tab显示)
                if (selectedTab == 2) {
                    item {
                        UpVideoCategoryBar(
                            selectedCategory = selectedVideoCategory,
                            onCategorySelected = { selectedVideoCategory = it }
                        )
                    }
                }

                // 根据选中的Tab显示不同内容
                when (selectedTab) {
                    0 -> {
                        // 主页内容 - 暂时显示空白
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("主页内容", color = Color.Gray)
                            }
                        }
                    }
                    1 -> {
                        // 动态内容 - 暂时显示空白
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("动态内容", color = Color.Gray)
                            }
                        }
                    }
                    2 -> {
                        // 投稿内容 - 显示视频列表
                        item {
                            UpVideoListHeader()
                        }
                        items(videos) { video ->
                            UpVideoItem(video = video, presenter = presenter)
                            HorizontalDivider(
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
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

/**
 * 顶部导航栏
 */
@Composable
fun UpTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 返回按钮
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black
            )
        }

        // 右侧功能按钮
        Row {
            IconButton(onClick = { /* TODO: 消息 */ }) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "消息",
                    tint = Color.Black
                )
            }
            IconButton(onClick = { /* TODO: 搜索 */ }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.Black
                )
            }
            IconButton(onClick = { /* TODO: 更多 */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多",
                    tint = Color.Black
                )
            }
        }
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

/**
 * 用户头像区域
 */
@Composable
fun UpHeaderSection(
    upMaster: UPMaster,
    presenter: UpPresenter,
    onFollowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 左侧头像
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${upMaster.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = upMaster.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 右侧统计数据
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 粉丝数
                    UpStatItem(
                        count = presenter.formatCount(upMaster.fansCount),
                        label = "粉丝"
                    )
                    // 关注数 (假设103)
                    UpStatItem(
                        count = "103",
                        label = "关注"
                    )
                    // 获赞数 (假设2782.4万)
                    UpStatItem(
                        count = "2782.4万",
                        label = "获赞"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 关注按钮
                Surface(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    color = if (upMaster.isFollowed) Color(0xFFF5F5F5) else Color(0xFFFF6699),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onFollowClick)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (upMaster.isFollowed) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (upMaster.isFollowed) "已关注" else "关注",
                            tint = if (upMaster.isFollowed) Color.Gray else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (upMaster.isFollowed) "已关注" else "关注",
                            fontSize = 14.sp,
                            color = if (upMaster.isFollowed) Color.Gray else Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 统计数据项
 */
@Composable
fun UpStatItem(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

/**
 * 用户信息区域
 */
@Composable
fun UpInfoSection(upMaster: UPMaster) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 用户名称和徽章
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = upMaster.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            // LIVE徽章
            Surface(
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "LIVE",
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            // 年度大会友徽章
            Surface(
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "年度大会友",
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 认证信息
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "认证",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "bilibili UP主认证：bilibili 知名UP主",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "详情",
                fontSize = 13.sp,
                color = Color(0xFF00A1D6),
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 合作信息
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "合作",
                tint = Color(0xFF00A1D6),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "找我官方合作 欢迎Hi-Fi&汽车&专业音响试听、国乐、合...",
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // IP属地和其他信息
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "IP属地",
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "IP属地：吉林",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "已实名",
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "已实名",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "学校",
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "中国传媒大学",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 充电区域和小店入口
 */
@Composable
fun UpChargeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 充电区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: 充电 */ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 充电用户头像列表 (示意3个头像)
                Row {
                    repeat(3) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "充电用户",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "等730人为TA充电",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // 充电按钮
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6699))
            ) {
                Text(
                    text = "充电",
                    fontSize = 14.sp,
                    color = Color(0xFFFF6699),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // 小店入口
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: 小店 */ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "小店",
                    tint = Color(0xFFFFAA00),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "小店",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "进入",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Tab导航栏
 */
@Composable
fun UpTabBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("主页", "动态", "投稿", "小店", "收藏", "追番")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(tabs.size) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = tabs[index],
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == index) Color(0xFFFF6699) else Color.Gray
                )
                if (selectedTab == index) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier
                            .width(32.dp)
                            .height(3.dp),
                        color = Color(0xFFFF6699),
                        shape = RoundedCornerShape(1.5.dp)
                    ) {}
                }
            }
        }
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

/**
 * 投稿分类标签栏
 */
@Composable
fun UpVideoCategoryBar(selectedCategory: Int, onCategorySelected: (Int) -> Unit) {
    val categories = listOf("视频", "图文", "私人致享", "邓紫棋", "刘燕姿", "林...")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        items(categories.size) { index ->
            Surface(
                color = if (selectedCategory == index) Color(0xFFFFEEF5) else Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = categories[index],
                    fontSize = 14.sp,
                    color = if (selectedCategory == index) Color(0xFFFF6699) else Color.Gray,
                    modifier = Modifier
                        .clickable { onCategorySelected(index) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 视频列表标题行
 */
@Composable
fun UpVideoListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "播放全部",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "播放全部",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "排序",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "最新发布",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 视频列表项
 */
@Composable
fun UpVideoItem(video: Video, presenter: UpPresenter) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { /* TODO: 播放视频 */ }
            .padding(16.dp)
    ) {
        // 左侧封面
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(100.dp)
        ) {
            if (video.coverImage.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${video.coverImage}")
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 占位符
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFF0F0F0)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "视频",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            // 右下角播放量和评论数
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "播放量",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = presenter.formatViewCount(video.viewCount),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "评论数",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = presenter.formatCommentCount(video.commentCount),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧信息
        Column(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 视频标题
            Text(
                text = video.title,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // 发布时间
            Text(
                text = presenter.formatRelativeTime(video.createdTime),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 更多按钮
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
