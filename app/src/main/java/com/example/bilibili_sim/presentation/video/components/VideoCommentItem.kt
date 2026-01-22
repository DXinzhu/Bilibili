package com.example.bilibili_sim.presentation.video.components

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.VideoView
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili_sim.data.model.UPMaster
import com.example.bilibili_sim.data.model.Video
import com.example.bilibili_sim.presentation.video.VideoPresenter
import com.example.bilibili_sim.common.utils.BilibiliAutoTestLogger
import com.example.bilibili_sim.presentation.content.components.CommentItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun VideoCommentItem(
    comment: com.example.bilibili_sim.data.model.Comment,
    presenter: com.example.bilibili_sim.presentation.content.ContentPresenter,
    onReply: (com.example.bilibili_sim.data.model.Comment) -> Unit,
    onLike: (com.example.bilibili_sim.data.model.Comment) -> Unit
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
