package com.example.bilibili.view

import android.content.Context
import androidx.compose.foundation.Image
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
import com.example.bilibili.model.Post
import com.example.bilibili.model.PostType
import com.example.bilibili.model.UPMaster
import com.example.bilibili.presenter.ActionPresenter

/**
 * 动态页面(关注页)
 * 按照MVP模式实现,展示关注UP主的动态
 */
@Composable
fun ActionTab(context: Context) {
    val presenter = remember { ActionPresenter(context) }
    var frequentUPMasters by remember { mutableStateOf<List<UPMaster>>(emptyList()) }
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=全部, 1=视频

    LaunchedEffect(Unit) {
        frequentUPMasters = presenter.getFrequentlyVisitedUPMasters()
        posts = presenter.getPosts()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏 - 固定不滚动
        ActionTopBar()

        // 可滚动内容区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 全部/视频切换按钮
            item {
                TabSwitcher(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // 最常访问栏
            item {
                FrequentlyVisitedSection(frequentUPMasters)
            }

            // 动态列表
            items(posts) { post ->
                when (post.type) {
                    PostType.VIDEO -> VideoPostCard(post)
                    PostType.TEXT -> TextPostCard(post)
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
 * 顶部栏 - "关注"标题 + 发布动态图标
 */
@Composable
fun ActionTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 关注标题
        Text(
            text = "关注",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // 发布动态图标
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "发布动态",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

/**
 * 全部/视频切换按钮
 */
@Composable
fun TabSwitcher(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // 全部按钮
        TabButton(
            text = "全部",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )

        Spacer(modifier = Modifier.width(80.dp))

        // 视频按钮
        TabButton(
            text = "视频",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
    }
}

/**
 * 标签按钮
 */
@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
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
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp),
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(1.5.dp)
            ) {}
        }
    }
}

/**
 * 最常访问栏
 */
@Composable
fun FrequentlyVisitedSection(upMasters: List<UPMaster>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        // 标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最常访问",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO */ }
            ) {
                Text(
                    text = "更多",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "更多",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // UP主头像列表
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(upMasters) { upMaster ->
                FrequentUPMasterItem(upMaster)
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * 最常访问UP主项
 */
@Composable
fun FrequentUPMasterItem(upMaster: UPMaster) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO */ }
    ) {
        // 头像
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${upMaster.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = upMaster.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            // 小红点(表示有新动态)
            Surface(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.TopEnd),
                color = Color(0xFFFF6699),
                shape = CircleShape
            ) {}
        }

        Spacer(modifier = Modifier.height(6.dp))

        // UP主名称
        Text(
            text = upMaster.name,
            fontSize = 12.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 视频动态卡片
 */
@Composable
fun VideoPostCard(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        // UP主信息行
        PostHeader(
            avatarUrl = post.upMasterAvatar,
            name = post.upMasterName,
            time = post.publishTime
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 视频封面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
                .clickable { /* TODO: 播放视频 */ }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/video/${post.videoCover}")
                    .crossfade(true)
                    .build(),
                contentDescription = post.content,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 左下角时长和播放次数
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.videoDuration,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = post.videoPlayCount,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 视频标题
        Text(
            text = post.content,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 互动按钮行
        PostActionBar(
            forwardCount = post.forwardCount,
            commentCount = post.commentCount,
            likeCount = post.likeCount,
            isLiked = post.isLiked
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * 文字动态卡片
 */
@Composable
fun TextPostCard(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        // UP主信息行
        PostHeader(
            avatarUrl = post.upMasterAvatar,
            name = post.upMasterName,
            time = post.publishTime
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 文字内容
        Text(
            text = post.content,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 配图(如果有)
        if (post.images.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/avatar/${post.images[0]}")
                    .crossfade(true)
                    .build(),
                contentDescription = "配图",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* TODO */ },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 互动按钮行
        PostActionBar(
            forwardCount = post.forwardCount,
            commentCount = post.commentCount,
            likeCount = post.likeCount,
            isLiked = post.isLiked
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * 动态头部(UP主信息)
 */
@Composable
fun PostHeader(avatarUrl: String, name: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // UP主头像
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${avatarUrl}")
                .crossfade(true)
                .build(),
            contentDescription = name,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { /* TODO */ },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 名称和时间
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 更多按钮
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 互动按钮行(转发、评论、点赞)
 */
@Composable
fun PostActionBar(
    forwardCount: Int,
    commentCount: Int,
    likeCount: Int,
    isLiked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 转发
        ActionButton(
            icon = Icons.Default.Share,
            count = forwardCount,
            onClick = { /* TODO */ }
        )

        // 评论
        ActionButton(
            icon = Icons.Default.ChatBubbleOutline,
            count = commentCount,
            onClick = { /* TODO */ }
        )

        // 点赞
        ActionButton(
            icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            count = likeCount,
            tint = if (isLiked) Color(0xFFFF6699) else Color.Gray,
            onClick = { /* TODO */ }
        )
    }
}

/**
 * 互动按钮
 */
@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    tint: Color = Color.Gray,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (count > 0) count.toString() else "",
            fontSize = 13.sp,
            color = tint
        )
    }
}
