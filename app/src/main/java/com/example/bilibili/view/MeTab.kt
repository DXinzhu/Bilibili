package com.example.bilibili.view

import android.content.Context
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

/**
 * "我的"标签页
 * 按照MVP模式实现，展示用户信息和各种服务入口
 */
@Composable
fun MeTab(
    context: Context,
    onNavigateToConcern: () -> Unit = {},
    onNavigateToVip: () -> Unit = {},
    onNavigateToSetting: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val presenter = remember { MePresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部工具栏
            TopToolbar()

            // 中部信息栏（固定）
            user?.let { UserInfoSection(it, onNavigateToConcern, onNavigateToVip) }

            // 底部滚动列表
            BottomServiceList(onNavigateToSetting, onNavigateToHistory)
        }
    }
}

/**
 * 顶部工具栏
 */
@Composable
fun TopToolbar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 互连图标
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "互连",
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 扫一扫图标
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "扫一扫",
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 皮肤图标
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "皮肤",
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 夜间模式图标
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = "夜间模式",
                tint = Color.Gray
            )
        }
    }
}

/**
 * 用户信息区域（中部固定区域）
 */
@Composable
fun UserInfoSection(
    user: User,
    onNavigateToConcern: () -> Unit = {},
    onNavigateToVip: () -> Unit = {}
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
                    .border(2.dp, Color(0xFFFF6699), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 昵称和编辑按钮
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
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
                            text = "成为大会员",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6699)
                        )
                        Text(
                            text = "热播内容看不停 >",
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
@Composable
fun StatItem(count: Int, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

/**
 * 快捷功能项
 */
@Composable
fun QuickActionItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF00A1D6),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

/**
 * 底部服务列表（可滚动）
 */
@Composable
fun BottomServiceList(
    onNavigateToSetting: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
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
                    QuickActionItem(icon = Icons.Default.Download, label = "离线缓存", onClick = { /* TODO */ })
                    QuickActionItem(icon = Icons.Default.History, label = "历史记录", onClick = onNavigateToHistory)
                    QuickActionItem(icon = Icons.Default.Star, label = "我的收藏", onClick = { /* TODO */ })
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
@Composable
fun ServiceSection(
    title: String,
    hasPromo: Boolean = false,
    promoText: String = "",
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (hasPromo) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* TODO */ }
                    ) {
                        Text(
                            text = promoText,
                            fontSize = 12.sp,
                            color = Color(0xFFFF6699)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "更多",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * 服务网格
 */
@Composable
fun ServiceGrid(items: List<ServiceItem>) {
    Column {
        items.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { item ->
                    ServiceGridItem(
                        icon = item.icon,
                        label = item.label,
                        modifier = Modifier.weight(1f)
                    )
                }
                // 填充空白项
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            if (rowItems != items.chunked(4).last()) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * 服务网格项
 */
@Composable
fun ServiceGridItem(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { /* TODO */ }
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFFFF6699),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

/**
 * 服务列表项
 */
@Composable
fun ServiceListItem(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFFFF6699),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "前往",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * 服务项数据类
 */
data class ServiceItem(
    val icon: ImageVector,
    val label: String
)
