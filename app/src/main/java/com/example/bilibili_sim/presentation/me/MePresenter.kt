package com.example.bilibili_sim.presentation.me

import android.content.Context
import com.example.bilibili_sim.data.model.User
import com.google.gson.Gson
import java.io.IOException

/**
 * "我的"页面Presenter
 * 遵循MVP模式，负责处理业务逻辑
 */
class MePresenter(private val context: Context) {

    private var user: User? = null
    private val gson = Gson()

    /**
     * 加载用户数据
     */
    fun loadUserData(): User? {
        try {
            val jsonString = context.assets.open("data/user.json").bufferedReader().use { it.readText() }
            user = gson.fromJson(jsonString, User::class.java)
            return user
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 获取当前用户
     */
    fun getCurrentUser(): User? {
        return user
    }

    /**
     * 保存用户数据（将来可扩展为持久化存储）
     */
    fun saveUserData(updatedUser: User) {
        user = updatedUser
        // TODO: 将来可以实现保存到JSON文件或数据库
    }

    /**
     * 切换夜间模式
     */
    fun toggleNightMode(): Boolean {
        // TODO: 实现夜间模式切换逻辑
        return false
    }

    /**
     * 切换皮肤
     */
    fun changeSkin() {
        // TODO: 实现皮肤切换逻辑
    }

    /**
     * 获取离线缓存数量
     */
    fun getOfflineCacheCount(): Int {
        return user?.offlineCacheCount ?: 0
    }

    /**
     * 获取历史记录数量
     */
    fun getHistoryCount(): Int {
        return user?.historyCount ?: 0
    }

    /**
     * 获取收藏数量
     */
    fun getCollectionCount(): Int {
        return user?.collectionCount ?: 0
    }

    /**
     * 获取稍后再看数量
     */
    fun getWatchLaterCount(): Int {
        return user?.watchLaterCount ?: 0
    }
}
