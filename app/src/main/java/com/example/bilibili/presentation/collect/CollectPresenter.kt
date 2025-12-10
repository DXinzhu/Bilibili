package com.example.bilibili.presentation.collect

import android.content.Context
import com.example.bilibili.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * 收藏页面的Presenter
 * 按照MVP模式实现，负责处理收藏相关的业务逻辑
 */
class CollectPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载所有视频数据
     */
    private fun loadVideos(): List<Video> {
        return try {
            val inputStream = context.assets.open("data/videos.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Video>>() {}.type
            gson.fromJson(reader, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取已收藏的视频列表
     * 筛选isFavorited=true的视频
     */
    fun getCollectedVideos(): List<Video> {
        val allVideos = loadVideos()
        return allVideos.filter { it.isFavorited }
    }

    /**
     * 格式化播放量
     * @param count 播放量
     */
    fun formatViewCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化评论数
     * @param count 评论数
     */
    fun formatCommentCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化视频时长
     * @param durationSeconds 时长（秒）
     */
    fun formatDuration(durationSeconds: Int): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
