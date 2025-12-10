package com.example.bilibili.presentation.person

import android.content.Context
import com.example.bilibili.data.model.User
import com.example.bilibili.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * 个人主页的Presenter
 * 按照MVP模式实现，负责处理个人主页相关的业务逻辑
 */
class PersonPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载用户数据
     */
    fun loadUserData(): User? {
        return try {
            val inputStream = context.assets.open("data/user.json")
            val reader = InputStreamReader(inputStream)
            gson.fromJson(reader, User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 加载所有视频数据
     */
    private fun loadVideos(): List<Video> {
        return try {
            val inputStream = context.assets.open("data/videos.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Video>>() {}.type
            gson.fromJson(reader, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取默认收藏夹信息
     * 返回收藏夹名称、内容数量和封面图
     */
    fun getDefaultFavorite(): FavoriteFolder {
        val videos = loadVideos()
        val favoritedVideos = videos.filter { it.isFavorited }
        return FavoriteFolder(
            name = "默认收藏夹",
            count = favoritedVideos.size,
            coverImage = "video/L1.png",
            isPublic = true
        )
    }

    /**
     * 获取追漫列表
     */
    fun getFollowedCartoons(): List<Video> {
        val videos = loadVideos()
        return videos.filter {
            it.videoId == "cartoon002"
        }
    }

    /**
     * 获取最近投币的视频
     * 返回vid001和vid002
     */
    fun getRecentCoinedVideos(): List<Video> {
        val videos = loadVideos()
        return videos.filter {
            it.videoId == "vid001" || it.videoId == "vid002"
        }
    }

    /**
     * 获取最近点赞的视频
     * 返回vid003和vid004（根据isLiked=true筛选前2个）
     */
    fun getRecentLikedVideos(): List<Video> {
        val videos = loadVideos()
        return videos.filter {
            it.videoId == "vid002" || it.videoId == "vid004"
        }.take(2)
    }

    /**
     * 格式化播放量
     */
    fun formatViewCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化评论数
     */
    fun formatCommentCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 收藏夹数据类
     */
    data class FavoriteFolder(
        val name: String,
        val count: Int,
        val coverImage: String,
        val isPublic: Boolean
    )
}
