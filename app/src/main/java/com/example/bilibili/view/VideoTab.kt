package com.example.bilibili.view

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.widget.VideoView
import com.example.bilibili.utils.BilibiliAutoTestLogger
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.model.UPMaster
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.VideoPresenter

/**
 * 视频播放页面
 * 包含视频播放区、标签栏、UP主信息、视频信息、互动按钮、推荐视频列表
 * 支持简介/评论标签切换，无需跳转到独立页面
 */
@Composable
fun VideoTab(
    context: Context,
    videoId: String,
    onBack: () -> Unit = {}
) {
    val presenter = remember { VideoPresenter(context) }
    val contentPresenter = remember { com.example.bilibili.presenter.ContentPresenter(context) }
    var video by remember { mutableStateOf<Video?>(null) }
    var upMaster by remember { mutableStateOf<UPMaster?>(null) }
    var recommendedVideos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var comments by remember { mutableStateOf<List<com.example.bilibili.model.Comment>>(emptyList()) }
    var selectedTab by remember { mutableStateOf("简介") }
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<com.example.bilibili.model.Comment?>(null) }
    var isFullScreen by remember { mutableStateOf(false) }

    LaunchedEffect(videoId) {
        video = presenter.getVideoById(videoId)
        video?.let {
            upMaster = presenter.getUpMasterById(it.upMasterId)
            recommendedVideos = presenter.getRecommendedVideos(videoId)
        }
        comments = contentPresenter.getCommentsByVideoId(videoId)
        // 指令9,17,18,21,22,23: 记录视频播放页打开
        BilibiliAutoTestLogger.logVideoPlayerOpened(videoId)
    }

    // 全屏模式下只显示视频播放器
    if (isFullScreen) {
        VideoPlayerSharedSection(
            video = video,
            onBack = onBack,
            isFullScreen = isFullScreen,
            onFullScreenChange = { isFullScreen = it }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 视频播放区域（与ContentTab共享）
        VideoPlayerSharedSection(
            video = video,
            onBack = onBack,
            isFullScreen = isFullScreen,
            onFullScreenChange = { isFullScreen = it }
        )

        // 滚动内容区域
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF5F5F5))
        ) {
            // 标签栏（简介/评论）+ 弹幕按钮
            item {
                TabAndDanmakuSection(
                    selectedTab = selectedTab,
                    commentCount = video?.commentCount ?: 0,
                    onTabSelected = { tab -> selectedTab = tab }
                )
            }

            // 根据选中的标签显示不同的内容
            if (selectedTab == "简介") {
                // UP主信息区域
                item {
                    video?.let { v ->
                        upMaster?.let { up ->
                            UpMasterInfoSection(
                                upMaster = up,
                                presenter = presenter
                            )
                        }
                    }
                }

                // 视频标题和数据信息
                item {
                    video?.let { v ->
                        VideoInfoSection(
                            video = v,
                            presenter = presenter
                        )
                    }
                }

                // 互动按钮区域
                item {
                    video?.let { v ->
                        InteractionButtonsSection(
                            video = v,
                            presenter = presenter,
                            onLike = { v.toggleLike() },
                            onDislike = { v.toggleDislike() },
                            onCoin = { v.addCoin() },
                            onFavorite = { v.toggleFavorite() },
                            onShare = { v.markAsShared() }
                        )
                    }
                }

                // 话题标签区域
                item {
                    video?.let { v ->
                        if (v.tags.isNotEmpty()) {
                            TagsSection(tags = v.tags)
                        }
                    }
                }

                // 推荐视频列表
                items(recommendedVideos) { recommendedVideo ->
                    RecommendedVideoItem(
                        video = recommendedVideo,
                        presenter = presenter
                    )
                }
            } else {
                // 评论内容区域
                // 热门评论标题
                item {
                    HotCommentsHeader()
                }

                // 评论列表
                items(comments) { comment ->
                    VideoCommentItem(
                        comment = comment,
                        presenter = contentPresenter,
                        onReply = { replyComment ->
                            replyingTo = replyComment
                            selectedTab = "评论"
                        },
                        onLike = { updatedComment ->
                            val index = comments.indexOf(comment)
                            if (index != -1) {
                                comments = comments.toMutableList().apply {
                                    set(index, updatedComment)
                                }
                            }
                        }
                    )
                }
            }

            // 底部空白（为输入框留出空间）
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // 底部评论输入框（仅在评论标签页显示）
        if (selectedTab == "评论") {
            VideoBottomCommentInput(
                commentText = commentText,
                replyingTo = replyingTo,
                onCommentTextChange = { commentText = it },
                onSendComment = {
                    if (commentText.isNotBlank()) {
                        // 指令17: 记录评论输入和发送
                        BilibiliAutoTestLogger.logCommentInputText(commentText)
                        BilibiliAutoTestLogger.logSendButtonClicked()

                        if (replyingTo != null) {
                            // 发布回复
                            val reply = contentPresenter.addReply(replyingTo!!, videoId, commentText)
                            // 将回复添加到父评论的回复列表
                            val index = comments.indexOfFirst { it.commentId == replyingTo!!.commentId }
                            if (index != -1) {
                                val parentComment = comments[index]
                                parentComment.replyList.add(reply)
                                // 触发UI更新
                                comments = comments.toMutableList()
                            }
                            replyingTo = null
                        } else {
                            // 发布新评论
                            val newComment = contentPresenter.addComment(videoId, commentText)
                            comments = listOf(newComment) + comments
                        }

                        // 指令17: 记录评论发送成功
                        BilibiliAutoTestLogger.logCommentSentSuccess()
                        commentText = ""
                    }
                },
                onCancelReply = {
                    replyingTo = null
                    commentText = ""
                }
            )
        }
    }
}

