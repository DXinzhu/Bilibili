package com.example.bilibili.view

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
import com.example.bilibili.model.Comment
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.ContentPresenter

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
@Composable
fun VideoPlayerHeaderSection(
    video: Video?,
    presenter: ContentPresenter,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
    ) {
        // 视频封面
        video?.let { v ->
            if (v.coverImage.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${v.coverImage}")
                        .crossfade(true)
                        .build(),
                    contentDescription = v.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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

            // UP主名字和bilibili标识
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                video?.let { v ->
                    Text(
                        text = v.upMasterName,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier
                        .background(Color(0xFFFB7299), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    color = Color.Transparent
                ) {
                    Text(
                        text = "bilibili",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 播放按钮
        Box(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .clickable { /* TODO: 播放视频 */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "播放",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }

    // 广告区域
    if (video != null) {
        AdvertisementSection()
    }
}

/**
 * 广告区域
 */
@Composable
fun AdvertisementSection() {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "广告内容展示区域",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .background(Color(0xFFFB7299), shape = RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                        color = Color.Transparent
                    ) {
                        Text(
                            text = "广告",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "78.3万播放",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 内容页面（评论页）的标签栏和弹幕按钮区域
 */
@Composable
fun ContentTabAndDanmakuSection(
    selectedTab: String,
    commentCount: Int,
    onTabSelected: (String) -> Unit
) {
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "评论 $commentCount",
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == "评论") FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == "评论") Color(0xFFFF6699) else Color.Gray,
                        modifier = Modifier.clickable { onTabSelected("评论") }
                    )
                    if (selectedTab == "评论") {
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

                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { /* TODO: 弹幕开关 */ },
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6699)),
                    color = Color.White
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "弹",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6699),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 热门评论标题
 */
@Composable
fun HotCommentsHeader() {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "热门评论",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "按热度",
                    fontSize = 14.sp,
                    color = Color(0xFFFF6699)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "排序选项",
                    tint = Color(0xFFFF6699),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 评论项
 */
@Composable
fun CommentItem(
    comment: Comment,
    presenter: ContentPresenter,
    onReply: (Comment) -> Unit,
    onLike: (Comment) -> Unit
) {
    var isLiked by remember { mutableStateOf(comment.isLiked) }
    var likeCount by remember { mutableStateOf(comment.likeCount) }
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(Color.White),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 用户头像
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${presenter.getUserAvatarUrl(comment.authorId)}")
                        .crossfade(true)
                        .build(),
                    contentDescription = comment.authorName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // 用户名和等级
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comment.authorName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        // 等级标识
                        Surface(
                            modifier = Modifier
                                .background(
                                    when (presenter.getUserLevel(comment.authorId)) {
                                        in 6..Int.MAX_VALUE -> Color(0xFFFF6699)
                                        in 3..5 -> Color(0xFF6B8EFF)
                                        else -> Color(0xFF999999)
                                    },
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 3.dp, vertical = 1.dp),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = "LV${presenter.getUserLevel(comment.authorId)}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 评论内容
                    Text(
                        text = comment.content,
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 时间和互动按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = presenter.formatRelativeTime(comment.publishTime),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "来自广东",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "回复",
                                fontSize = 12.sp,
                                color = Color(0xFF6B8EFF),
                                modifier = Modifier.clickable { onReply(comment) }
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 点赞
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    isLiked = !isLiked
                                    likeCount = if (isLiked) likeCount + 1 else maxOf(0, likeCount - 1)
                                    onLike(comment.copy(isLiked = isLiked, likeCount = likeCount))
                                }
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                    contentDescription = "点赞",
                                    tint = if (isLiked) Color(0xFFFF6699) else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = presenter.formatCount(likeCount),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            // 点踩
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { /* TODO: 点踩 */ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDownOffAlt,
                                    contentDescription = "点踩",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // 更多
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "更多",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { /* TODO: 更多选项 */ }
                            )
                        }
                    }

                    // 回复区域
                    if (comment.replyList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8F8F8), RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            color = Color.Transparent
                        ) {
                            Column {
                                // 显示回复列表：如果已展开显示全部，否则只显示前2条
                                val repliesToShow = if (isExpanded) comment.replyList else comment.replyList.take(2)

                                repliesToShow.forEachIndexed { index, reply ->
                                    ReplyItem(
                                        reply = reply,
                                        presenter = presenter,
                                        onReply = { onReply(comment) }
                                    )
                                    if (index < repliesToShow.size - 1) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }

                                // 展开/收起按钮
                                if (comment.replyList.size > 2) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isExpanded) {
                                            "收起回复 ^"
                                        } else {
                                            "查看更多${comment.replyList.size - 2}条回复 >"
                                        },
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B8EFF),
                                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 回复项
 */
@Composable
fun ReplyItem(
    reply: Comment,
    presenter: ContentPresenter,
    onReply: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${presenter.getUserAvatarUrl(reply.authorId)}")
                .crossfade(true)
                .build(),
            contentDescription = reply.authorName,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reply.authorName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B8EFF)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "回复",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "原评论",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = reply.content,
                fontSize = 12.sp,
                color = Color.Black,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = presenter.formatRelativeTime(reply.publishTime),
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO: 点赞回复 */ }
                ) {
                    Icon(
                        imageVector = if (reply.isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                        contentDescription = "点赞",
                        tint = if (reply.isLiked) Color(0xFFFF6699) else Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = presenter.formatCount(reply.likeCount),
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "回复",
                    fontSize = 10.sp,
                    color = Color(0xFF6B8EFF),
                    modifier = Modifier.clickable { onReply() }
                )
            }
        }
    }
}

/**
 * 底部评论输入框
 */
@Composable
fun BottomCommentInput(
    commentText: String,
    replyingTo: Comment?,
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