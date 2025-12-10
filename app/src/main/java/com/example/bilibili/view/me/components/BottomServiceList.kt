package com.example.bilibili.view.me.components

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
import com.example.bilibili.model.User
import com.example.bilibili.presenter.MePresenter

@Composable
fun BottomServiceList(
    onNavigateToSetting: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToCollect: () -> Unit = {},
    onNavigateToLoad: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 快捷功能区（离线缓存、历史记录、我的收藏、稍后再看）
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionItem(icon = Icons.Default.Download, label = "离线缓存", onClick = onNavigateToLoad)
                    QuickActionItem(icon = Icons.Default.History, label = "历史记录", onClick = onNavigateToHistory)
                    QuickActionItem(icon = Icons.Default.Star, label = "我的收藏", onClick = {
                        Log.d("BilibiliAutoTest", "FAVORITE_TAB_CLICKED")
                        onNavigateToCollect()
                    })
                    QuickActionItem(icon = Icons.Default.WatchLater, label = "稍后再看", onClick = { /* TODO */ })
                }
            }
        }

        // 发布视频提示
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { /* TODO */ },
                color = Color(0xFFFFE5F0),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFFF6699),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "UP",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "发布你的第一个视频",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "领限定头像挂件，赢活动奖金",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFFF6699),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "有奖发布",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 游戏中心
        item {
            ServiceSection(
                title = "游戏中心",
                hasPromo = true,
                promoText = "命运公测累登送200抽！"
            ) {
                ServiceGrid(
                    items = listOf(
                        ServiceItem(Icons.Default.SportsEsports, "我的游戏"),
                        ServiceItem(Icons.Default.Schedule, "我的预约"),
                        ServiceItem(Icons.Default.Gamepad, "找游戏"),
                        ServiceItem(Icons.Default.EmojiEvents, "游戏排行榜")
                    )
                )
            }
        }

        // 我的服务
        item {
            ServiceSection(title = "我的服务") {
                ServiceGrid(
                    items = listOf(
                        ServiceItem(Icons.Default.School, "我的课程"),
                        ServiceItem(Icons.Default.DataUsage, "免流量服务"),
                        ServiceItem(Icons.Default.Checkroom, "个性装扮"),
                        ServiceItem(Icons.Default.AccountBalanceWallet, "我的钱包"),
                        ServiceItem(Icons.Default.ShoppingBag, "会员购"),
                        ServiceItem(Icons.Default.LiveTv, "我的直播"),
                        ServiceItem(Icons.Default.Animation, "漫画"),
                        ServiceItem(Icons.Default.Whatshot, "必火推广"),
                        ServiceItem(Icons.Default.Lightbulb, "创作中心"),
                        ServiceItem(Icons.Default.Forum, "社区中心"),
                        ServiceItem(Icons.Default.Favorite, "哔哩哔哩公益"),
                        ServiceItem(Icons.Default.Store, "工房"),
                        ServiceItem(Icons.Default.LocalGasStation, "能量加油站")
                    )
                )
            }
        }

        // 更多服务
        item {
            ServiceSection(title = "更多服务") {
                Column {
                    ServiceListItem(icon = Icons.Default.Support, text = "联系客服")
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    ServiceListItem(icon = Icons.Default.Headphones, text = "听视频", onClick = { /* TODO */ })
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    ServiceListItem(icon = Icons.Default.ChildCare, text = "未成年人守护", onClick = { /* TODO */ })
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    ServiceListItem(icon = Icons.Default.Settings, text = "设置", onClick = onNavigateToSetting)
                }
            }
        }

        // 底部空白
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 服务区块
 */