/**
 * 视频播放区域（共享组件，供VideoTab和ContentTab使用）
 */
@Composable
fun VideoPlayerSharedSection(
    video: Video?,
    onBack: () -> Unit,
    isFullScreen: Boolean = false,
    onFullScreenChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var isUserSeeking by remember { mutableStateOf(false) }

    // 定期更新播放进度
    LaunchedEffect(isPlaying, videoViewRef) {
        while (isActive && isPlaying && videoViewRef != null) {
            videoViewRef?.let { videoView ->
                if (!isUserSeeking) {
                    currentPosition = videoView.currentPosition
                    if (duration == 0) {
                        duration = videoView.duration
                    }
                }
            }
            delay(100) // 每100ms更新一次
        }
    }

    // 全屏切换函数
    val toggleFullScreen: () -> Unit = {
        val activity = context as? Activity
        activity?.let {
            val newFullScreenState = !isFullScreen
            android.util.Log.d("VideoPlayer", "toggleFullScreen: $isFullScreen -> $newFullScreenState")

            if (newFullScreenState) {
                // 进入全屏模式
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val window = it.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController?.apply {
                    hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                // 退出全屏模式
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val window = it.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController?.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            }

            // 最后更新状态
            onFullScreenChange(newFullScreenState)
        }
    }

    Box(
        modifier = if (isFullScreen) {
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        }
    ) {
        // VideoView视频播放器
        video?.let { v ->
            if (v.videoPath.isNotEmpty()) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            try {
                                // 从 videoPath "video/1.mp4" 提取 "1" 并转换为 "video_1"
                                val fileName = v.videoPath.replace("video/", "").replace(".mp4", "")
                                val resourceName = "video_$fileName"
                                val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)

                                if (resId != 0) {
                                    // 如果资源存在，使用资源URI
                                    val videoUri = Uri.parse("android.resource://${context.packageName}/$resId")
                                    setVideoURI(videoUri)

                                    setOnPreparedListener { mediaPlayer ->
                                        mediaPlayer.isLooping = true
                                        start()
                                        isPlaying = true
                                        // 指令9,22,23: 记录视频开始播放
                                        BilibiliAutoTestLogger.logVideoPlaybackStarted()
                                    }

                                    setOnErrorListener { _, what, extra ->
                                        android.util.Log.e("VideoPlayer", "Error: what=$what, extra=$extra")
                                        false
                                    }
                                } else {
                                    // 资源不存在，记录错误
                                    android.util.Log.e("VideoPlayer", "Video resource not found: $fileName")
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("VideoPlayer", "Error loading video", e)
                            }
                            videoViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { videoView ->
                        videoViewRef = videoView
                    }
                )
            } else if (v.coverImage.isNotEmpty()) {
                // 如果没有视频路径，显示封面
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/${v.coverImage}")
                            .crossfade(true)
                            .build(),
                        contentDescription = v.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // 播放按钮图标
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "播放",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        // 顶部返回按钮和功能图标
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 返回按钮
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        if (isFullScreen) {
                            // 如果当前是全屏模式,先退出全屏
                            toggleFullScreen()
                        } else {
                            // 否则返回上一页
                            onBack()
                        }
                    }
            )

            // 右侧功能图标
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "首页",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = "音频",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.Cast,
                    contentDescription = "投屏",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "电视",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 底部播放控制
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // 进度条
            if (duration > 0) {
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { newValue ->
                        isUserSeeking = true
                        currentPosition = newValue.toInt()
                    },
                    onValueChangeFinished = {
                        videoViewRef?.seekTo(currentPosition)
                        isUserSeeking = false
                    },
                    valueRange = 0f..duration.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF6699),
                        activeTrackColor = Color(0xFFFF6699),
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
            }

            // 播放控制行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 播放/暂停按钮
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "暂停" else "播放",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                videoViewRef?.let { videoView ->
                                    if (isPlaying) {
                                        // 指令14,21,22: 记录暂停操作
                                        BilibiliAutoTestLogger.logPauseButtonClicked()
                                        videoView.pause()
                                        isPlaying = false
                                        BilibiliAutoTestLogger.logVideoPaused()
                                    } else {
                                        videoView.start()
                                        isPlaying = true
                                        BilibiliAutoTestLogger.logVideoPlaybackStarted()
                                    }
                                }
                            }
                    )

                    // 时间显示
                    Text(
                        text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                // 全屏按钮
                Icon(
                    imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = if (isFullScreen) "退出全屏" else "全屏",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            if (!isFullScreen) {
                                // 指令11: 记录进入全屏
                                BilibiliAutoTestLogger.logFullscreenButtonClicked()
                            }
                            toggleFullScreen()
                            if (isFullScreen) {
                                BilibiliAutoTestLogger.logFullscreenModeEntered()
                            }
                        }
                )
            }
        }
    }
}

