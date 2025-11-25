package com.example.bilibili.utils

import android.util.Log

/**
 * Bilibili自动化测试日志工具类
 * 用于输出自动化测试所需的日志标签
 *
 * 使用方法：
 * 1. 将此文件复制到你的项目中
 * 2. 在需要输出日志的地方调用相应的方法
 * 3. 例如：BilibiliAutoTestLogger.logVideoPlayerOpened("vid001")
 */
object BilibiliAutoTestLogger {

    private const val TAG = "BilibiliAutoTest"

    // ==================== 收藏相关 ====================

    /**
     * 指令7: 点击收藏标签
     */
    fun logFavoriteTabClicked() {
        Log.d(TAG, "FAVORITE_TAB_CLICKED")
    }

    /**
     * 指令7: 进入收藏页面
     */
    fun logFavoritePageEntered() {
        Log.d(TAG, "FAVORITE_PAGE_ENTERED")
    }

    /**
     * 指令7: 收藏数据加载完成
     * @param count 收藏数量
     */
    fun logFavoriteDataLoaded(count: Int) {
        Log.d(TAG, "FAVORITE_DATA_LOADED: $count")
    }

    /**
     * 指令8: 点击收藏按钮
     */
    fun logFavoriteButtonClicked() {
        Log.d(TAG, "FAVORITE_BUTTON_CLICKED")
    }

    /**
     * 指令8: 收藏状态改变（可选）
     * @param isFavorited true表示已收藏，false表示取消收藏
     */
    fun logFavoriteStatusChanged(isFavorited: Boolean) {
        val status = if (isFavorited) "favorited" else "unfavorited"
        Log.d(TAG, "FAVORITE_STATUS_CHANGED: $status")
    }

    /**
     * 指令25: 显示收藏数量
     * @param count 收藏数量
     */
    fun logFavoriteCountDisplayed(count: Int) {
        Log.d(TAG, "FAVORITE_COUNT_DISPLAYED: $count")
    }

    // ==================== 视频播放相关 ====================

    /**
     * 指令9,17,18,21,22,23: 打开视频播放器
     * @param videoId 视频ID
     */
    fun logVideoPlayerOpened(videoId: String) {
        Log.d(TAG, "VIDEO_PLAYER_OPENED: $videoId")
    }

    /**
     * 指令9,22,23: 视频开始播放
     */
    fun logVideoPlaybackStarted() {
        Log.d(TAG, "VIDEO_PLAYBACK_STARTED")
    }

    /**
     * 指令14,21,22: 视频暂停
     */
    fun logVideoPaused() {
        Log.d(TAG, "VIDEO_PAUSED")
    }

    /**
     * 指令14: 点击暂停按钮
     */
    fun logPauseButtonClicked() {
        Log.d(TAG, "PAUSE_BUTTON_CLICKED")
    }

    /**
     * 指令22: 触发暂停操作
     */
    fun logPauseActionTriggered() {
        Log.d(TAG, "PAUSE_ACTION_TRIGGERED")
    }

    // ==================== 全屏相关 ====================

    /**
     * 指令11: 进入全屏模式
     */
    fun logFullscreenModeEntered() {
        Log.d(TAG, "FULLSCREEN_MODE_ENTERED")
    }

    /**
     * 指令11: 点击全屏按钮
     */
    fun logFullscreenButtonClicked() {
        Log.d(TAG, "FULLSCREEN_BUTTON_CLICKED")
    }

    // ==================== UP主相关 ====================

    /**
     * 指令12,19: 进入UP主主页
     * @param uploaderName UP主名称（可选）
     */
    fun logUploaderPageEntered(uploaderName: String? = null) {
        if (uploaderName != null) {
            Log.d(TAG, "UPLOADER_PAGE_ENTERED: $uploaderName")
        } else {
            Log.d(TAG, "UPLOADER_PAGE_ENTERED")
        }
    }

    /**
     * 指令19: 找到UP主
     * @param uploaderName UP主名称
     */
    fun logUploaderFound(uploaderName: String) {
        Log.d(TAG, "UPLOADER_FOUND: $uploaderName")
    }

    /**
     * 指令19: UP主数据加载完成
     */
    fun logUploaderDataLoaded() {
        Log.d(TAG, "UPLOADER_DATA_LOADED")
    }

