package com.example.bilibili.view.content.components

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
