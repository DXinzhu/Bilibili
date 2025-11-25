package com.example.bilibili.presenter

import android.content.Context
import com.example.bilibili.model.User
import com.example.bilibili.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * 会员中心页面 Presenter
 * 负责加载用户信息和会员专享内容
 */
class VipPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载用户数据
     */
    fun loadUserData(): User? {
        return try {
            val inputStream = context.assets.open("data/user.json")
            val reader = InputStreamReader(inputStream)
            val user: User = gson.fromJson(reader, User::class.java)
            reader.close()
            user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取会员专享番剧（使用CartoonTab的热门排行榜番剧）
     */
    fun getVipExclusiveContent(): List<Video> {
        return try {
            val inputStream = context.assets.open("data/videos.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Video>>() {}.type
            val allVideos: List<Video> = gson.fromJson(reader, type)
            reader.close()

            // 获取番剧分类的排行榜番剧（cartoon002, cartoon003, cartoon004）
            allVideos.filter {
                it.videoId in listOf("cartoon002", "cartoon003", "cartoon004")
            }.sortedBy { it.ranking ?: Int.MAX_VALUE }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取会员特权列表
     */
    fun getVipPrivileges(): List<VipPrivilege> {
        return listOf(
            VipPrivilege("免费看", "海量番剧免费看"),
            VipPrivilege("超清看", "1080P高清画质"),
            VipPrivilege("粉色昵称", "专属身份标识"),
            VipPrivilege("等级加速", "经验值1.5倍"),
            VipPrivilege("彩色弹幕", "专属弹幕特权"),
            VipPrivilege("抢先看", "新番抢先观看")
        )
    }
}

/**
 * 会员特权数据类
 */
data class VipPrivilege(
    val title: String,
    val description: String
)
