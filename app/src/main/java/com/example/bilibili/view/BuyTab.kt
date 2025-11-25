package com.example.bilibili.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili.model.Product
import com.example.bilibili.presenter.BuyPresenter

/**
 * 会员购页面
 * 按照MVP模式实现,展示商品列表
 */
@Composable
fun BuyTab(context: Context) {
    val presenter = remember { BuyPresenter(context) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        products = presenter.getProducts()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏 - 固定不滚动
        BuyTopBar()

        // 可滚动内容区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 商品网格列表
            item {
                ProductGrid(products)
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 顶部栏 - "会员购"标题 + 搜索栏
 */
@Composable
fun BuyTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 会员购标题
            Text(
                text = "会员购",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 搜索栏
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clickable { /* TODO */ },
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "搜索",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}

/**
 * 商品网格布局 - 2列
 */
@Composable
fun ProductGrid(products: List<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height((products.size / 2 * 280).dp), // 每行高度约280dp
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

/**
 * 商品卡片
 */
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
        // 商品图片
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

        // 商品名称
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

        // 价格行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // 价格
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

                // 原价(如果有折扣)
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

            // 销量(如果有)
            if (product.salesCount > 0) {
                Text(
                    text = "${product.salesCount}人购买",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        // 标签(如果有)
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
