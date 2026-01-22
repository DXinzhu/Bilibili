package com.example.bilibili_sim.presentation.vip

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
import com.example.bilibili_sim.data.model.User
import com.example.bilibili_sim.presentation.vip.components.*
import com.example.bilibili_sim.data.model.Video
import com.example.bilibili_sim.presentation.vip.VipPresenter
import com.example.bilibili_sim.presentation.vip.VipPrivilege
import com.example.bilibili_sim.common.utils.BilibiliAutoTestLogger

/**
 * 会员中心页面
 * 展示会员信息、特权、专享内容等
 */
@Composable
fun VipTab(
    context: Context,
    onBack: () -> Unit
) {
    val presenter = remember { VipPresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var vipContent by remember { mutableStateOf<List<Video>>(emptyList()) }
    var vipPrivileges by remember { mutableStateOf<List<VipPrivilege>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf(0) } // 0=猜你喜欢, 1=番剧, 2=电影...
    var agreeToTerms by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        BilibiliAutoTestLogger.logVipPageEntered()
        user = presenter.loadUserData()
        vipContent = presenter.getVipExclusiveContent()
        vipPrivileges = presenter.getVipPrivileges()

        // 指令3: 加载会员数据并记录
        user?.let {
            val vipStatus = if (it.isVip) it.getVipStatusText() else "未开通"
            BilibiliAutoTestLogger.logVipDataLoaded(vipStatus)
            BilibiliAutoTestLogger.logVipStatusViewed()

            // 指令27,31: 如果有会员有效期,记录有效期和剩余天数
            if (it.isVip && it.vipExpireDate != null) {
                BilibiliAutoTestLogger.logVipExpireDateDisplayed(it.vipExpireDate!!)
                val expiryText = it.getVipExpiryText()
                android.util.Log.d("BilibiliAutoTest", "MEMBERSHIP_INFO: $expiryText")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏 - 固定
        VipTopBar(onBack = onBack)

        // 可滚动内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 卡通形象装饰区域
            item {
                VipDecorativeSection()
            }

            // 用户信息区域
            item {
                user?.let { VipUserSection(it) }
            }

            // 大会员特权区域
            item {
                VipPrivilegesSection(vipPrivileges)
            }

            // 广告横幅区域
            item {
                VipAdvertisementBanner()
            }

            // 大会员专享内容区域
            item {
                VipExclusiveContentSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    vipContent = vipContent
                )
            }

            // 连续包年区域
            item {
                VipAnnualSubscriptionSection(
                    agreeToTerms = agreeToTerms,
                    onAgreeChanged = { agreeToTerms = it }
                )
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * 顶部导航栏
 */
