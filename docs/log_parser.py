#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
日志解析器 - 从 Android logcat 中提取关键信息
"""

import re

class LogParser:
    """解析 BilibiliAutoTest 日志"""

    @staticmethod
    def parse_task_17(log_content):
        """
        任务17: 评论区一共有多少个赞
        从日志中提取评论相关信息
        """
        # 查找评论发送成功标记
        if 'COMMENT_SENT_SUCCESS' in log_content:
            return "评论发送成功"

        # 查找评论页面进入标记
        if 'COMMENT_PAGE_ENTERED' in log_content:
            return "进入评论页面"

        return None

    @staticmethod
    def parse_task_18(log_content):
        """
        任务18: 消息通知开关状态
        从日志中提取: SMART_FILTER_STATUS_VIEWED: 开启/关闭
        """
        pattern = r'SMART_FILTER_STATUS_VIEWED: (开启|关闭)'
        match = re.search(pattern, log_content)
        if match:
            status = match.group(1)
            return f"已{status}"
        return None

    @staticmethod
    def parse_task_19(log_content):
        """
        任务19: 等级最低的评论点赞数
        从日志中提取UP主相关信息
        """
        # 查找UP主页面进入
        if 'UPLOADER_PAGE_ENTERED' in log_content:
            return "进入UP主页面"

        # 查找UP主数据加载
        if 'UPLOADER_DATA_LOADED' in log_content:
            return "UP主数据加载完成"

        return None

    @staticmethod
    def parse_task_20(log_content):
        """
        任务20: 搜索游戏结果数量
        从日志中提取: SEARCH_RESULTS_COUNT_DISPLAYED: X
        """
        pattern = r'SEARCH_RESULTS_COUNT_DISPLAYED: (\d+)'
        match = re.search(pattern, log_content)
        if match:
            count = match.group(1)
            return count

        # 查找游戏搜索页面加载
        if 'GAME_SEARCH_PAGE_LOADED' in log_content:
            return "游戏搜索页面已加载"

        return None

    @staticmethod
    def parse_task_21(log_content):
        """
        任务21: 相关视频数量
        从日志中提取搜索结果数量
        """
        pattern = r'SEARCH_RESULTS_COUNT_DISPLAYED: (\d+)'
        match = re.search(pattern, log_content)
        if match:
            count = match.group(1)
            return count
        return None

    @staticmethod
    def parse_task_24(log_content):
        """
        任务24: 收藏视频数量
        从日志中提取: FAVORITE_COUNT_DISPLAYED: X
        """
        pattern = r'FAVORITE_COUNT_DISPLAYED: (\d+)'
        match = re.search(pattern, log_content)
        if match:
            count = match.group(1)
            return count
        return None

    @staticmethod
    def parse_task_23(log_content):
        """
        任务23: 第一个收藏视频的时长
        从日志中提取: FIRST_FAVORITE_VIDEO_DURATION: MM:SS
        """
        pattern = r'FIRST_FAVORITE_VIDEO_DURATION: ([\d:]+)'
        match = re.search(pattern, log_content)
        if match:
            duration = match.group(1)
            return duration
        return None

    @staticmethod
    def parse_task_25(log_content):
        """
        任务25: UID
        从日志中提取: UID_DISPLAYED: X
        """
        pattern = r'UID_DISPLAYED: (\d+)'
        match = re.search(pattern, log_content)
        if match:
            uid = match.group(1)
            return uid
        return None

    @staticmethod
    def parse_task_26(log_content):
        """
        任务26: 历史记录数量
        从日志中提取: HISTORY_DATA_LOADED: X
        """
        pattern = r'HISTORY_DATA_LOADED: (\d+)'
        match = re.search(pattern, log_content)
        if match:
            count = match.group(1)
            return count
        return None

    @staticmethod
    def parse_task_27(log_content):
        """
        任务27: 在首页第一条视频评论页面，找到一条点赞数最高的评论，看看up主的名字叫什么
        从日志中提取: TOP_LIKED_COMMENT_USER: 用户名
        """
        pattern = r'TOP_LIKED_COMMENT_USER: (.+)'
        match = re.search(pattern, log_content)
        if match:
            username = match.group(1).strip()
            return username
        return None

    @staticmethod
    def parse_task_28(log_content):
        """
        任务28: 在设置中，查看当前定时关闭状态
        从日志中提取: TIMER_CLOSE_STATUS_VIEWED: 开启/不开启
        """
        pattern = r'TIMER_CLOSE_STATUS_VIEWED: (开启|不开启|已开启|未开启|关闭|已关闭)'
        match = re.search(pattern, log_content)
        if match:
            status = match.group(1)
            return status
        return None

    @staticmethod
    def parse_task_29(log_content):
        """
        任务29: 直播观看人数
        从日志中提取: LIVE_VIEWER_COUNT_DISPLAYED: X
        或 TOP_LIKED_COMMENT_FOUND: likes=X
        """
        # 查找直播观看人数
        pattern1 = r'LIVE_VIEWER_COUNT_DISPLAYED: ([\d.万]+)'
        match1 = re.search(pattern1, log_content)
        if match1:
            count = match1.group(1)
            return count

        # 查找点赞最高的评论
        pattern2 = r'TOP_LIKED_COMMENT_FOUND: likes=(\d+)'
        match2 = re.search(pattern2, log_content)
        if match2:
            likes = match2.group(1)
            return likes

        return None

    @staticmethod
    def parse_task_30(log_content):
        """
        任务30: 看一下会员购里的商品一共卖了多少件
        从日志中提取: VIP_SHOP_DATA_LOADED: count=X, totalPrice=Y
        """
        # 查找会员购数据
        pattern = r'VIP_SHOP_DATA_LOADED: count=(\d+)'
        match = re.search(pattern, log_content)
        if match:
            count = match.group(1)
            return f"一共卖了{count}件"
        return None

    @staticmethod
    def parse_task_31(log_content):
        """
        任务31: 查看大会员还有多久到期
        从日志中提取: VIP_EXPIRE_DATE_DISPLAYED: 2025-06-01
        """
        # 查找会员到期日期
        pattern = r'VIP_EXPIRE_DATE_DISPLAYED: ([\d-]+)'
        match = re.search(pattern, log_content)
        if match:
            date = match.group(1)
            return f"{date}到期"
        return None
