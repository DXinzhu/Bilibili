package com.example.bilibili.presentation.me

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
import com.example.bilibili.data.model.User
import com.example.bilibili.presentation.me.components.*
import com.example.bilibili.presentation.me.MePresenter

/**
 * "我的"标签页
 * 按照MVP模式实现，展示用户信息和各种服务入口
 */
@Composable
fun MeTab(
    context: Context,
    onNavigateToConcern: () -> Unit = {},
    onNavigateToVip: () -> Unit = {},
    onNavigateToSetting: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToCollect: () -> Unit = {},
    onNavigateToPerson: () -> Unit = {},
    onNavigateToLoad: () -> Unit = {},
    onNavigateToAccountProfile: () -> Unit = {},
    onNavigateToUnderDevelopment: () -> Unit = {}
) {
    val presenter = remember { MePresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
        // 记录日志：用户数据加载完成
        if (user != null) {
            Log.d("BilibiliAutoTest", "VIP_STATUS_VIEWED")
            Log.d("BilibiliAutoTest", "VIP_DATA_LOADED:${user!!.getVipStatusText()}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部工具栏
            TopToolbar(onNavigateToUnderDevelopment)

            // 中部信息栏（固定）
            user?.let { UserInfoSection(it, onNavigateToConcern, onNavigateToVip, onNavigateToPerson, onNavigateToAccountProfile, onNavigateToUnderDevelopment) }

            // 底部滚动列表
            BottomServiceList(onNavigateToSetting, onNavigateToHistory, onNavigateToCollect, onNavigateToLoad, onNavigateToUnderDevelopment)
        }
    }
}

/**
 * 顶部工具栏
 */
