package com.example.bilibili.view

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
import com.example.bilibili.model.Comment
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.GamePresenter

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

    LaunchedEffect(Unit) {
        videos = presenter.loadGameVideos()
        allComments = presenter.loadComments()
        // 记录游戏搜索结果页面加载成功
        Log.d("BilibiliAutoTest", "GAME_SEARCH_PAGE_LOADED: $searchQuery")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部搜索栏
        GameSearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBack = onBack,
            onSearch = { /* TODO: 搜索功能 */ }
        )

        // 分类导航栏
        CategoryNavigationBar(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // 视频列表
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            items(videos) { video ->
                VideoItemWithComment(
                    video = video,
                    comments = presenter.getCommentsForVideo(video.videoId, allComments),
                    presenter = presenter,
                    onClick = { onNavigateToVideo(video.videoId) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * 游戏搜索页面的顶部搜索栏
 */
@Composable
fun GameSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 搜索输入框
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(20.dp)
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
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        modifier = Modifier.weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 15.sp,
                            color = Color.Black
                        ),
                        singleLine = true
                    )
                    if (searchText.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "清除",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onSearchTextChange("") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 搜索按钮
            Text(
                text = "搜索",
                fontSize = 15.sp,
                color = Color(0xFFFF6699),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSearch() }
            )
        }
    }
}

/**
 * 分类导航栏
 */
@Composable
fun CategoryNavigationBar(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        color = Color.White
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(categories) { category ->
                CategoryTab(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) }
                )
            }

            // 筛选图标
            item {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "筛选",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* TODO: 筛选功能 */ }
                )
            }
        }
    }
}

/**
 * 分类标签
 */
@Composable
fun CategoryTab(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = category,
            fontSize = 15.sp,
            color = if (isSelected) Color(0xFFFF6699) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Divider(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp),
                color = Color(0xFFFF6699)
            )
        }
    }
}

/**
 * 视频条目（带评论）
 */
@Composable
fun VideoItemWithComment(
    video: Video,
    comments: List<Comment>,
    presenter: GamePresenter,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        // 视频主体部分
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 左侧视频缩略图
            Surface(
                modifier = Modifier
                    .width(160.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFE0E0E0)
            ) {
                if (video.coverImage.isNotEmpty()) {
                    AsyncImage(
                        model = "file:///android_asset/${video.coverImage}",
                        contentDescription = "视频封面",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "视频",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧视频信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 视频标题
                Text(
                    text = video.title,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Column {
                    // UP主信息
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "UP主",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = video.upMasterName,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 播放量和发布时间
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "播放量",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = presenter.formatViewCount(video.viewCount),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "· ${presenter.formatPublishTime(video.createdTime)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // 右侧更多按钮
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        // 评论部分（如果有评论）
        if (comments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            comments.firstOrNull()?.let { comment ->
                CommentItem(comment)
            }
        }
    }
}

/**
 * 评论条目
 */
@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // 评论图标
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "评论",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f)) {
            // 评论者名称
            Text(
                text = comment.authorName,
                fontSize = 12.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 评论内容
            Text(
                text = comment.content,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
