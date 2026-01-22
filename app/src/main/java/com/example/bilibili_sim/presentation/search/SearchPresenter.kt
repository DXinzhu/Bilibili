package com.example.bilibili_sim.presentation.search

import android.content.Context
import com.example.bilibili_sim.data.model.HotSearch
import com.example.bilibili_sim.data.model.SearchHistory
import com.example.bilibili_sim.data.model.SearchDiscovery
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * 搜索页面的Presenter
 * 负责加载热搜、搜索历史、搜索发现数据
 */
class SearchPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载热搜数据
     */
    fun loadHotSearches(): List<HotSearch> {
        return try {
            val inputStream = context.assets.open("data/hot_searches.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<HotSearch>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 加载搜索历史数据
     */
    fun loadSearchHistory(): List<SearchHistory> {
        return try {
            val inputStream = context.assets.open("data/search_history.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<SearchHistory>>() {}.type
            val history: List<SearchHistory> = gson.fromJson(reader, type)
            // 按时间戳倒序排列
            history.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 加载搜索发现数据
     */
    fun loadSearchDiscoveries(): List<SearchDiscovery> {
        return try {
            val inputStream = context.assets.open("data/search_discoveries.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<SearchDiscovery>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 搜索功能（TODO: 实现实际搜索逻辑）
     * @param keyword 搜索关键词
     */
    fun search(keyword: String) {
        // TODO: 实现搜索功能
        // 这里可以添加搜索逻辑，比如保存到搜索历史
    }

    /**
     * 清除搜索历史（TODO: 实现清除逻辑）
     */
    fun clearHistory() {
        // TODO: 实现清除历史功能
        // 需要实现本地存储才能真正清除
    }
}
