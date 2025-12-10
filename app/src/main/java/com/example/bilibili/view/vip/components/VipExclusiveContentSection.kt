package com.example.bilibili.view.vip.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.bilibili.model.User
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.VipPresenter
import com.example.bilibili.presenter.VipPrivilege
import com.example.bilibili.utils.BilibiliAutoTestLogger

@Composable
fun VipExclusiveContentSection(
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit,
    vipContent: List<Video>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题
            Text(
                text = "大会员专享",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 分类标签
            val categories = listOf("猜你喜欢", "番剧", "电影", "电视剧", "国创", "纪录片")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories.size) { index ->
                    VipCategoryTab(
                        text = categories[index],
                        isSelected = selectedCategory == index,
                        onClick = { onCategorySelected(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 内容卡片（横向滚动）
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vipContent) { video ->
                    VipContentCard(video)
                }
            }
        }
    }
}

/**
 * 分类标签
 */
