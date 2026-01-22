package com.example.bilibili_sim.presentation.up

import android.content.Context
import com.example.bilibili_sim.common.utils.BilibiliAutoTestLogger
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
import com.example.bilibili_sim.presentation.up.components.*

/**
 * UP主主页页面
 * 展示UP主的详细信息和投稿视频
 */
@Composable
fun UpTab(
    context: Context,
    upMasterId: String,
    onBack: () -> Unit
) {
    val presenter = remember { UpPresenter(context) }
    var upMaster by remember { mutableStateOf<UPMaster?>(null) }
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=主页, 1=动态, 2=投稿
    var selectedVideoCategory by remember { mutableStateOf(0) } // 0=视频, 1=图文, 2=私人致享

    LaunchedEffect(upMasterId) {
        upMaster = presenter.getUPMasterById(upMasterId)
        videos = presenter.getUPMasterVideos(upMasterId)
        // 指令12,19: 记录进入UP主主页
        upMaster?.let {
            BilibiliAutoTestLogger.logUploaderPageEntered(it.name)
            BilibiliAutoTestLogger.logUploaderDataLoaded()
            // 指令12: 记录显示粉丝数
            BilibiliAutoTestLogger.logFansCountDisplayed(presenter.formatCount(it.fansCount))
        }
    }

    upMaster?.let { up ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 顶部导航栏 - 固定
            UpTopBar(onBack = onBack)

            // 内容区域 - 可滚动
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // 用户头像区
                item {
                    UpHeaderSection(
                        upMaster = up,
                        presenter = presenter,
                        onFollowClick = {
                            upMaster = up.copy(
                                isFollowed = !up.isFollowed,
                                fansCount = if (!up.isFollowed) up.fansCount + 1 else up.fansCount - 1
                            )
                        }
                    )
                }

                // 用户信息区
                item {
                    UpInfoSection(upMaster = up)
                }

                // 充电区域和小店入口
                item {
                    UpChargeSection()
                }

                // Tab导航栏
                item {
                    UpTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                // 投稿分类标签 (仅在投稿Tab显示)
                if (selectedTab == 2) {
                    item {
                        UpVideoCategoryBar(
                            selectedCategory = selectedVideoCategory,
                            onCategorySelected = { selectedVideoCategory = it }
                        )
                    }
                }

                // 根据选中的Tab显示不同内容
                when (selectedTab) {
                    0 -> {
                        // 主页内容 - 暂时显示空白
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("主页内容", color = Color.Gray)
                            }
                        }
                    }
                    1 -> {
                        // 动态内容 - 暂时显示空白
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("动态内容", color = Color.Gray)
                            }
                        }
                    }
                    2 -> {
                        // 投稿内容 - 显示视频列表
                        item {
                            UpVideoListHeader()
                        }
                        items(videos) { video ->
                            UpVideoItem(video = video, presenter = presenter)
                            HorizontalDivider(
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }

                // 底部空白
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

/**
 * 顶部导航栏
 */
