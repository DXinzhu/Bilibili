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
import com.example.bilibili.view.video.components.*
import com.example.bilibili.view.video.components.formatTime
import com.example.bilibili.view.common.VideoPlayerSharedSection

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
