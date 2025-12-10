package com.example.bilibili.presentation.buy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.data.model.Product

@Composable
fun ProductCard(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* TODO: 查看商品详情 */ }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/buy/${product.imageUrl}")
                .crossfade(true)
                .build(),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "¥",
                    fontSize = 12.sp,
                    color = Color(0xFFFF6699),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format("%.1f", product.price),
                    fontSize = 18.sp,
                    color = Color(0xFFFF6699),
                    fontWeight = FontWeight.Bold
                )

                if (product.hasDiscount()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "¥${String.format("%.1f", product.originalPrice)}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            if (product.salesCount > 0) {
                Text(
                    text = "${product.salesCount}人购买",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        if (product.tag.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                modifier = Modifier.wrapContentWidth(),
                color = Color(0xFFFFE5E5),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = product.tag,
                    fontSize = 10.sp,
                    color = Color(0xFFFF6699),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