/**
 * 视频播放区域（旧版,保留用于兼容）
 */
@Composable
fun VideoPlayerSection(
    video: Video?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isFullScreen by remember { mutableStateOf(false) }

    // 全屏切换函数
    val toggleFullScreen: () -> Unit = {
        val activity = context as? Activity
        activity?.let {
            isFullScreen = !isFullScreen
            if (isFullScreen) {
                // 进入全屏模式
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val window = it.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController?.apply {
                    hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                // 退出全屏模式
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val window = it.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController?.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
    ) {
        // VideoView视频播放器
        video?.let { v ->
            if (v.videoPath.isNotEmpty()) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            try {
                                // 从 videoPath "video/1.mp4" 提取 "1" 并转换为 "video_1"
                                val fileName = v.videoPath.replace("video/", "").replace(".mp4", "")
                                val resourceName = "video_$fileName"
                                val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)

                                if (resId != 0) {
                                    // 如果资源存在，使用资源URI
                                    val videoUri = Uri.parse("android.resource://${context.packageName}/$resId")
                                    setVideoURI(videoUri)

                                    setOnPreparedListener { mediaPlayer ->
                                        mediaPlayer.isLooping = true
                                        start()
                                        isPlaying = true
                                        // 指令9,22,23: 记录视频开始播放
                                        BilibiliAutoTestLogger.logVideoPlaybackStarted()
                                    }

                                    setOnErrorListener { _, what, extra ->
                                        android.util.Log.e("VideoPlayer", "Error: what=$what, extra=$extra")
                                        false
                                    }
                                } else {
                                    // 资源不存在，记录错误
                                    android.util.Log.e("VideoPlayer", "Video resource not found: $fileName")
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("VideoPlayer", "Error loading video", e)
                            }
                            videoViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { videoView ->
                        videoViewRef = videoView
                    }
                )
            } else if (v.coverImage.isNotEmpty()) {
                // 如果没有视频路径，显示封面
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/${v.coverImage}")
                            .crossfade(true)
                            .build(),
                        contentDescription = v.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // 播放按钮图标
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "播放",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        // 顶部返回按钮和功能图标
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 返回按钮
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBack() }
            )

            // 右侧功能图标
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "首页",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = "音频",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.Cast,
                    contentDescription = "投屏",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "电视",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 底部播放控制
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 播放/暂停按钮
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "暂停" else "播放",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        videoViewRef?.let { videoView ->
                            if (isPlaying) {
                                videoView.pause()
                                isPlaying = false
                            } else {
                                videoView.start()
                                isPlaying = true
                            }
                        }
                    }
            )

            // 时间和全屏
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "00:01/02:59",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Icon(
                    imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = if (isFullScreen) "退出全屏" else "全屏",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { toggleFullScreen() }
                )
            }
        }
    }
}

