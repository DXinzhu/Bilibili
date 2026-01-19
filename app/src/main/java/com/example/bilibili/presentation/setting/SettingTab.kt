package com.example.bilibili.presentation.setting

import android.content.Context
import com.example.bilibili.common.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilibili.presentation.setting.SettingPresenter
import com.example.bilibili.presentation.setting.SettingGroup
import com.example.bilibili.presentation.setting.SettingItem

/**
 * 设置页面
 * 展示所有设置项，按分组排列
 */
@Composable
fun SettingTab(
    context: Context,
    onBack: () -> Unit,
    onNavigateToMessageSetting: () -> Unit = {},
    onNavigateToPushSetting: () -> Unit = {},
    onNavigateToAccountProfile: () -> Unit = {}
) {
    val presenter = remember { SettingPresenter(context) }
    val settingGroups = remember { presenter.getSettingGroups() }

    // 指令30: 记录进入设置页面
    // 指令25: 记录UID显示
    LaunchedEffect(Unit) {
        BilibiliAutoTestLogger.logSettingsPageEntered()

        // 从用户数据中获取UID并记录
        try {
            val jsonString = context.assets.open("data/user.json").bufferedReader().use { it.readText() }
            val gson = com.google.gson.Gson()
            val user = gson.fromJson(jsonString, com.example.bilibili.data.model.User::class.java)
            if (user != null) {
                BilibiliAutoTestLogger.logUidDisplayed(user.uid)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        SettingTopBar(onBack = onBack)

        // 设置项列表
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            settingGroups.forEachIndexed { groupIndex, group ->
                // 每组设置项
                items(group.items) { item ->
                    SettingItemRow(
                        item = item,
                        onClick = {
                            android.util.Log.d("SettingTab", "点击了: ${item.title}")
                            when (item.title) {
                                "账号资料" -> {
                                    android.util.Log.d("SettingTab", "触发账号资料导航")
                                    onNavigateToAccountProfile()
                                }
                                "推送设置" -> {
                                    android.util.Log.d("SettingTab", "触发推送设置导航")
                                    onNavigateToPushSetting()
                                }
                                "消息设置" -> {
                                    android.util.Log.d("SettingTab", "触发消息设置导航")
                                    onNavigateToMessageSetting()
                                }
                                else -> { /* TODO: 其他设置项的处理 */ }
                            }
                        }
                    )
                }

                // 组间分隔空白
                if (groupIndex < settingGroups.size - 1) {
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(Color(0xFFF5F5F5))
                        )
                    }
                }
            }

            // 账号切换按钮
            item {
                Spacer(modifier = Modifier.height(20.dp))
                AccountSwitchButton()
            }

            // 退出登录按钮
            item {
                Spacer(modifier = Modifier.height(12.dp))
                LogoutButton()
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

/**
 * 顶部导航栏
 */
@Composable
fun SettingTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回箭头
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black
            )
        }

        // 设置标题
        Text(
            text = "设置",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // 占位符，保持标题居中
        Spacer(modifier = Modifier.width(48.dp))
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

/**
 * 设置项行
 */
@Composable
fun SettingItemRow(
    item: SettingItem,
    onClick: () -> Unit = {}
) {
    // 指令28: 当定时关闭项显示时，记录其状态
    LaunchedEffect(item.title, item.subtitle) {
        if (item.title == "定时关闭") {
            BilibiliAutoTestLogger.logTimerCloseStatusViewed(item.subtitle)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable {
                // 指令30: 检测定时关闭选项
                if (item.title.contains("定时") || item.title.contains("关闭")) {
                    BilibiliAutoTestLogger.logTimerShutdownOptionFound()
                    BilibiliAutoTestLogger.logTimerShutdownClicked()
                }
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：设置项标题和副标题
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = Color.Black
            )

            // 如果有副标题，显示副标题
            if (item.subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧：向右箭头
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "进入",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }

    // 分隔线
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = Color.LightGray,
        thickness = 0.5.dp
    )
}

/**
 * 账号切换按钮
 */
@Composable
fun AccountSwitchButton() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { /* TODO: 账号切换 */ },
        color = Color.White,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "账号切换",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 退出登录按钮
 */
@Composable
fun LogoutButton() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { /* TODO: 退出登录 */ },
        color = Color.White,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "退出登录",
                fontSize = 16.sp,
                color = Color(0xFFFF6699),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
