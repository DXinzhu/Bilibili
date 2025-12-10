package com.example.bilibili.presentation.action

import android.content.Context
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.Image
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
import com.example.bilibili.data.model.Post
import com.example.bilibili.presentation.action.components.*
import com.example.bilibili.data.model.PostType
import com.example.bilibili.data.model.UPMaster
import com.example.bilibili.presentation.action.ActionPresenter

/**
 * 动态页面(关注页)
 * 按照MVP模式实现,展示关注UP主的动态
 */
@Composable
fun ActionTab(context: Context) {
    val presenter = remember { ActionPresenter(context) }
    var frequentUPMasters by remember { mutableStateOf<List<UPMaster>>(emptyList()) }
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=全部, 1=视频

    LaunchedEffect(Unit) {
        frequentUPMasters = presenter.getFrequentlyVisitedUPMasters()
        posts = presenter.getPosts()
        // 指令14,15: 记录进入关注页
        BilibiliAutoTestLogger.logFollowPageEntered()
        // 指令15: 记录动态列表加载完成
        BilibiliAutoTestLogger.logDynamicListLoaded()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏 - 固定不滚动
        ActionTopBar()

        // 可滚动内容区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 全部/视频切换按钮
            item {
                TabSwitcher(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // 最常访问栏
            item {
                FrequentlyVisitedSection(frequentUPMasters)
            }

            // 动态列表
            items(posts) { post ->
                when (post.type) {
                    PostType.VIDEO -> VideoPostCard(post)
                    PostType.TEXT -> TextPostCard(post)
                }
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 顶部栏 - "关注"标题 + 发布动态图标
 */
