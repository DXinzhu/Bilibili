package com.example.bilibili.presentation.concern

import android.content.Context
import com.example.bilibili.data.model.UPMaster
import com.example.bilibili.data.model.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * 关注页面 Presenter
 * 负责加载和管理关注的UP主数据
 */
class ConcernPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载所有已关注的UP主
     */
    fun loadFollowedUPMasters(): List<UPMaster> {
        return try {
            val inputStream = context.assets.open("data/upmasters.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<UPMaster>>() {}.type
            val allUPMasters: List<UPMaster> = gson.fromJson(reader, type)
            reader.close()

            // 筛选已关注的UP主，按关注时间倒序排列（最近关注的在前）
            allUPMasters
                .filter { it.isFollowed }
                .sortedByDescending { it.followTime ?: 0 }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取指定UP主列表（ActionTab的4位UP主 + 逍遥散人）
     * 根据需求，关注列表显示这5位UP主
     */
    fun getConcernPageUPMasters(): List<UPMaster> {
        val allFollowed = loadFollowedUPMasters()
        val targetIds = setOf("up1", "up2", "up3", "up4", "up_xiaoyao")

        return allFollowed
            .filter { it.upMasterId in targetIds }
            .sortedByDescending { it.followTime ?: 0 }
    }

    /**
     * 搜索已关注的UP主
     */
    fun searchFollowedUPMasters(query: String): List<UPMaster> {
        if (query.isBlank()) {
            return getConcernPageUPMasters()
        }

        return getConcernPageUPMasters().filter { upMaster ->
            upMaster.name.contains(query, ignoreCase = true) ||
            upMaster.description.contains(query, ignoreCase = true)
        }
    }

    /**
     * 获取关注数量
     */
    fun getFollowedCount(): Int {
        return getConcernPageUPMasters().size
    }

    /**
     * 获取关注的UP主发布的动态
     * 从posts.json中筛选关注的UP主发布的动态
     */
    fun getFollowingDynamics(): List<Post> {
        return try {
            // 获取已关注的UP主ID列表
            val followedUPMasterIds = getConcernPageUPMasters().map { it.upMasterId }.toSet()

            // 加载所有动态
            val inputStream = context.assets.open("data/posts.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Post>>() {}.type
            val allPosts: List<Post> = gson.fromJson(reader, type)
            reader.close()

            // 筛选出关注的UP主发布的动态
            allPosts.filter { it.upMasterId in followedUPMasterIds }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
