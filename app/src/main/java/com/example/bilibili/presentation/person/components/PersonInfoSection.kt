package com.example.bilibili.presentation.person.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.bilibili.data.model.User
import com.example.bilibili.presentation.person.PersonPresenter

/**
 * 个人主页信息区域
 */
@Composable
fun PersonInfoSection(
    user: User?,
    defaultFavorite: PersonPresenter.FavoriteFolder?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "个人简介",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "这个人很懒，什么都没有留下",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 显示UID
            if (user != null) {
                Text(
                    text = "UID: ${user.uid}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "关注", count = user?.followingCount ?: 0)
                StatItem(label = "粉丝", count = user?.fansCount ?: 0)
                StatItem(label = "动态", count = user?.dynamicCount ?: 0)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (defaultFavorite != null) {
            item {
                Text(
                    text = "默认收藏夹",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${defaultFavorite.name} (${defaultFavorite.count}个内容)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 我追的动漫
        item {
            Text(
                text = "我追的动漫",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            FollowedCartoonsRow()
        }
    }
}

@Composable
private fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun FollowedCartoonsRow() {
    val context = LocalContext.current
    val cartoons = listOf(
        CartoonItem("cartoon2.jpg", "进击的巨人"),
        CartoonItem("cartoon3.jpg", "鬼灭之刃"),
        CartoonItem("cartoon4.jpg", "咒术回战")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(cartoons.size) { index ->
            CartoonCard(cartoon = cartoons[index])
        }
    }
}

@Composable
private fun CartoonCard(cartoon: CartoonItem) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.width(120.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/video/${cartoon.imageName}")
                .crossfade(true)
                .build(),
            contentDescription = cartoon.name,
            modifier = Modifier
                .width(120.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF0F0F0)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = cartoon.name,
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}

private data class CartoonItem(
    val imageName: String,
    val name: String
)
