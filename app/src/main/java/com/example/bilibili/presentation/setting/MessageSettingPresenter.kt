package com.example.bilibili.presentation.setting

import android.content.Context

/**
 * 消息设置页面 Presenter
 * 负责提供消息设置项数据
 */
class MessageSettingPresenter(private val context: Context) {

    /**
     * 获取所有消息设置项（按模块分组）
     */
    fun getMessageSettingGroups(): List<MessageSettingGroup> {
        return listOf(
            // 消息提醒模块
            MessageSettingGroup(
                title = "消息提醒",
                items = listOf(
                    MessageSettingItem(
                        title = "消息提醒",
                        description = "关闭后，APP首页将不再进行数字提醒",
                        type = MessageSettingItemType.SWITCH,
                        switchState = true
                    )
                )
            ),
            // 消息接收设置模块
            MessageSettingGroup(
                title = "消息接收设置",
                items = listOf(
                    MessageSettingItem(
                        title = "私信智能拦截",
                        description = "开启后，将自动拦截疑似骚扰和不良的会话",
                        type = MessageSettingItemType.SWITCH,
                        switchState = true
                    ),
                    MessageSettingItem(
                        title = "消息屏蔽词",
                        description = "添加后，将不再接受包含屏蔽词的消息",
                        type = MessageSettingItemType.ARROW
                    )
                )
            ),
            // 互动通知模块
            MessageSettingGroup(
                title = "互动通知",
                items = listOf(
                    MessageSettingItem(
                        title = "回复与@",
                        description = "你将收到这些人的评论、弹幕等通知提醒",
                        type = MessageSettingItemType.OPTION,
                        optionValue = "所有人"
                    ),
                    MessageSettingItem(
                        title = "收到喜欢",
                        description = "是否接收点赞等通知提醒",
                        type = MessageSettingItemType.OPTION,
                        optionValue = "所有人"
                    ),
                    MessageSettingItem(
                        title = "新增粉丝",
                        description = "是否接收新增粉丝通知提醒",
                        type = MessageSettingItemType.OPTION,
                        optionValue = "接收提醒"
                    )
                )
            ),
            // 应援团和未关注人模块
            MessageSettingGroup(
                title = "应援团和未关注人",
                items = listOf(
                    MessageSettingItem(
                        title = "应援团消息",
                        description = "",
                        type = MessageSettingItemType.OPTION,
                        optionValue = "接收消息"
                    ),
                    MessageSettingItem(
                        title = "未关注人消息",
                        description = "",
                        type = MessageSettingItemType.ARROW
                    )
                )
            ),
            // 联系人模块
            MessageSettingGroup(
                title = "联系人",
                items = listOf(
                    MessageSettingItem(
                        title = "黑名单",
                        description = "",
                        type = MessageSettingItemType.ARROW
                    )
                )
            )
        )
    }
}

/**
 * 消息设置分组数据类
 */
data class MessageSettingGroup(
    val title: String,
    val items: List<MessageSettingItem>
)

/**
 * 消息设置项数据类
 */
data class MessageSettingItem(
    val title: String,
    val description: String,
    val type: MessageSettingItemType,
    val switchState: Boolean = false,  // 开关状态（仅 SWITCH 类型使用）
    val optionValue: String = ""  // 选项值（仅 OPTION 类型使用）
)

/**
 * 消息设置项类型枚举
 */
enum class MessageSettingItemType {
    SWITCH,  // 开关类型
    ARROW,   // 箭头类型（可点击进入下级页面）
    OPTION   // 选项类型（显示当前选项值）
}
