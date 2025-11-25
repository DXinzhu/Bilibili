package com.example.bilibili.presenter

import android.content.Context
import com.example.bilibili.model.LiveStream
import com.example.bilibili.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 直播页面Presenter
 * 遵循MVP模式，负责处理业务逻辑
 */
class LivePresenter(private val context: Context) {

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
     * 加载所有直播数据
     */
    fun loadAllLiveStreams(): List<LiveStream> {
        try {
            val jsonString = context.assets.open("data/livestreams.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<LiveStream>>() {}.type
            return gson.fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * 获取天猫直播（第一个特殊直播）
     */
    fun getTmallLiveStream(): LiveStream? {
        val allStreams = loadAllLiveStreams()
        return allStreams.find { it.liveId == "live001" }
    }

    /**
     * 获取关注的UP主直播列表
     */
    fun getFollowedLiveStreams(): List<LiveStream> {
        val allStreams = loadAllLiveStreams()
        // 获取逍遥散人的直播
        return allStreams.filter { it.upMasterId == "up_xiaoyao" }
    }

    /**
     * 获取推荐直播列表（底部网格展示）
     * 按照顺序返回：live002, live003, live004, live005
     */
    fun getRecommendedLiveStreams(): List<LiveStream> {
        val allStreams = loadAllLiveStreams()
        val liveIds = listOf("live002", "live003", "live004", "live005")
        // 按照指定顺序返回
        return liveIds.mapNotNull { id -> allStreams.find { it.liveId == id } }
    }

    /**
     * 统计正在直播的关注UP主数量
     */
    fun getFollowedLiveCount(): Int {
        return getFollowedLiveStreams().count { it.isLive }
    }
}
