package com.example.bilibili.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.bilibili.model.User
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.VipPresenter
import com.example.bilibili.presenter.VipPrivilege

/**
 * 会员中心页面
 * 展示会员信息、特权、专享内容等
 */
@Composable
fun VipTab(
    context: Context,
    onBack: () -> Unit
) {
    val presenter = remember { VipPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var vipContent by remember { mutableStateOf<List<Video>>(emptyList()) }
    var vipPrivileges by remember { mutableStateOf<List<VipPrivilege>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf(0) } // 0=猜你喜欢, 1=番剧, 2=电影...
    var agreeToTerms by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        vipContent = presenter.getVipExclusiveContent()
        vipPrivileges = presenter.getVipPrivileges()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏 - 固定
        VipTopBar(onBack = onBack)

        // 可滚动内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 卡通形象装饰区域
            item {
                VipDecorativeSection()
            }

            // 用户信息区域
            item {
                user?.let { VipUserSection(it) }
            }

            // 大会员特权区域
            item {
                VipPrivilegesSection(vipPrivileges)
            }

            // 广告横幅区域
            item {
                VipAdvertisementBanner()
            }

            // 大会员专享内容区域
            item {
                VipExclusiveContentSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    vipContent = vipContent
                )
            }

            // 连续包年区域
            item {
                VipAnnualSubscriptionSection(
                    agreeToTerms = agreeToTerms,
                    onAgreeChanged = { agreeToTerms = it }
                )
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * 顶部导航栏
 */
@Composable
fun VipTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = Color.White.copy(alpha = 0.95f)
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回箭头
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black
            )
        }

        // 会员中心标题
        Text(
            text = "会员中心",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // 右侧功能区：天气和更多
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 天气图标
            Surface(
                color = Color.Gray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "天气",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "7°",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 扫描图标
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "扫描",
                    tint = Color.Black
                )
            }

            // 更多图标
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多",
                    tint = Color.Black
                )
            }
        }
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

/**
 * 卡通形象装饰区域
 */
@Composable
fun VipDecorativeSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE5F0),
                        Color(0xFFFFF0F5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/video/vip1.jpg")
                .crossfade(true)
                .build(),
            contentDescription = "会员吉祥物",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * 用户信息区域
 */
@Composable
fun VipUserSection(user: User) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 用户名和开通状态
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (user.isVip) user.getVipStatusText() else "尚未开通",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // 立即开通按钮
            Surface(
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable { /* TODO: 开通会员 */ }
            ) {
                Text(
                    text = "立即开通",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                )
            }
        }
    }
}

/**
 * 大会员特权区域
 */
@Composable
fun VipPrivilegesSection(privileges: List<VipPrivilege>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "开通大会员获得以下${privileges.size}项特权",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO */ }
                ) {
                    Text(
                        text = "查看更多",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "查看更多",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 特权图标横向滚动列表
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(privileges.size) { index ->
                    VipPrivilegeItem(privileges[index])
                }
            }
        }
    }
}

/**
 * 特权项
 */
@Composable
fun VipPrivilegeItem(privilege: VipPrivilege) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .padding(vertical = 8.dp)
    ) {
        // 特权图标（粉色圆角方框）
        Surface(
            color = Color(0xFFFFE5F0),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = privilege.title,
                    tint = Color(0xFFFF6699),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = privilege.title,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 广告横幅区域
 */
@Composable
fun VipAdvertisementBanner() {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/video/vip2.jpg")
            .crossfade(true)
            .build(),
        contentDescription = "会员广告",
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { /* TODO: 跳转广告链接 */ },
        contentScale = ContentScale.Crop
    )
}

/**
 * 大会员专享内容区域
 */
@Composable
fun VipExclusiveContentSection(
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit,
    vipContent: List<Video>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题
            Text(
                text = "大会员专享",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 分类标签
            val categories = listOf("猜你喜欢", "番剧", "电影", "电视剧", "国创", "纪录片")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories.size) { index ->
                    VipCategoryTab(
                        text = categories[index],
                        isSelected = selectedCategory == index,
                        onClick = { onCategorySelected(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 内容卡片（横向滚动）
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vipContent) { video ->
                    VipContentCard(video)
                }
            }
        }
    }
}

/**
 * 分类标签
 */
@Composable
fun VipCategoryTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFFFF6699) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * 内容卡片
 */
@Composable
fun VipContentCard(video: Video) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { /* TODO: 播放视频 */ }
    ) {
        // 封面图
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${video.coverImage}")
                    .crossfade(true)
                    .build(),
                contentDescription = video.title,
                modifier = Modifier
                    .width(140.dp)
                    .height(186.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // 左上角"出品"标签
            Surface(
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "出品",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // 右下角评分
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "纪录片",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${9.0 + (video.ranking ?: 0) * 0.3}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 标题
        Text(
            text = video.title,
            fontSize = 13.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 连续包年区域
 */
@Composable
fun VipAnnualSubscriptionSection(
    agreeToTerms: Boolean,
    onAgreeChanged: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "连续包年",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "展开",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 自动续费说明
            Text(
                text = "自动续费可随时取消",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 支付按钮和优惠倒计时
            Column {
                // 优惠倒计时
                Text(
                    text = "优惠限时23:59:49",
                    fontSize = 12.sp,
                    color = Color(0xFFFF6699),
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 支付按钮
                Surface(
                    color = Color(0xFFFF6699),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: 支付 */ }
                ) {
                    Text(
                        text = "确认协议并支付¥148",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 14.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 协议勾选框
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAgreeChanged(!agreeToTerms) }
            ) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = onAgreeChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFF6699),
                        uncheckedColor = Color.Gray
                    ),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "开通前请阅读《大会员服务协议》《大会员自动续费服务规则》",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
