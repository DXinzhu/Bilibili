package com.example.bilibili.presentation.content

import android.content.Context
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.data.model.Comment
import com.example.bilibili.data.model.Video
import com.example.bilibili.presentation.content.ContentPresenter
import com.example.bilibili.presentation.content.components.*
import com.example.bilibili.ui.components.VideoPlayerSharedSection

/**
 * 内容页面（评论页面）
 * 包含视频播放区域、广告区域、简介/评论标签栏、评论列表、评论输入框
 */
@Composable
fun ContentTab(
    context: Context,
    videoId: String,
    onBack: () -> Unit = {}
) {
    val presenter = remember { ContentPresenter(context) }
    var video by remember { mutableStateOf<Video?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var selectedTab by remember { mutableStateOf("评论") }
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }

    LaunchedEffect(videoId) {
        video = presenter.getVideoById(videoId)
        comments = presenter.getCommentsByVideoId(videoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 视频播放区域（简化版，只显示封面）
        VideoPlayerHeaderSection(
            video = video,
            presenter = presenter,
            onBack = onBack
        )

        // 滚动内容区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 标签栏（简介/评论）+ 弹幕按钮
            item {
                ContentTabAndDanmakuSection(
                    selectedTab = selectedTab,
                    commentCount = video?.commentCount ?: 0,
                    onTabSelected = { tab -> selectedTab = tab }
                )
            }

            // 热门评论标题
            if (selectedTab == "评论" && comments.isNotEmpty()) {
                item {
                    HotCommentsHeader()
                }
            }

            // 评论列表
            if (selectedTab == "评论") {
                items(comments) { comment ->
                    CommentItem(
                        comment = comment,
                        presenter = presenter,
                        onReply = { replyComment ->
                            replyingTo = replyComment
                            selectedTab = "评论"
                        },
                        onLike = { updatedComment ->
                            // 更新评论状态
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

        // 底部评论输入框
        BottomCommentInput(
            commentText = commentText,
            replyingTo = replyingTo,
            onCommentTextChange = { commentText = it },
            onSendComment = {
                if (commentText.isNotBlank()) {
                    if (replyingTo != null) {
                        // 发布回复
                        val reply = presenter.addReply(replyingTo!!, videoId, commentText)
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
                        val newComment = presenter.addComment(videoId, commentText)
                        comments = listOf(newComment) + comments
                        // 更新视频评论数
                        video = video?.copy(commentCount = (video?.commentCount ?: 0) + 1)
                    }
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

/**
 * 视频播放头部区域（简化版）
 */
