package com.example.bilibili.presentation.video.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * 标签栏和弹幕按钮区域
 */
@Composable
fun TabAndDanmakuSection(
    selectedTab: String,
    commentCount: Int,
    onTabSelected: (String) -> Unit
) {
    // 弹幕开关状态
    var isDanmakuEnabled by remember { mutableStateOf(true) }

    // 指令26: 记录弹幕初始状态（仅首次）
    LaunchedEffect(Unit) {
        BilibiliAutoTestLogger.logDanmakuInitialState(isDanmakuEnabled)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧标签栏
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 简介标签
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "简介",
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == "简介") FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == "简介") Color(0xFFFF6699) else Color.Gray,
                        modifier = Modifier.clickable { onTabSelected("简介") }
                    )
                    if (selectedTab == "简介") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier
                                .width(20.dp)
                                .height(3.dp),
                            color = Color(0xFFFF6699),
                            shape = RoundedCornerShape(1.5.dp)
                        ) {}
                    }
                }

                // 评论标签
                Text(
                    text = "评论 $commentCount",
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == "评论") FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == "评论") Color(0xFFFF6699) else Color.Gray,
                    modifier = Modifier.clickable {
                        // 指令17,20,29: 记录进入评论页面
                        BilibiliAutoTestLogger.logCommentPageEntered()
                        onTabSelected("评论")
                    }
                )
            }

            // 右侧弹幕按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .clickable { /* TODO: 发弹幕 */ },
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    color = Color.White
                ) {
                    Text(
                        text = "点我发弹幕",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // 弹幕开关按钮 - 带开关功能
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            // 指令26: 记录弹幕开关点击
                            BilibiliAutoTestLogger.logDanmakuSwitchClicked()
                            isDanmakuEnabled = !isDanmakuEnabled
                            // 指令26: 记录弹幕状态改变
                            BilibiliAutoTestLogger.logDanmakuStatusChanged(isDanmakuEnabled)
                        },
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isDanmakuEnabled) Color(0xFFFF6699) else Color(0xFFCCCCCC)
                    ),
                    color = if (isDanmakuEnabled) Color.White else Color(0xFFF5F5F5)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "弹",
                            fontSize = 12.sp,
                            color = if (isDanmakuEnabled) Color(0xFFFF6699) else Color(0xFFCCCCCC),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