    /**
     * 指令12: 显示粉丝数
     * @param fansCount 粉丝数量
     */
    fun logFansCountDisplayed(fansCount: String) {
        Log.d(TAG, "FANS_COUNT_DISPLAYED: $fansCount")
    }

    // ==================== 关注相关 ====================

    /**
     * 指令19: 进入关注列表
     */
    fun logFollowListEntered() {
        Log.d(TAG, "FOLLOW_LIST_ENTERED")
    }

    /**
     * 指令18: 点击关注按钮
     */
    fun logFollowButtonClicked() {
        Log.d(TAG, "FOLLOW_BUTTON_CLICKED")
    }

    /**
     * 指令18: 关注状态改变（可选）
     * @param isFollowed true表示已关注，false表示取消关注
     */
    fun logFollowStatusChanged(isFollowed: Boolean) {
        val status = if (isFollowed) "followed" else "unfollowed"
        Log.d(TAG, "FOLLOW_STATUS_CHANGED: $status")
    }

    /**
     * 指令16: 点击最近访问标签
     */
    fun logRecentVisitTabClicked() {
        Log.d(TAG, "RECENT_VISIT_TAB_CLICKED")
    }

    /**
     * 指令16: 最近访问加载完成
     */
    fun logRecentVisitLoaded() {
        Log.d(TAG, "RECENT_VISIT_LOADED")
    }

    // ==================== 动态相关 ====================

    /**
     * 指令15: 点击第一个动态
     */
    fun logFirstDynamicClicked() {
        Log.d(TAG, "FIRST_DYNAMIC_CLICKED")
    }

    /**
     * 指令15: 打开动态详情
     */
    fun logDynamicDetailOpened() {
        Log.d(TAG, "DYNAMIC_DETAIL_OPENED")
    }

    // ==================== 评论相关 ====================

    /**
     * 指令17,20,29: 进入评论页面
     */
    fun logCommentPageEntered() {
        Log.d(TAG, "COMMENT_PAGE_ENTERED")
    }

    /**
     * 指令20,29: 评论列表加载完成
     */
    fun logCommentListLoaded() {
        Log.d(TAG, "COMMENT_LIST_LOADED")
    }

    /**
     * 指令20: 点击评论点赞
     */
    fun logCommentLikeClicked() {
        Log.d(TAG, "COMMENT_LIKE_CLICKED")
    }

    /**
     * 指令20: 评论点赞状态改变（可选）
     */
    fun logCommentLikeStatusChanged() {
        Log.d(TAG, "COMMENT_LIKE_STATUS_CHANGED")
    }

    /**
     * 指令17: 点击回复按钮
     */
    fun logReplyButtonClicked() {
        Log.d(TAG, "REPLY_BUTTON_CLICKED")
    }

    /**
     * 指令17: 输入评论内容
     * @param text 评论内容
     */
    fun logCommentInputText(text: String) {
        Log.d(TAG, "COMMENT_INPUT_TEXT: $text")
    }

    /**
     * 指令17: 点击发送按钮
     */
    fun logSendButtonClicked() {
        Log.d(TAG, "SEND_BUTTON_CLICKED")
    }

    /**
     * 指令17: 评论发送成功
     */
    fun logCommentSentSuccess() {
        Log.d(TAG, "COMMENT_SENT_SUCCESS")
    }

    /**
     * 指令29: 选择按点赞排序
     */
    fun logSortByLikesSelected() {
        Log.d(TAG, "SORT_BY_LIKES_SELECTED")
    }

    /**
     * 指令29: 找到点赞最高的评论
     * @param likes 点赞数
     */
    fun logTopLikedCommentFound(likes: Int) {
        Log.d(TAG, "TOP_LIKED_COMMENT_FOUND: likes=$likes")
    }

    // ==================== 搜索相关 ====================

    /**
     * 指令4,21,23: 输入搜索内容
     * @param keyword 搜索关键词
     */
    fun logSearchInput(keyword: String) {
        Log.d(TAG, "SEARCH_INPUT: $keyword")
    }

    /**
     * 指令4,21,23: 点击搜索按钮
     */
    fun logSearchButtonClicked() {
        Log.d(TAG, "SEARCH_BUTTON_CLICKED")
    }

    /**
     * 指令21,22,23: 搜索完成
     * @param keyword 搜索关键词
     */
    fun logSearchCompleted(keyword: String) {
        Log.d(TAG, "SEARCH_COMPLETED: $keyword")
    }

