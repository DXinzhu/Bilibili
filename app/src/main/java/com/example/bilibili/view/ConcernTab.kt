package com.example.bilibili.view

import android.content.Context
import com.example.bilibili.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.model.UPMaster
import com.example.bilibili.view.concern.components.*
import com.example.bilibili.presenter.ConcernPresenter

/**
 * 关注页面
 * 展示用户关注的UP主列表
 */
@Composable
fun ConcernTab(
    context: Context,
    onBack: () -> Unit,
    onNavigateToUp: (String) -> Unit = {}
) {
    val presenter = remember { ConcernPresenter(context) }
    var upMasters by remember { mutableStateOf<List<UPMaster>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=关注, 1=粉丝
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        upMasters = presenter.getConcernPageUPMasters()
        // 指令14,15: 记录进入关注页
        BilibiliAutoTestLogger.logFollowPageEntered()
        // 指令16: 记录进入关注列表/最近访问
        BilibiliAutoTestLogger.logFollowListEntered()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部栏
        ConcernTopBar(onBack = onBack)

        // Tab切换栏
        ConcernFilterBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // UP主列表
        ConcernVideoList(
            upMasters = upMasters,
            onUpClick = { up -> onNavigateToUp(up.upMasterId) }
        )
    }
}
