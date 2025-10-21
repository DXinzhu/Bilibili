package com.example.bilibili.view

import android.content.Context
import androidx.compose.foundation.background
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.model.UPMaster
import com.example.bilibili.presenter.ConcernPresenter

/**
 * 关注页面
 * 展示用户关注的UP主列表
 */
@Composable
fun ConcernTab(
    context: Context,
    onBack: () -> Unit
) {
    val presenter = remember { ConcernPresenter(context) }
    var upMasters by remember { mutableStateOf<List<UPMaster>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=关注, 1=粉丝
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        upMasters = presenter.getConcernPageUPMasters()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部标题栏 - 固定
        ConcernTopBar(onBack = onBack)

        // 标签栏 - 固定
        ConcernTabBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // 搜索栏 - 固定
        ConcernSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query
                upMasters = presenter.searchFollowedUPMasters(query)
            }
        )

        // 关注列表区域 - 可滚动
        ConcernList(
            upMasters = upMasters,
            followedCount = presenter.getFollowedCount()
        )
    }
}

/**
 * 顶部标题栏
 */
@Composable
fun ConcernTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
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

        Spacer(modifier = Modifier.width(8.dp))

        // 关闭按钮
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 我的好友标题
        Text(
            text = "我的好友",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

/**
 * 标签栏 - 关注/粉丝
 */
@Composable
fun ConcernTabBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // 关注标签
        ConcernTabItem(
            text = "关注",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )

        Spacer(modifier = Modifier.width(80.dp))

        // 粉丝标签
        ConcernTabItem(
            text = "粉丝",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

/**
 * 标签项
 */
@Composable
fun ConcernTabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6699) else Color.Gray
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                modifier = Modifier
                    .width(32.dp)
                    .height(3.dp),
                color = Color(0xFFFF6699),
                shape = RoundedCornerShape(1.5.dp)
            ) {}
        }
    }
}

/**
 * 搜索栏
 */
@Composable
fun ConcernSearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "搜索我的关注" else searchQuery,
                    fontSize = 14.sp,
                    color = if (searchQuery.isEmpty()) Color.Gray else Color.Black
                )
            }
        }
    }
}

/**
 * 关注列表
 */
@Composable
fun ConcernList(upMasters: List<UPMaster>, followedCount: Int) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 标题行
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "我的关注",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${followedCount}人",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO: 按最近关注排序 */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "排序",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "最近关注",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // UP主列表
        items(upMasters) { upMaster ->
            UPMasterItem(upMaster)
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                color = Color.LightGray,
                thickness = 0.5.dp
            )
        }

        // 底部空白
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * UP主列表项
 */
@Composable
fun UPMasterItem(upMaster: UPMaster) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 进入UP主主页 */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：UP主头像
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${upMaster.avatarUrl}")
                .crossfade(true)
                .build(),
            contentDescription = upMaster.name,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 中间：UP主名称和简介
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = upMaster.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = upMaster.description,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧：已关注按钮
        Surface(
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .clickable { /* TODO: 取消关注 */ }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "已关注",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "已关注",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