/**
 * 标签栏和弹幕按钮区域
 */
@Composable
fun TabAndDanmakuSection(
    selectedTab: String,
    commentCount: Int,
    onTabSelected: (String) -> Unit
) {
    // 弹幕开关状态
    var isDanmakuEnabled by remember { mutableStateOf(true) }

    // 指令26: 记录弹幕初始状态（仅首次）
    LaunchedEffect(Unit) {
        BilibiliAutoTestLogger.logDanmakuInitialState(isDanmakuEnabled)
    }

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
                Text(
                    text = "评论 $commentCount",
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == "评论") FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == "评论") Color(0xFFFF6699) else Color.Gray,
                    modifier = Modifier.clickable {
                        // 指令17,20,29: 记录进入评论页面
                        BilibiliAutoTestLogger.logCommentPageEntered()
                        onTabSelected("评论")
                    }
                )
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

                // 弹幕开关按钮 - 带开关功能
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            // 指令26: 记录弹幕开关点击
                            BilibiliAutoTestLogger.logDanmakuSwitchClicked()
                            isDanmakuEnabled = !isDanmakuEnabled
                            // 指令26: 记录弹幕状态改变
                            BilibiliAutoTestLogger.logDanmakuStatusChanged(isDanmakuEnabled)
                        },
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isDanmakuEnabled) Color(0xFFFF6699) else Color(0xFFCCCCCC)
                    ),
                    color = if (isDanmakuEnabled) Color.White else Color(0xFFF5F5F5)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "弹",
                            fontSize = 12.sp,
                            color = if (isDanmakuEnabled) Color(0xFFFF6699) else Color(0xFFCCCCCC),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * UP主信息区域
 */
