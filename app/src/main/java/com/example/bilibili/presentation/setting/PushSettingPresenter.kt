package com.example.bilibili.presentation.setting

import android.content.Context

/**
 * 推送设置页面 Presenter
 * 负责提供推送设置项数据
 */
class PushSettingPresenter(private val context: Context) {

    /**
     * 获取所有推送设置项（按模块分组）
     */
    fun getPushSettingGroups(): List<PushSettingGroup> {
        return listOf(
            // 基础设置模块（无标题）
            PushSettingGroup(
                title = "",
                items = listOf(
                    PushSettingItem(
                        title = "接收消息通知总开关",
                        description = "请在您设备的\"设置-通知\"中，选择\"哔哩哔哩\"进行修改",
                        type = PushSettingItemType.OPTION,
                        optionValue = "[已关闭]"
                    ),
                    PushSettingItem(
                        title = "消息免打扰",
                        description = "开启后，免打扰时间内将不会收到推送消息",
                        type = PushSettingItemType.ARROW
                    )
                )
            ),
            // 互动通知模块
            PushSettingGroup(
                title = "互动通知",
                items = listOf(
                    PushSettingItem(
                        title = "点赞",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    ),
                    PushSettingItem(
                        title = "评论",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    ),
                    PushSettingItem(
                        title = "@",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    )
                )
            ),
            // 私信通知模块
            PushSettingGroup(
                title = "私信通知",
                items = listOf(
                    PushSettingItem(
                        title = "聊天消息",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    )
                )
            ),
            // 关注通知模块
            PushSettingGroup(
                title = "关注通知",
                items = listOf(
                    PushSettingItem(
                        title = "关注up主的更新提醒",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    )
                )
            ),
            // 内容推荐模块
            PushSettingGroup(
                title = "内容推荐",
                items = listOf(
                    PushSettingItem(
                        title = "推荐可能感兴趣的内容",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    ),
                    PushSettingItem(
                        title = "热点",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    ),
                    PushSettingItem(
                        title = "活动",
                        description = "",
                        type = PushSettingItemType.SWITCH,
                        switchState = true
                    )
                )
            )
        )
    }
}

/**
 * 推送设置分组数据类
 */
data class PushSettingGroup(
    val title: String,
    val items: List<PushSettingItem>
)

/**
 * 推送设置项数据类
 */
data class PushSettingItem(
    val title: String,
    val description: String,
    val type: PushSettingItemType,
    val switchState: Boolean = false,  // 开关状态（仅 SWITCH 类型使用）
    val optionValue: String = ""  // 选项值（仅 OPTION 类型使用）
)

/**
 * 推送设置项类型枚举
 */
enum class PushSettingItemType {
    SWITCH,  // 开关类型
    ARROW,   // 箭头类型（可点击进入下级页面）
    OPTION   // 选项类型（显示当前选项值）
}
