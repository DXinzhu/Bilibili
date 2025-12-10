package com.example.bilibili.presentation.collect.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CollectCategoryBar(
    selectedMainTab: String,
    selectedSubTab: String,
    onMainTabSelected: (String) -> Unit,
    onSubTabSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTabItem(
                text = "收藏",
                isSelected = selectedMainTab == "收藏",
                onClick = { onMainTabSelected("收藏") }
            )

            Spacer(modifier = Modifier.width(24.dp))

            MainTabItem(
                text = "追更",
                isSelected = selectedMainTab == "追更",
                onClick = { onMainTabSelected("追更") }
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        if (selectedMainTab == "收藏") {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubTabItem(
                    text = "视频",
                    isSelected = selectedSubTab == "视频",
                    onClick = { onSubTabSelected("视频") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                SubTabItem(
                    text = "音频",
                    isSelected = selectedSubTab == "音频",
                    onClick = { onSubTabSelected("音频") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                SubTabItem(
                    text = "文章",
                    isSelected = selectedSubTab == "文章",
                    onClick = { onSubTabSelected("文章") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                SubTabItem(
                    text = "专栏",
                    isSelected = selectedSubTab == "专栏",
                    onClick = { onSubTabSelected("专栏") }
                )
            }
        }
    }
}

@Composable
fun MainTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp)
                    .background(Color(0xFFFF6699), RoundedCornerShape(1.5.dp))
            )
        }
    }
}

@Composable
fun SubTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
        color = if (isSelected) Color(0xFFFF6699) else Color.Gray,
        modifier = Modifier.clickable { onClick() }
    )
}
