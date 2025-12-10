package com.example.bilibili.presentation.vip.components

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
import com.example.bilibili.data.model.User
import com.example.bilibili.data.model.Video
import com.example.bilibili.presentation.vip.VipPresenter
import com.example.bilibili.presentation.vip.VipPrivilege
import com.example.bilibili.common.utils.BilibiliAutoTestLogger

@Composable
fun VipUserSection(user: User) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 用户头像
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${user.avatarUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 用户名和开通状态
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (user.isVip) user.getVipStatusText() else "尚未开通",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                if (user.isVip) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user.getVipExpiryText(),
                        fontSize = 12.sp,
                        color = Color(0xFFFF6699)
                    )
                }
            }

            // 会员状态按钮
            Surface(
                color = if (user.isVip) Color(0xFFE0E0E0) else Color(0xFFFF6699),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable { /* TODO: 开通会员 */ }
            ) {
                Text(
                    text = if (user.isVip) "已开通" else "立即开通",
                    color = if (user.isVip) Color.Gray else Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                )
            }
        }
    }
}

/**
 * 大会员特权区域
 */
