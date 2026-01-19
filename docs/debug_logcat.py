#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
调试脚本 - 查看实际的 logcat 输出
"""

import subprocess
import sys
import os
import shutil

def find_adb():
    """查找 ADB 命令路径"""
    adb_path = shutil.which('adb')
    if adb_path:
        return adb_path

    # Windows 常见路径
    possible_paths = [
        r'C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe',
        r'C:\Android\sdk\platform-tools\adb.exe',
        r'D:\Android\sdk\platform-tools\adb.exe',
        r'%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe',
    ]

    for path in possible_paths:
        expanded_path = os.path.expandvars(path)
        if os.path.exists(expanded_path):
            return expanded_path

    return 'adb'

def read_logcat(device_id=None):
    """读取 logcat 日志"""
    adb_cmd = find_adb()
    print(f"使用 ADB 路径: {adb_cmd}\n")

    cmd = [adb_cmd]
    if device_id:
        cmd.extend(['-s', device_id])
    cmd.extend(['logcat', '-d', '-s', 'BilibiliAutoTest:D'])

    try:
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            timeout=10,
            encoding='utf-8',
            errors='ignore'
        )
        return result.stdout
    except Exception as e:
        print(f"读取日志失败: {e}")
        return ""

if __name__ == '__main__':
    print("正在读取 logcat 日志...")
    print("="*60)

    log_content = read_logcat()

    if log_content:
        print("日志内容:")
        print(log_content)
        print("="*60)
        print(f"\n日志长度: {len(log_content)} 字符")

        # 检查是否包含任务30的关键日志
        if 'VIP_SHOP_DATA_LOADED' in log_content:
            print("✓ 找到会员购数据日志")
        else:
            print("✗ 未找到会员购数据日志")
            print("\n提示: 请确保在应用中调用了 BilibiliAutoTestLogger.logVipShopDataLoaded()")
    else:
        print("未读取到任何日志")
        print("\n可能的原因:")
        print("1. ADB 未连接到设备")
        print("2. 应用未输出日志")
        print("3. 日志标签不匹配")
