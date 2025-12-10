package com.example.bilibili.presentation.cartoon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.bilibili.data.model.Video

/**
 * 排行榜番剧卡片
 */
@Composable
fun RankingCartoonCard(cartoon: Video) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { /* TODO: 进入番剧详情 */ }
    ) {
        Box {
            // 封面图片
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${cartoon.coverImage}")
                    .crossfade(true)
                    .build(),
                contentDescription = cartoon.title,
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // 排名标签（左上角）
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
                color = when (cartoon.ranking) {
                    1 -> Color(0xFFFFD700) // 金色
                    2 -> Color(0xFFC0C0C0) // 银色
                    3 -> Color(0xFFCD7F32) // 铜色
                    else -> Color(0xFF666666)
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = cartoon.ranking.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 番剧标题
        Text(
            text = cartoon.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // 更新信息
        Text(
            text = cartoon.episodeInfo,
            fontSize = 11.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
