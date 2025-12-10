package com.example.bilibili.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 用户数据模型
 * 用于维护当前登录用户的基本信息
 */
data class User(
    val userId: String,                     // 用户唯一标识
    val uid: Long = 0,                      // 用户UID
    val name: String,                       // 用户昵称
    val avatarUrl: String = "",             // 头像URL
    val level: Int = 1,                     // 用户等级（1-6）
    var bCoins: Double = 0.0,               // B币余额
    var hardCoins: Int = 0,                 // 硬币数量
    var isVip: Boolean = false,             // 是否为正式会员
    var vipLevel: Int = 0,                  // 会员等级（0-普通，1-月度大会员，2-年度大会员）
    var vipExpireDate: String? = null,      // 会员有效期（格式：yyyy-MM-dd）
    var dynamicCount: Int = 0,              // 动态数量
    var followingCount: Int = 0,            // 关注数量
    var fansCount: Int = 0,                 // 粉丝数量
    val space: String = "空间",             // 空间名称
    var offlineCacheCount: Int = 0,         // 离线缓存数量
    var historyCount: Int = 0,              // 历史记录数量
    var collectionCount: Int = 0,           // 收藏数量
    var watchLaterCount: Int = 0,           // 稍后再看数量
    var lastUpdateTime: Long = System.currentTimeMillis()  // 最后更新时间
) {
    /**
     * 获取会员状态描述
     */
    fun getVipStatusText(): String {
        return when (vipLevel) {
            0 -> ""
            1 -> "正式会员"
            2 -> "年度大会员"
            else -> ""
        }
    }

    /**
     * 增加B币
     */
    fun addBCoins(amount: Double) {
        bCoins += amount
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 增加硬币
     */
    fun addHardCoins(amount: Int) {
        hardCoins += amount
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 关注UP主
     */
    fun followUser() {
        followingCount++
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 取消关注UP主
     */
    fun unfollowUser() {
        followingCount = maxOf(0, followingCount - 1)
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 获取会员剩余天数
     */
    fun getVipRemainingDays(): Long? {
        if (!isVip || vipExpireDate == null) return null
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val expireDate = LocalDate.parse(vipExpireDate, formatter)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, expireDate)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取会员到期信息文本
     */
    fun getVipExpiryText(): String {
        val remainingDays = getVipRemainingDays()
        return when {
            remainingDays == null -> ""
            remainingDays <= 0 -> "会员已到期"
            remainingDays == 1L -> "还有1天到期"
            else -> "还有${remainingDays}天到期"
        }
    }
}
