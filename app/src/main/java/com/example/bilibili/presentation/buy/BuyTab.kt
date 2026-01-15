package com.example.bilibili.presentation.buy

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
import com.example.bilibili.data.model.Product
import com.example.bilibili.presentation.buy.BuyPresenter
import com.example.bilibili.presentation.buy.components.*

/**
 * 会员购页面
 * 按照MVP模式实现,展示商品列表
 */
@Composable
fun BuyTab(context: Context, onNavigateToUnderDevelopment: () -> Unit = {}) {
    val presenter = remember { BuyPresenter(context) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        products = presenter.getProducts()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏 - 固定不滚动
        BuyTopBar(onNavigateToUnderDevelopment)

        // 可滚动内容区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 商品网格列表
            item {
                ProductGrid(products, onNavigateToUnderDevelopment)
            }

            // 底部空白
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
