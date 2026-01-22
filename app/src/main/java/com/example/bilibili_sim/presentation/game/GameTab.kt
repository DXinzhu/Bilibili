package com.example.bilibili_sim.presentation.game

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bilibili_sim.data.model.Comment
import com.example.bilibili_sim.presentation.game.components.*
import com.example.bilibili_sim.data.model.Video
import com.example.bilibili_sim.presentation.game.GamePresenter
import com.example.bilibili_sim.common.utils.BilibiliAutoTestLogger

/**
 * 游戏搜索结果页面
 * 显示游戏解说相关的搜索结果
 */
@Composable
fun GameTab(
    context: Context,
    searchQuery: String = "游戏解说",
    onBack: () -> Unit = {},
    onNavigateToVideo: (String) -> Unit = {}
) {
    val presenter = remember { GamePresenter(context) }
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var allComments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("综合") }
    var searchText by remember { mutableStateOf(searchQuery) }

    // 分类列表
    val categories = listOf("综合", "番剧", "直播", "用户", "影视", "图文")

    LaunchedEffect(searchQuery) {
        videos = presenter.loadGameVideos(searchQuery)
        allComments = presenter.loadComments()
        // 指令20: 记录游戏搜索结果页面加载成功
        BilibiliAutoTestLogger.logGameSearchPageLoaded()
        // 指令20: 记录搜索结果数量
        BilibiliAutoTestLogger.logSearchResultsCountDisplayed(videos.size)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部搜索栏
        GameTopBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBack = onBack,
            onSearch = {
                // 根据搜索框中的文本重新搜索
                if (searchText.isNotBlank()) {
                    videos = presenter.loadGameVideos(searchText)
                }
            }
        )

        // 分类标签栏
        GameCategorySection(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // 视频列表
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            items(videos.size) { index ->
                val video = videos[index]
                GameItem(video, onClick = {
                    // 指令22: 如果是第一个视频，记录点击
                    if (index == 0) {
                        BilibiliAutoTestLogger.logFirstSearchResultClicked()
                    }
                    onNavigateToVideo(video.videoId)
                })
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
