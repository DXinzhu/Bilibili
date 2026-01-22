package com.example.bilibili_sim.data.model

/**
 * 搜索发现数据模型
 * @param id 发现ID
 * @param title 发现标题
 * @param subtitle 副标题（如更新时间）
 */
data class SearchDiscovery(
    val id: String,
    val title: String,
    val subtitle: String = ""
)
