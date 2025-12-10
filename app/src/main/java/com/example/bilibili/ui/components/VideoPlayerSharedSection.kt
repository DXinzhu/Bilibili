package com.example.bilibili.ui.components

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.data.model.Video
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
import com.example.bilibili.common.utils.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

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
            delay(100)
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
                                val fileName = v.videoPath.replace("video/", "").replace(".mp4", "")
                                val resourceName = "video_$fileName"
                                val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)

                                if (resId != 0) {
                                    val videoUri = Uri.parse("android.resource://${context.packageName}/$resId")
                                    setVideoURI(videoUri)

                                    setOnPreparedListener { mediaPlayer ->
                                        mediaPlayer.isLooping = true
                                        start()
                                        isPlaying = true
                                        BilibiliAutoTestLogger.logVideoPlaybackStarted()
                                    }

                                    setOnErrorListener { _, what, extra ->
                                        android.util.Log.e("VideoPlayer", "Error: what=$what, extra=$extra")
                                        false
                                    }
                                } else {
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
                            toggleFullScreen()
                        } else {
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
