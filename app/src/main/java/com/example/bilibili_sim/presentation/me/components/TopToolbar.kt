package com.example.bilibili_sim.presentation.me.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili_sim.data.model.User
import com.example.bilibili_sim.presentation.me.MePresenter

@Composable
fun TopToolbar(onNavigateToUnderDevelopment: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 互连图标
        IconButton(onClick = onNavigateToUnderDevelopment) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "互连",
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 扫一扫图标
        IconButton(onClick = onNavigateToUnderDevelopment) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "扫一扫",
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 皮肤图标
        IconButton(onClick = onNavigateToUnderDevelopment) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "皮肤",
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 夜间模式图标
        IconButton(onClick = onNavigateToUnderDevelopment) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = "夜间模式",
                tint = Color.Gray
            )
        }
    }
}

/**
 * 用户信息区域（中部固定区域）
 */
