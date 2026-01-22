package com.example.bilibili_sim.presentation.setting

import android.content.Context
import com.example.bilibili_sim.data.model.User
import com.google.gson.Gson
import java.io.IOException

/**
 * 设置页面 Presenter
 * 负责提供设置项列表数据
 */
class SettingPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载用户数据
     */
    private fun loadUserData(): User? {
        return try {
            val jsonString = context.assets.open("data/user.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, User::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取所有设置项（按分组）
     */
    fun getSettingGroups(): List<SettingGroup> {
        val user = loadUserData()
        val uidText = if (user != null) "UID: ${user.uid}" else ""

        return listOf(
            // 第一组：账号相关
            SettingGroup(
                items = listOf(
                    SettingItem("账号资料", uidText),
                    SettingItem("安全隐私", ""),
                    SettingItem("收货信息", "")
                )
            ),
            // 第二组：界面相关
            SettingGroup(
                items = listOf(
                    SettingItem("语言", ""),
                    SettingItem("开屏画面设置", ""),
                    SettingItem("首页推荐设置", "双列/Wi-Fi/免流/移动网络下自动播放"),
                    SettingItem("首页头像入口设置", ""),
                    SettingItem("播放设置", ""),
                    SettingItem("离线设置", ""),
                    SettingItem("追番/追剧设置", "")
                )
            ),
            // 第三组：消息相关
            SettingGroup(
                items = listOf(
                    SettingItem("推送设置", ""),
                    SettingItem("消息设置", ""),
                    SettingItem("防骚扰和互动人群设置", "")
                )
            ),
            // 第四组：其他功能
            SettingGroup(
                items = listOf(
                    SettingItem("下载管理", ""),
                    SettingItem("清理存储空间", ""),
                    SettingItem("其他设置", "")
                )
            ),
            // 第五组：时间管理
            SettingGroup(
                items = listOf(
                    SettingItem("定时关闭", "不开启"),
                    SettingItem("睡眠提醒", "不提醒")
                )
            ),
            // 第六组：主题
            SettingGroup(
                items = listOf(
                    SettingItem("深色设置", "")
                )
            ),
            // 第七组：服务
            SettingGroup(
                items = listOf(
                    SettingItem("我的客服", ""),
                    SettingItem("关于哔哩哔哩", ""),
                    SettingItem("商务合作", "")
                )
            ),
            // 第八组：协议
            SettingGroup(
                items = listOf(
                    SettingItem("用户协议", ""),
                    SettingItem("隐私政策", ""),
                    SettingItem("隐私权限设置", ""),
                    SettingItem("个人信息收集清单", ""),
                    SettingItem("第三方信息共享清单", ""),
                    SettingItem("哔哩哔哩（基本功能）隐私政策", "")
                )
            )
        )
    }
}

/**
 * 设置组数据类
 */
data class SettingGroup(
    val items: List<SettingItem>
)

/**
 * 设置项数据类
 */
data class SettingItem(
    val title: String,
    val subtitle: String
)
