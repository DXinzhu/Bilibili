package com.example.bilibili.view.concern.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.model.UPMaster

/**
 * 关注列表单个UP主条目
 */
@Composable
fun ConcernVideoItem(
    upMaster: UPMaster,
    onUpClick: (UPMaster) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUpClick(upMaster) }
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // UP主头像
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = upMaster.name,
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )

        // UP主信息
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = upMaster.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "${upMaster.fansCount}粉丝 · ${upMaster.videoCount}视频",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        // 关注按钮
        Button(
            onClick = { /* TODO: 取消关注 */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFE5F0)
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "已关注",
                fontSize = 13.sp,
                color = Color(0xFFFF6699)
            )
        }
    }
}
