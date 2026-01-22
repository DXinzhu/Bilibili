package com.example.bilibili_sim.data.model

/**
 * 商品数据模型
 * 用于会员购商品信息
 */
data class Product(
    val productId: String,              // 商品唯一标识
    val name: String,                   // 商品名称
    val imageUrl: String,               // 商品图片
    val price: Double,                  // 商品价格
    val originalPrice: Double = 0.0,    // 原价(用于显示折扣)
    val salesCount: Int = 0,            // 销量
    val description: String = "",       // 商品描述
    val tag: String = "",               // 标签(如"新人券"、"热销"等)
    var isFavorite: Boolean = false,    // 是否收藏
    var lastUpdateTime: Long = System.currentTimeMillis()
) {
    /**
     * 是否有折扣
     */
    fun hasDiscount(): Boolean {
        return originalPrice > 0.0 && originalPrice > price
    }

    /**
     * 获取折扣百分比
     */
    fun getDiscountPercent(): Int {
        if (!hasDiscount()) return 0
        return ((1 - price / originalPrice) * 100).toInt()
    }

    /**
     * 切换收藏状态
     */
    fun toggleFavorite() {
        isFavorite = !isFavorite
        lastUpdateTime = System.currentTimeMillis()
    }
}
