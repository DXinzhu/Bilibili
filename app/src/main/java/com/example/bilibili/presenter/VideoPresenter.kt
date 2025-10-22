package com.example.bilibili.presenter

import android.content.Context
import com.example.bilibili.model.UPMaster
import com.example.bilibili.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * 视频播放页面的Presenter
 * 负责加载视频详情、UP主信息、推荐视频等
 */
class VideoPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载所有视频数据
     */
    private fun loadAllVideos(): List<Video> {
        return try {
            val inputStream = context.assets.open("data/videos.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Video>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 根据videoId获取视频详情
     */
    fun getVideoById(videoId: String): Video? {
        val videos = loadAllVideos()
        return videos.find { it.videoId == videoId }
    }

    /**
     * 加载所有UP主数据
     */
    private fun loadAllUpMasters(): List<UPMaster> {
        return try {
            val inputStream = context.assets.open("data/upmasters.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<UPMaster>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 根据upMasterId获取UP主信息
     */
    fun getUpMasterById(upMasterId: String): UPMaster? {
        val upMasters = loadAllUpMasters()
        return upMasters.find { it.upMasterId == upMasterId }
    }

    /**
     * 获取推荐视频列表（排除当前视频）
     * 从vid002-vid007中随机选择4个视频
     */
    fun getRecommendedVideos(currentVideoId: String, limit: Int = 4): List<Video> {
        val allVideos = loadAllVideos()
        // 指定推荐视频池：vid002-vid007
        val recommendVideoIds = listOf("vid002", "vid003", "vid004", "vid005", "vid006", "vid007")
        return allVideos
            .filter { it.videoId in recommendVideoIds && it.videoId != currentVideoId }
            .shuffled()
            .take(limit)
    }

    /**
     * 格式化播放量
     * 例如: 489000 -> "48.9万"
     */
    fun formatViewCount(count: Int): String {
        return when {
            count >= 100000000 -> String.format("%.1f亿", count / 100000000.0)
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化数字（点赞、投币、收藏、分享）
     * 例如: 40000 -> "4万", 3105 -> "3105"
     */
    fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化粉丝数
     * 例如: 1299000 -> "129.9万粉丝"
     */
    fun formatFansCount(count: Int): String {
        val formatted = when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
        return "${formatted}粉丝"
    }

    /**
     * 格式化发布时间
     * 例如: 2025年10月21日 19:35
     */
    fun formatPublishTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA)
        return sdf.format(Date(timestamp))
    }

    /**
     * 格式化在线观看人数
     * 例如: 477 -> "477人正在看"
     */
    fun formatOnlineViewers(count: Int): String {
        return "${count}人正在看"
    }
}
