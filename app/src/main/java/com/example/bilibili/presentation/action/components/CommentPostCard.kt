package com.example.bilibili.presentation.action.components

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
import com.example.bilibili.data.model.Post
import com.example.bilibili.data.model.PostType
import com.example.bilibili.data.model.UPMaster
import com.example.bilibili.presentation.action.ActionPresenter
import com.example.bilibili.common.utils.BilibiliAutoTestLogger

@Composable
fun TextPostCard(post: Post) {
    var currentPost by remember { mutableStateOf(post) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        // UP主信息行
        PostHeader(
            avatarUrl = currentPost.upMasterAvatar,
            name = currentPost.upMasterName,
            time = currentPost.publishTime
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 文字内容
        Text(
            text = currentPost.content,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 配图(如果有)
        if (currentPost.images.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${currentPost.images[0]}")
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
            forwardCount = currentPost.forwardCount,
            commentCount = currentPost.commentCount,
            likeCount = currentPost.likeCount,
            isLiked = currentPost.isLiked,
            collectCount = currentPost.collectCount,
            isCollected = currentPost.isCollected,
            coinCount = currentPost.coinCount,
            isCoined = currentPost.isCoined,
            onLikeClick = {
                currentPost = currentPost.copy().apply { toggleLike() }
            },
            onCollectClick = {
                currentPost = currentPost.copy().apply { toggleCollect() }
            },
            onCoinClick = {
                currentPost = currentPost.copy().apply { addCoin() }
            }
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
 * 互动按钮行(转发、评论、点赞、收藏、投币)
 */
@Composable
fun PostActionBar(
    forwardCount: Int,
    commentCount: Int,
    likeCount: Int,
    isLiked: Boolean,
    collectCount: Int,
    isCollected: Boolean,
    coinCount: Int,
    isCoined: Boolean,
    onLikeClick: () -> Unit = {},
    onCollectClick: () -> Unit = {},
    onCoinClick: () -> Unit = {}
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
            onClick = onLikeClick
        )

        // 收藏
        ActionButton(
            icon = if (isCollected) Icons.Default.Star else Icons.Default.StarBorder,
            count = collectCount,
            tint = if (isCollected) Color(0xFFFFB800) else Color.Gray,
            onClick = onCollectClick
        )

        // 投币
        ActionButton(
            icon = Icons.Default.MonetizationOn,
            count = coinCount,
            tint = if (isCoined) Color(0xFFFFB800) else Color.Gray,
            onClick = onCoinClick
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