@Composable
fun UpMasterInfoSection(
    upMaster: UPMaster,
    presenter: VideoPresenter
) {
    var isFollowed by remember { mutableStateOf(upMaster.isFollowed) }
    var fansCount by remember { mutableStateOf(upMaster.fansCount) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // UP主头像
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${upMaster.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = upMaster.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // UP主信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = upMaster.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${presenter.formatFansCount(fansCount)}  ${upMaster.videoCount}视频",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // 关注按钮
            Surface(
                modifier = Modifier.clickable {
                    // 指令18: 记录关注按钮点击
                    BilibiliAutoTestLogger.logFollowButtonClicked()
                    upMaster.toggleFollow()
                    isFollowed = upMaster.isFollowed
                    fansCount = upMaster.fansCount
                    BilibiliAutoTestLogger.logFollowStatusChanged(isFollowed)
                },
                shape = RoundedCornerShape(16.dp),
                color = if (isFollowed) Color(0xFFE0E0E0) else Color(0xFFFF6699)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isFollowed) "已关注" else "关注",
                        tint = if (isFollowed) Color.Gray else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFollowed) "已关注" else "关注",
                        fontSize = 13.sp,
                        color = if (isFollowed) Color.Gray else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * 格式化时间（毫秒转换为 mm:ss 格式）
 */
fun formatTime(timeMs: Int): String {
    if (timeMs <= 0) return "00:00"
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

/**
 * 视频信息区域
 */
@Composable
fun VideoInfoSection(
    video: Video,
    presenter: VideoPresenter
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 视频标题
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = "热门",
                    tint = Color(0xFFFF6699),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = video.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 视频数据信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "播放",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = presenter.formatViewCount(video.viewCount),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "评论",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = video.commentCount.toString(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = presenter.formatPublishTime(video.createdTime),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = "在线",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = presenter.formatOnlineViewers(video.onlineViewers),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 互动按钮区域
 */
@Composable
fun InteractionButtonsSection(
    video: Video,
    presenter: VideoPresenter,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onCoin: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit
) {
    var likeCount by remember { mutableStateOf(video.likeCount) }
    var coinCount by remember { mutableStateOf(video.coinCount) }
    var favoriteCount by remember { mutableStateOf(video.favoriteCount) }
    var shareCount by remember { mutableStateOf(video.shareCount) }
    var isLiked by remember { mutableStateOf(video.isLiked) }
    var isFavorited by remember { mutableStateOf(video.isFavorited) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 点赞
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    // 指令6,23: 记录点赞按钮点击
                    BilibiliAutoTestLogger.logLikeButtonClicked()
                    onLike()
                    isLiked = !isLiked
                    likeCount = if (isLiked) likeCount + 1 else maxOf(0, likeCount - 1)
                    BilibiliAutoTestLogger.logLikeStatusChanged(isLiked)
                }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                    contentDescription = "点赞",
                    tint = if (isLiked) Color(0xFFFF6699) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(likeCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 不喜欢
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onDislike()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbDownOffAlt,
                    contentDescription = "不喜欢",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "不喜欢",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 投币
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onCoin()
                    coinCount++
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "投币",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(coinCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 收藏
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    // 指令8: 记录收藏按钮点击
                    BilibiliAutoTestLogger.logFavoriteButtonClicked()
                    onFavorite()
                    isFavorited = !isFavorited
                    favoriteCount = if (isFavorited) favoriteCount + 1 else maxOf(0, favoriteCount - 1)
                    BilibiliAutoTestLogger.logFavoriteStatusChanged(isFavorited)
                }
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "收藏",
                    tint = if (isFavorited) Color(0xFFFFB800) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(favoriteCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 分享
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onShare()
                    shareCount++
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "分享",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = presenter.formatCount(shareCount),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 话题标签区域
 */
@Composable
fun TagsSection(tags: List<String>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalOffer,
                contentDescription = "话题",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = tags.joinToString("  "),
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 推荐视频列表项
 */
@Composable
fun RecommendedVideoItem(
    video: Video,
    presenter: VideoPresenter
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { /* TODO: 跳转到视频播放页面 */ },
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 视频缩略图
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
            ) {
                if (video.coverImage.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/${video.coverImage}")
                            .crossfade(true)
                            .build(),
                        contentDescription = video.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "播放",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 视频信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 标题
                Text(
                    text = video.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // UP主和数据
                Column {
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
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "播放",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = presenter.formatViewCount(video.viewCount),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = "评论",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = video.commentCount.toString(),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // 更多按钮
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Top)
            )
        }
    }
}

/**
 * 视频评论项（直接使用ContentTab的CommentItem组件）
 */
@Composable
fun VideoCommentItem(
    comment: com.example.bilibili.model.Comment,
    presenter: com.example.bilibili.presenter.ContentPresenter,
    onReply: (com.example.bilibili.model.Comment) -> Unit,
    onLike: (com.example.bilibili.model.Comment) -> Unit
) {
    CommentItem(
        comment = comment,
        presenter = presenter,
        onReply = onReply,
        onLike = onLike
    )
}

/**
 * 视频页底部评论输入框
 */
@Composable
fun VideoBottomCommentInput(
    commentText: String,
    replyingTo: com.example.bilibili.model.Comment?,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onCancelReply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 如果正在回复，显示回复提示
        if (replyingTo != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F8F8)),
                color = Color(0xFFF8F8F8)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = "回复",
                            tint = Color(0xFF6B8EFF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "回复 ${replyingTo.authorName}",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "取消回复",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onCancelReply() }
                    )
                }
            }
        }

        // 输入框区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 头像
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/avatar/spring.jpg")
                        .crossfade(true)
                        .build(),
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // 输入框
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    color = Color(0xFFF8F8F8)
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = onCommentTextChange,
                        placeholder = {
                            Text(
                                text = if (replyingTo != null) "回复 ${replyingTo.authorName}" else "发一条友善的评论",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                // 表情按钮
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "表情",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* TODO: 表情选择 */ }
                )

                // 发送按钮
                Surface(
                    modifier = Modifier
                        .clickable(enabled = commentText.isNotBlank()) { onSendComment() }
                        .background(
                            if (commentText.isNotBlank()) Color(0xFF6B8EFF) else Color(0xFFE0E0E0),
                            CircleShape
                        )
                        .size(32.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "发送",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
