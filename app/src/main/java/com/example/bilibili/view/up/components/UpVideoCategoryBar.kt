package com.example.bilibili.view.up.components

import android.content.Context
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
import com.example.bilibili.model.UPMaster
import com.example.bilibili.model.Video
import com.example.bilibili.presenter.UpPresenter
import com.example.bilibili.utils.BilibiliAutoTestLogger

@Composable
fun UpVideoCategoryBar(selectedCategory: Int, onCategorySelected: (Int) -> Unit) {
    val categories = listOf("视频", "图文", "私人致享", "邓紫棋", "刘燕姿", "林...")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        items(categories.size) { index ->
            Surface(
                color = if (selectedCategory == index) Color(0xFFFFEEF5) else Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = categories[index],
                    fontSize = 14.sp,
                    color = if (selectedCategory == index) Color(0xFFFF6699) else Color.Gray,
                    modifier = Modifier
                        .clickable { onCategorySelected(index) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 视频列表标题行
 */
