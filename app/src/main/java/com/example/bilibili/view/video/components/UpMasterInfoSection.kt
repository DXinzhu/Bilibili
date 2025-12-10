package com.example.bilibili.view.video.components

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
import com.example.bilibili.model.UPMaster
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.VideoPresenter
import com.example.bilibili.utils.BilibiliAutoTestLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun UpMasterInfoSection(
    upMaster: UPMaster,
    presenter: VideoPresenter
) {
    var isFollowed by remember { mutableStateOf(upMaster.isFollowed) }
    var fansCount by remember { mutableStateOf(upMaster.fansCount) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // UP主头像
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${upMaster.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = upMaster.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // UP主信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = upMaster.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${presenter.formatFansCount(fansCount)}  ${upMaster.videoCount}视频",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // 关注按钮
            Surface(
                modifier = Modifier.clickable {
                    // 指令18: 记录关注按钮点击
                    BilibiliAutoTestLogger.logFollowButtonClicked()
                    upMaster.toggleFollow()
                    isFollowed = upMaster.isFollowed
                    fansCount = upMaster.fansCount
                    BilibiliAutoTestLogger.logFollowStatusChanged(isFollowed)
                },
                shape = RoundedCornerShape(16.dp),
                color = if (isFollowed) Color(0xFFE0E0E0) else Color(0xFFFF6699)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isFollowed) "已关注" else "关注",
                        tint = if (isFollowed) Color.Gray else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFollowed) "已关注" else "关注",
                        fontSize = 13.sp,
                        color = if (isFollowed) Color.Gray else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
