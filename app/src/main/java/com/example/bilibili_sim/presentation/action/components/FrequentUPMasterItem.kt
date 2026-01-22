package com.example.bilibili_sim.presentation.action.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili_sim.data.model.Post
import com.example.bilibili_sim.data.model.PostType
import com.example.bilibili_sim.data.model.UPMaster
import com.example.bilibili_sim.presentation.action.ActionPresenter
import com.example.bilibili_sim.common.utils.BilibiliAutoTestLogger

@Composable
fun FrequentUPMasterItem(upMaster: UPMaster, onNavigateToUnderDevelopment: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onNavigateToUnderDevelopment() }
    ) {
        // 头像
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${upMaster.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = upMaster.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            // 小红点(表示有新动态)
            Surface(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.TopEnd),
                color = Color(0xFFFF6699),
                shape = CircleShape
            ) {}
        }

        Spacer(modifier = Modifier.height(6.dp))

        // UP主名称
        Text(
            text = upMaster.name,
            fontSize = 12.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 视频动态卡片
 */
