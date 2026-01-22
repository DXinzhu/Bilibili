package com.example.bilibili_sim.presentation.content.components

import android.content.Context
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili_sim.data.model.Comment
import com.example.bilibili_sim.data.model.Video
import com.example.bilibili_sim.presentation.content.ContentPresenter

@Composable
fun ContentTabAndDanmakuSection(
    selectedTab: String,
    commentCount: Int,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧标签栏
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 简介标签
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "简介",
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == "简介") FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == "简介") Color(0xFFFF6699) else Color.Gray,
                        modifier = Modifier.clickable { onTabSelected("简介") }
                    )
                    if (selectedTab == "简介") {
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

                // 评论标签
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "评论 $commentCount",
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == "评论") FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == "评论") Color(0xFFFF6699) else Color.Gray,
                        modifier = Modifier.clickable { onTabSelected("评论") }
                    )
                    if (selectedTab == "评论") {
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

            // 右侧弹幕按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .clickable { /* TODO: 发弹幕 */ },
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    color = Color.White
                ) {
                    Text(
                        text = "点我发弹幕",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { /* TODO: 弹幕开关 */ },
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6699)),
                    color = Color.White
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "弹",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6699),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 热门评论标题
 */
