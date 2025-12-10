package com.example.bilibili.view.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilibili.model.HotSearch

@Composable
fun HotSearchSection(hotSearches: List<HotSearch>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "bilibili热搜",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        hotSearches.forEachIndexed { index, hotSearch ->
            HotSearchItem(index + 1, hotSearch)
            if (index < hotSearches.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun HotSearchItem(rank: Int, hotSearch: HotSearch) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 点击热搜项 */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名
        Text(
            text = rank.toString(),
            fontSize = 14.sp,
            fontWeight = if (rank <= 3) FontWeight.Bold else FontWeight.Normal,
            color = if (rank <= 3) Color(0xFFFF6699) else Color.Gray,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 热搜内容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = hotSearch.keyword,
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }

        // 热度标识
        if (hotSearch.tag == "hot") {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "热",
                tint = Color(0xFFFF6699),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
