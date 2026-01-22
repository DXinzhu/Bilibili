package com.example.bilibili_sim.presentation.up.components

import android.content.Context
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
import com.example.bilibili_sim.data.model.UPMaster
import com.example.bilibili_sim.data.model.Video
import com.example.bilibili_sim.presentation.up.UpPresenter
import com.example.bilibili_sim.common.utils.BilibiliAutoTestLogger

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
