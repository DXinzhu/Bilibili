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
fun VideoPostCard(post: Post, onNavigateToUnderDevelopment: () -> Unit = {}) {
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
            time = currentPost.publishTime,
            onNavigateToUnderDevelopment = onNavigateToUnderDevelopment
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 视频封面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
                .clickable {
                    // 指令15: 记录点击第一个动态
                    BilibiliAutoTestLogger.logFirstDynamicClicked()
                    BilibiliAutoTestLogger.logDynamicDetailOpened()
                    onNavigateToUnderDevelopment()
                }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/video/${currentPost.videoCover}")
                    .crossfade(true)
                    .build(),
                contentDescription = currentPost.content,
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
                    text = currentPost.videoDuration,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentPost.videoPlayCount,
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
            text = currentPost.content,
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
            },
            onNavigateToUnderDevelopment = onNavigateToUnderDevelopment
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * 文字动态卡片
 */
