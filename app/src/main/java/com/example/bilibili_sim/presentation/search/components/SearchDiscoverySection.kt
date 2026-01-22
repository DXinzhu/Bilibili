package com.example.bilibili_sim.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.example.bilibili_sim.data.model.SearchDiscovery

@Composable
fun SearchDiscoverySection(discoveries: List<SearchDiscovery>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "搜索发现",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        discoveries.forEach { discovery ->
            SearchDiscoveryItem(discovery)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SearchDiscoveryItem(discovery: SearchDiscovery) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { /* TODO: 点击发现项 */ }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 内容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = discovery.title,
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            if (discovery.subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = discovery.subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
