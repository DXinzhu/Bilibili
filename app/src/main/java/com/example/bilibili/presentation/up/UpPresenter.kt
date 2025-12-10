package com.example.bilibili.presentation.up

import android.content.Context
import com.example.bilibili.data.model.UPMaster
import com.example.bilibili.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * UP主主页业务逻辑层
 * 负责加载UP主详情信息和视频列表
 */
class UpPresenter(private val context: Context) {

    /**
     * 根据upMasterId加载UP主详情
     */
    fun getUPMasterById(upMasterId: String): UPMaster? {
        return try {
            val json = context.assets.open("data/upmasters.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<UPMaster>>() {}.type
            val upMasters: List<UPMaster> = Gson().fromJson(json, type)
            upMasters.find { it.upMasterId == upMasterId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取UP主的视频列表
     */
    fun getUPMasterVideos(upMasterId: String): List<Video> {
        return try {
            val json = context.assets.open("data/videos.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Video>>() {}.type
            val videos: List<Video> = Gson().fromJson(json, type)
            videos.filter { it.upMasterId == upMasterId }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 格式化数字显示(粉丝数、关注数、获赞数)
     * 例如: 3633000 -> 363.3万
     */
    fun formatCount(count: Int): String {
        return when {
            count >= 100000000 -> String.format("%.1f亿", count / 100000000.0)
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化播放量
     * 例如: 489000 -> 48.9万
     */
    fun formatViewCount(count: Int): String {
        return when {
            count >= 100000000 -> String.format("%.1f亿", count / 100000000.0)
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化评论数
     */
    fun formatCommentCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化相对时间
     * 将时间戳转换为相对时间显示
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30

        return when {
            seconds < 60 -> "刚刚"
            minutes < 60 -> "${minutes}分钟前"
            hours < 24 -> "${hours}小时前"
            days < 30 -> "${days}天前"
            months < 12 -> "${months}个月前"
            else -> "${months / 12}年前"
        }
    }

    /**
     * 切换关注状态
     */
    fun toggleFollow(upMaster: UPMaster): Boolean {
        upMaster.toggleFollow()
        return upMaster.isFollowed
    }
}
