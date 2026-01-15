package com.example.bilibili.presentation.accountprofile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bilibili.data.model.User
import com.example.bilibili.presentation.accountprofile.components.*

/**
 * 账号资料页面
 * 展示用户的详细账号信息，包括头像、昵称、性别、出生年月等
 */
@Composable
fun AccountProfileTab(
    context: Context,
    onNavigateBack: () -> Unit = {}
) {
    val presenter = remember { AccountProfilePresenter(context) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        user = presenter.loadUserData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        AccountProfileTopBar(onNavigateBack = onNavigateBack)

        // 账号资料列表
        user?.let { userData ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // 头像
                item {
                    ProfileListItem(
                        label = "头像",
                        value = "",
                        showAvatar = true,
                        avatarUrl = userData.avatarUrl,
                        onClick = { /* TODO: 编辑头像 */ }
                    )
                }

                // 昵称
                item {
                    ProfileListItem(
                        label = "昵称",
                        value = userData.name,
                        onClick = { /* TODO: 编辑昵称 */ }
                    )
                }

                // 性别
                item {
                    ProfileListItem(
                        label = "性别",
                        value = "保密",
                        onClick = { /* TODO: 编辑性别 */ }
                    )
                }

                // 出生年月
                item {
                    ProfileListItem(
                        label = "出生年月",
                        value = "1990-01-01",
                        onClick = { /* TODO: 编辑出生年月 */ }
                    )
                }

                // 个性签名
                item {
                    ProfileListItem(
                        label = "个性签名",
                        value = "这个人很懒，什么都没有留下",
                        onClick = { /* TODO: 编辑个性签名 */ }
                    )
                }

                // 学校
                item {
                    ProfileListItem(
                        label = "学校",
                        value = "填写学校信息",
                        isPlaceholder = true,
                        onClick = { /* TODO: 编辑学校 */ }
                    )
                }

                // UID
                item {
                    ProfileListItem(
                        label = "UID",
                        value = userData.uid.toString(),
                        showChevron = false,
                        onClick = { }
                    )
                }

                // 二维码名片
                item {
                    ProfileListItem(
                        label = "二维码名片",
                        value = "",
                        showQrCode = true,
                        onClick = { /* TODO: 查看二维码名片 */ }
                    )
                }

                // 购买邀请码
                item {
                    ProfileListItem(
                        label = "购买邀请码",
                        value = "剩余0个",
                        onClick = { /* TODO: 购买邀请码 */ }
                    )
                }

                // 哔哩哔哩认证
                item {
                    ProfileListItem(
                        label = "哔哩哔哩认证",
                        value = "",
                        onClick = { /* TODO: 哔哩哔哩认证 */ }
                    )
                }
            }
        }
    }
}