    /**
     * 指令21: 进入搜索结果页面
     */
    fun logSearchResultsPageEntered() {
        Log.d(TAG, "SEARCH_RESULTS_PAGE_ENTERED")
    }

    /**
     * 指令21: 显示搜索结果数量
     * @param count 结果数量
     */
    fun logSearchResultsCountDisplayed(count: Int) {
        Log.d(TAG, "SEARCH_RESULTS_COUNT_DISPLAYED: $count")
    }

    /**
     * 指令23: 点击第一个搜索结果
     */
    fun logFirstSearchResultClicked() {
        Log.d(TAG, "FIRST_SEARCH_RESULT_CLICKED")
    }

    /**
     * 指令4: 游戏搜索页面加载完成
     */
    fun logGameSearchPageLoaded() {
        Log.d(TAG, "GAME_SEARCH_PAGE_LOADED")
    }

    // ==================== 点赞相关 ====================

    /**
     * 指令6,23: 点击点赞按钮
     */
    fun logLikeButtonClicked() {
        Log.d(TAG, "LIKE_BUTTON_CLICKED")
    }

    /**
     * 指令6,23: 点赞状态改变（可选）
     * @param isLiked true表示已点赞，false表示取消点赞
     */
    fun logLikeStatusChanged(isLiked: Boolean) {
        val status = if (isLiked) "liked" else "unliked"
        Log.d(TAG, "LIKE_STATUS_CHANGED: $status")
    }

    // ==================== 离线缓存相关 ====================

    /**
     * 指令13: 进入离线缓存页面
     */
    fun logOfflineCachePageEntered() {
        Log.d(TAG, "OFFLINE_CACHE_PAGE_ENTERED")
    }

    /**
     * 指令13: 缓存列表加载完成
     */
    fun logCacheListLoaded() {
        Log.d(TAG, "CACHE_LIST_LOADED")
    }

    // ==================== 历史记录相关 ====================

    /**
     * 指令1,28: 进入历史记录页面
     */
    fun logHistoryPageEntered() {
        Log.d(TAG, "HISTORY_PAGE_ENTERED")
    }

    /**
     * 指令1,28: 历史数据加载完成
     * @param count 历史记录数量
     */
    fun logHistoryDataLoaded(count: Int) {
        Log.d(TAG, "HISTORY_DATA_LOADED: $count")
    }

    /**
     * 指令28: 找到昨天的视频
     */
    fun logYesterdayVideoFound() {
        Log.d(TAG, "YESTERDAY_VIDEO_FOUND")
    }

    /**
     * 指令28: 长按历史记录项
     */
    fun logHistoryItemLongPressed() {
        Log.d(TAG, "HISTORY_ITEM_LONG_PRESSED")
    }

    /**
     * 指令28: 点击删除按钮
     */
    fun logDeleteButtonClicked() {
        Log.d(TAG, "DELETE_BUTTON_CLICKED")
    }

    /**
     * 指令28: 历史记录删除成功
     * @param remainingCount 删除后剩余数量
     */
    fun logHistoryItemDeleted(remainingCount: Int) {
        Log.d(TAG, "HISTORY_ITEM_DELETED: count=$remainingCount")
    }

    /**
     * 指令1: 查看历史记录标签
     */
    fun logHistoryTabViewed() {
        Log.d(TAG, "HISTORY_TAB_VIEWED")
    }

    // ==================== 个人信息相关 ====================

    /**
     * 指令10: 个人资料标签
     */
    fun logPersonTab() {
        Log.d(TAG, "PersonTab")
    }

    /**
     * 指令10: 进入个人资料页
     */
    fun logProfilePageEntered() {
        Log.d(TAG, "PROFILE_PAGE_ENTERED")
    }

    /**
     * 指令10: 个人资料数据加载完成
     */
    fun logProfileDataLoaded() {
        Log.d(TAG, "PROFILE_DATA_LOADED")
    }

    // ==================== 设置相关 ====================

    /**
     * 指令30: 进入设置页面
     */
    fun logSettingsPageEntered() {
        Log.d(TAG, "SETTINGS_PAGE_ENTERED")
    }

    /**
     * 指令30: 找到定时关闭选项
     */
    fun logTimerShutdownOptionFound() {
        Log.d(TAG, "TIMER_SHUTDOWN_OPTION_FOUND")
    }

