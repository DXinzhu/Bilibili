package com.example.bilibili.view.recommend.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.model.User
import com.example.bilibili.utils.BilibiliAutoTestLogger

/**
 * 顶部工具栏
 */
@Composable
fun TopBar(
    user: User,
    selectedTab: String = "推荐",
    onTabSelected: (String) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
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
                    .clickable { onSearchClick() },
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
                        text = "天后",
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
                            text = "99",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 第二行：导航标签
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(text = "直播", isSelected = selectedTab == "直播", onClick = { onTabSelected("直播") })
            TabItem(text = "推荐", isSelected = selectedTab == "推荐", onClick = { onTabSelected("推荐") })
            TabItem(text = "热门", isSelected = selectedTab == "热门", onClick = { onTabSelected("热门") })
            TabItem(text = "动画", isSelected = selectedTab == "动画", onClick = {
                // 指令5: 记录点击动画频道
                BilibiliAutoTestLogger.logAnimationChannelClicked()
                Log.d("BilibiliAutoTest", "CHANNEL_ICON_CLICKED: 动画")
                onTabSelected("动画")
            })
            TabItem(text = "影视", isSelected = selectedTab == "影视", onClick = { onTabSelected("影视") })
            TabItem(text = "S15", isSelected = selectedTab == "S15", onClick = { onTabSelected("S15") })
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
 * 标签项
 */
@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6699) else Color.Gray
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp),
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(1.5.dp)
            ) {}
        }
    }
}
