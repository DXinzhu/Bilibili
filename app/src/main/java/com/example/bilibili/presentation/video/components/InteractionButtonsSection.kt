package com.example.bilibili.presentation.video.components

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.data.model.UPMaster
import com.example.bilibili.data.model.Video
import com.example.bilibili.presentation.video.VideoPresenter
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun InteractionButtonsSection(
    video: Video,
    presenter: VideoPresenter,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onCoin: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit
) {
    var likeCount by remember { mutableStateOf(video.likeCount) }
    var coinCount by remember { mutableStateOf(video.coinCount) }
    var favoriteCount by remember { mutableStateOf(video.favoriteCount) }
    var shareCount by remember { mutableStateOf(video.shareCount) }
    var isLiked by remember { mutableStateOf(video.isLiked) }
    var isFavorited by remember { mutableStateOf(video.isFavorited) }

    // 记录视频互动数据显示
    LaunchedEffect(video.videoId) {
        BilibiliAutoTestLogger.logVideoStatsDisplayed(
            videoId = video.videoId,
            likeCount = video.likeCount,
            coinCount = video.coinCount
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 点赞
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    // 指令6,23: 记录点赞按钮点击
                    BilibiliAutoTestLogger.logLikeButtonClicked()
                    onLike()
                    isLiked = !isLiked
                    likeCount = if (isLiked) likeCount + 1 else maxOf(0, likeCount - 1)
                    BilibiliAutoTestLogger.logLikeStatusChanged(isLiked)
                }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                    contentDescription = "点赞",
                    tint = if (isLiked) Color(0xFFFF6699) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(likeCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 不喜欢
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onDislike()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbDownOffAlt,
                    contentDescription = "不喜欢",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "不喜欢",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 投币
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onCoin()
                    coinCount++
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "投币",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(coinCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 收藏
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    // 指令8: 记录收藏按钮点击
                    BilibiliAutoTestLogger.logFavoriteButtonClicked()
                    onFavorite()
                    isFavorited = !isFavorited
                    favoriteCount = if (isFavorited) favoriteCount + 1 else maxOf(0, favoriteCount - 1)
                    BilibiliAutoTestLogger.logFavoriteStatusChanged(isFavorited)
                }
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "收藏",
                    tint = if (isFavorited) Color(0xFFFFB800) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(favoriteCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 分享
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onShare()
                    shareCount++
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "分享",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(shareCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 话题标签区域
 */