    /**
     * 指令30: 点击定时关闭
     */
    fun logTimerShutdownClicked() {
        Log.d(TAG, "TIMER_SHUTDOWN_CLICKED")
    }

    /**
     * 指令30: 定时关闭状态加载
     * @param isEnabled true表示开启，false表示关闭
     */
    fun logTimerShutdownStatusLoaded(isEnabled: Boolean) {
        val status = if (isEnabled) "on" else "off"
        Log.d(TAG, "TIMER_SHUTDOWN_STATUS_LOADED: $status")
    }

    // ==================== 会员相关 ====================

    /**
     * 指令3,27: 进入会员页面
     */
    fun logVipPageEntered() {
        Log.d(TAG, "VIP_PAGE_ENTERED")
    }

    /**
     * 指令3: 会员数据加载完成
     * @param status 会员状态（如：正式会员）
     */
    fun logVipDataLoaded(status: String) {
        Log.d(TAG, "VIP_DATA_LOADED: $status")
    }

    /**
     * 指令27: 显示会员有��期
     * @param expireDate 有效期日期（格式：yyyy-MM-dd）
     */
    fun logVipExpireDateDisplayed(expireDate: String) {
        Log.d(TAG, "VIP_EXPIRE_DATE_DISPLAYED: $expireDate")
    }

    /**
     * 指令3: 查看会员状态
     */
    fun logVipStatusViewed() {
        Log.d(TAG, "VIP_STATUS_VIEWED")
    }

    // ==================== 首页相关 ====================

    /**
     * 指令20,29: 首页激活
     */
    fun logHomePageActive() {
        Log.d(TAG, "HOME_PAGE_ACTIVE")
    }

    /**
     * 指令29: 点击第一个视频
     */
    fun logFirstVideoClicked() {
        Log.d(TAG, "FIRST_VIDEO_CLICKED")
    }

    // ==================== 直播相关 ====================

    /**
     * 指令31: 进入直播标签页
     */
    fun logLiveTabEntered() {
        Log.d(TAG, "LIVE_TAB_ENTERED")
    }

    /**
     * 指令31: 直播推荐列表加载完成
     */
    fun logLiveRecommendLoaded() {
        Log.d(TAG, "LIVE_RECOMMEND_LOADED")
    }

    /**
     * 指令31: 找到第一个直播
     */
    fun logFirstLiveFound() {
        Log.d(TAG, "FIRST_LIVE_FOUND")
    }

    /**
     * 指令31: 显示直播观看人数
     * @param viewerCount 观看人数（可以带单位，如：1.2万）
     */
    fun logLiveViewerCountDisplayed(viewerCount: String) {
        Log.d(TAG, "LIVE_VIEWER_COUNT_DISPLAYED: $viewerCount")
    }

    // ==================== 频道相关 ====================

    /**
     * 指令5: 点击动画频道图标
     */
    fun logAnimationChannelClicked() {
        Log.d(TAG, "ANIMATION_CHANNEL_CLICKED")
    }

    /**
     * 指令5: 进入动画频道页面
     */
    fun logAnimationChannelPageEntered() {
        Log.d(TAG, "ANIMATION_CHANNEL_PAGE_ENTERED")
    }

    /**
     * 指令5: 动画频道数据加载完成
     */
    fun logAnimationChannelDataLoaded() {
        Log.d(TAG, "ANIMATION_CHANNEL_DATA_LOADED")
    }

    // ==================== 弹幕相关 ====================

    /**
     * 指令26: 弹幕开关点击
     */
    fun logDanmakuSwitchClicked() {
        Log.d(TAG, "DANMAKU_SWITCH_CLICKED")
    }

    /**
     * 指令26: 弹幕状态改变
     * @param isOn true表示打开，false表示关闭
     */
    fun logDanmakuStatusChanged(isOn: Boolean) {
        val status = if (isOn) "on" else "off"
        Log.d(TAG, "DANMAKU_STATUS_CHANGED: $status")
    }

    /**
     * 指令26: 弹幕初始状态
     * @param isOn true表示打开，false表示关闭
     */
    fun logDanmakuInitialState(isOn: Boolean) {
        val status = if (isOn) "on" else "off"
        Log.d(TAG, "DANMAKU_INITIAL_STATE: $status")
    }
}
