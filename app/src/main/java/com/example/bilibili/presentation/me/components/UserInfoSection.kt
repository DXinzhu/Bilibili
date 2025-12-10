package com.example.bilibili.presentation.me.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.data.model.User
import com.example.bilibili.presentation.me.MePresenter

@Composable
fun UserInfoSection(
    user: User,
    onNavigateToConcern: () -> Unit = {},
    onNavigateToVip: () -> Unit = {},
    onNavigateToPerson: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 第一行：头像、昵称、等级、会员状态、空间
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${user.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFFF6699), CircleShape)
                    .clickable(onClick = onNavigateToPerson),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 昵称和编辑按钮
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onNavigateToPerson)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // LV5标识
                    Surface(
                        color = Color(0xFFFF6699),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "LV${user.level}",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 会员状态
                if (user.isVip) {
                    Surface(
                        color = Color(0xFFFFE5F0),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = user.getVipStatusText(),
                            color = Color(0xFFFF6699),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // B币和硬币
                Text(
                    text = "B币: ${user.bCoins}  硬币: ${user.hardCoins}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // 空间按钮
            TextButton(onClick = { /* TODO */ }) {
                Text(text = user.space, color = Color.Gray)
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "前往空间",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 第二行：动态、关注、粉丝
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(count = user.dynamicCount, label = "动态", onClick = { /* TODO */ })
            HorizontalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp),
                color = Color.LightGray
            )
            StatItem(count = user.followingCount, label = "关注", onClick = onNavigateToConcern)
            HorizontalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp),
                color = Color.LightGray
            )
            StatItem(count = user.fansCount, label = "粉丝", onClick = { /* TODO */ })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 第三行：会员中心横幅
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToVip),
            color = Color(0xFFFFE5F0),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "会员",
                        tint = Color(0xFFFF6699),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (user.isVip) "大会员" else "成为大会员",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6699)
                        )
                        Text(
                            text = if (user.isVip) user.getVipExpiryText() else "热播内容看不停 >",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6699)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFF6699),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "会员中心",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * 统计数字项
 */
