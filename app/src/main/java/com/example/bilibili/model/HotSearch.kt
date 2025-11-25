package com.example.bilibili.model

/**
 * 热搜数据模型
 * @param id 热搜ID
 * @param keyword 热搜关键词
 * @param tag 热搜标签 (hot/new/empty)
 */
data class HotSearch(
    val id: String,
    val keyword: String,
    val tag: String = ""  // "hot" 表示热，"new" 表示新，"" 表示无标签
)
