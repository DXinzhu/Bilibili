package com.example.bilibili_sim.presentation.cartoon

import android.content.Context
import com.example.bilibili_sim.data.model.User
import com.example.bilibili_sim.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 动画/番剧页面Presenter
 * 遵循MVP模式，负责处理业务逻辑
 */
class CartoonPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载用户数据
     */
    fun loadUserData(): User? {
        try {
            val jsonString = context.assets.open("data/user.json").bufferedReader().use { it.readText() }
            return gson.fromJson(jsonString, User::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 加载所有视频数据
     */
    fun loadAllVideos(): List<Video> {
        try {
            val jsonString = context.assets.open("data/videos.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Video>>() {}.type
            return gson.fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * 获取番剧分类的所有视频
     */
    fun getCartoonVideos(): List<Video> {
        return loadAllVideos().filter { it.category == "cartoon" }
    }

    /**
     * 获取顶部展示的番剧（Cartoon1）
     */
    fun getFeaturedCartoon(): Video? {
        return loadAllVideos().find { it.videoId == "cartoon001" }
    }

    /**
     * 获取排行榜番剧（按ranking排序）
     */
    fun getRankingCartoons(): List<Video> {
        return loadAllVideos()
            .filter { it.category == "cartoon" && it.ranking > 0 }
            .sortedBy { it.ranking }
    }
}
