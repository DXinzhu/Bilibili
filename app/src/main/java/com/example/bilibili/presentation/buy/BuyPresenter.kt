package com.example.bilibili.presentation.buy

import android.content.Context
import com.example.bilibili.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 会员购页面Presenter
 * 遵循MVP模式,负责处理会员购页面的业务逻辑
 */
class BuyPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载所有商品数据
     */
    fun loadAllProducts(): List<Product> {
        try {
            val jsonString = context.assets.open("data/products.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Product>>() {}.type
            return gson.fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * 获取商品列表
     */
    fun getProducts(): List<Product> {
        return loadAllProducts()
    }
}
