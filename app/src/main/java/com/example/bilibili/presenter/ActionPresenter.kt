package com.example.bilibili.presenter

import android.content.Context
import com.example.bilibili.model.Post
import com.example.bilibili.model.UPMaster
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 动态页面Presenter
 * 遵循MVP模式,负责处理动态页面的业务逻辑
 */
class ActionPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载所有UP主数据
     */
    fun loadAllUPMasters(): List<UPMaster> {
        try {
            val jsonString = context.assets.open("data/upmasters.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<UPMaster>>() {}.type
            return gson.fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * 根据UP主ID列表加载指定UP主
     */
    fun loadUPMastersByIds(upMasterIds: List<String>): List<UPMaster> {
        val allUPMasters = loadAllUPMasters()
        return upMasterIds.mapNotNull { id ->
            allUPMasters.find { it.upMasterId == id }
        }
    }

    /**
     * 获取最常访问的UP主列表(4个)
     */
    fun getFrequentlyVisitedUPMasters(): List<UPMaster> {
        // 获取up1, up2, up3, up4
        return loadUPMastersByIds(listOf("up1", "up2", "up3", "up4"))
    }

    /**
     * 加载所有动态数据
     */
    fun loadAllPosts(): List<Post> {
        try {
            val jsonString = context.assets.open("data/posts.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Post>>() {}.type
            return gson.fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * 获取动态列表
     */
    fun getPosts(): List<Post> {
        return loadAllPosts()
    }
}
