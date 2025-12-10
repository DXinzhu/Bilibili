package com.example.bilibili.presentation.recommend

import android.content.Context
import com.example.bilibili.data.model.User
import com.example.bilibili.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 推荐页面Presenter
 * 遵循MVP模式，负责处理业务逻辑
 */
class RecommendPresenter(private val context: Context) {

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
     * 根据视频ID列表加载指定视频
     */
    fun loadVideosByIds(videoIds: List<String>): List<Video> {
        val allVideos = loadAllVideos()
        return videoIds.mapNotNull { id ->
            allVideos.find { it.videoId == id }
        }
    }

    /**
     * 获取推荐视频列表（用于首页展示）
     */
    fun getRecommendedVideos(): List<Video> {
        // 返回所有视频
        return loadAllVideos()
    }
}
