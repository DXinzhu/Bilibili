package com.example.bilibili.presentation.action.components

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
import com.example.bilibili.data.model.Post
import com.example.bilibili.data.model.PostType
import com.example.bilibili.data.model.UPMaster
import com.example.bilibili.presentation.action.ActionPresenter
import com.example.bilibili.common.utils.BilibiliAutoTestLogger

@Composable
fun TabSwitcher(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // 全部按钮
        TabButton(
            text = "全部",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )

        Spacer(modifier = Modifier.width(80.dp))

        // 视频按钮
        TabButton(
            text = "视频",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
    }
}

/**
 * 标签按钮
 */
