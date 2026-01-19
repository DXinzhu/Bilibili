package com.example.bilibili.presentation.setting

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilibili.common.utils.BilibiliAutoTestLogger

/**
 * 推送设置页面
 * 展示所有推送设置项，按分组排列
 */
@Composable
fun PushSettingTab(
    context: Context,
    onBack: () -> Unit
) {
    val presenter = remember { PushSettingPresenter(context) }
    val settingGroups = remember { presenter.getPushSettingGroups() }

    // 记录进入推送设置页面并记录接收消息通知总开关状态
    LaunchedEffect(Unit) {
        // 查找"接收消息通知总开关"项并记录其状态
        settingGroups.forEach { group ->
            group.items.forEach { item ->
                if (item.title == "接收消息通知总开关") {
                    // 从 optionValue 中提取状态（"[已关闭]" 或 "[已开启]"）
                    val status = if (item.optionValue.contains("关闭")) "关闭" else "开启"
                    BilibiliAutoTestLogger.logSmartFilterStatusViewed(status == "开启")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        PushSettingTopBar(onBack = onBack)

        // 设置项列表
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            settingGroups.forEachIndexed { groupIndex, group ->
                // 分组标题（如果不是第一组，添加间距）
                if (groupIndex > 0) {
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(Color(0xFFF5F5F5))
                        )
                    }
                }

                // 分组标题
                if (group.title.isNotEmpty()) {
                    item {
                        Text(
                            text = group.title,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                // 每组设置项
                group.items.forEachIndexed { itemIndex, item ->
                    item {
                        PushSettingItemRow(
                            item = item,
                            showDivider = itemIndex < group.items.size - 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * 顶部导航栏
 */
@Composable
fun PushSettingTopBar(onBack: () -> Unit) {
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

        // 标题
        Text(
            text = "推送设置",
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
 * 推送设置项行
 */
@Composable
fun PushSettingItemRow(
    item: PushSettingItem,
    showDivider: Boolean = true
) {
    var switchState by remember { mutableStateOf(item.switchState) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(enabled = item.type != PushSettingItemType.SWITCH) {
                // 点击事件（除了开关类型）
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：设置项标题和描述
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )

            // 如果有描述，显示描述
            if (item.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧：根据类型显示不同的控件
        when (item.type) {
            PushSettingItemType.SWITCH -> {
                // 开关
                Switch(
                    checked = switchState,
                    onCheckedChange = { switchState = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFFF6699),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFDDDDDD)
                    )
                )
            }
            PushSettingItemType.OPTION -> {
                // 选项值 + 箭头
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.optionValue,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "进入",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            PushSettingItemType.ARROW -> {
                // 箭头
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "进入",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // 分隔线
    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = Color(0xFFEEEEEE),
            thickness = 0.5.dp
        )
    }
}
