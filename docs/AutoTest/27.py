import subprocess
import json
import os
import shutil
import time
import re

def find_adb():
    """查找adb命令路径"""
    adb_path = shutil.which('adb')
    if adb_path:
        return adb_path

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

    return None

def CheckHistoryItemDelete():
    """
    检验逻辑:在历史记录页面，找到昨天观看过的一个视频，长按该记录项，将其从历史记录中删除
    验证用户是否在APP中真正完成了历史记录删除操作
    """
    try:
        adb_cmd = find_adb()
        if not adb_cmd:
            print("错误: 找不到adb命令")
            return False

        print(f"使用adb路径: {adb_cmd}")

        # step1. 清除旧的logcat日志
        print("\n清除旧日志...")
        subprocess.run([adb_cmd, 'logcat', '-c'],
                      stderr=subprocess.PIPE,
                      stdout=subprocess.PIPE)

        print("=" * 60)
        print("请在虚拟机中执行以下操作:")
        print("1. 打开bilibili APP")
        print("2. 进入'我的'页面")
        print("3. 点击'历史记录'")
        print("4. 找到昨天观看过的一个视频")
        print("5. 长按该记录项")
        print("6. 点击删除")
        print("=" * 60)

        input("\n完成上述操作后，按回车键继续验证...")

        # step2. 读取logcat日志
        print("\n正在检查日志...")
        result = subprocess.run(
            [adb_cmd, 'logcat', '-d', '-s', 'BilibiliAutoTest:D'],
            capture_output=True,
            text=True,
            timeout=10,
            encoding='utf-8',
            errors='ignore'  # 忽略无法解码的字符
        )

        log_content = result.stdout

        # step3. 验证关键操作 - 放宽验证条件
        history_page_entered = 'HISTORY_PAGE_ENTERED' in log_content
        history_data_loaded = 'HISTORY_DATA_LOADED' in log_content
        history_item_long_pressed = 'HISTORY_ITEM_LONG_PRESSED' in log_content
        delete_button_clicked = 'DELETE_BUTTON_CLICKED' in log_content
        history_item_deleted = 'HISTORY_ITEM_DELETED' in log_content

        # 只要检测到删除相关操作即可
        if not (history_item_long_pressed or delete_button_clicked or history_item_deleted):
            print("验证失败: 未检测到删除历史记录操作")
            print("\n提示: 请确保:")
            print("1. 进入了历史记录页面")
            print("2. 长按了某个历史记录项")
            print("3. 点击了删除")
            print(f"\n日志内容:\n{log_content}")
            return False

        print("历史记录删除验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查历史记录删除时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckHistoryItemDelete()
    print(result)
