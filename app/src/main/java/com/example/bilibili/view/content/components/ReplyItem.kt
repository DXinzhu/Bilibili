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
