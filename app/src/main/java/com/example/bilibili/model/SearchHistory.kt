package com.example.bilibili.model

/**
 * 搜索历史数据模型
 * @param id 历史ID
 * @param keyword 搜索关键词
 * @param timestamp 搜索时间戳
 */
data class SearchHistory(
    val id: String,
    val keyword: String,
    val timestamp: Long = System.currentTimeMillis()
)
