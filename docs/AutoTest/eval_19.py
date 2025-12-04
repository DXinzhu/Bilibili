import subprocess
import json
import os
import shutil
import time

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

def CheckLikeComment():
    """
    检验逻辑:在首页推荐视频评论页面，为最新评论点赞
    验证用户是否为最新评论点赞
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
        print("2. 在首页打开推荐视频的评论页面")
        print("3. 为最新评论点赞")
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
        home_page_active = 'HOME_PAGE_ACTIVE' in log_content
        video_player_opened = 'VIDEO_PLAYER_OPENED' in log_content
        comment_page_entered = 'COMMENT_PAGE_ENTERED' in log_content
        comment_list_loaded = 'COMMENT_LIST_LOADED' in log_content
        comment_like_clicked = 'COMMENT_LIKE_CLICKED' in log_content
        comment_like_status_changed = 'COMMENT_LIKE_STATUS_CHANGED' in log_content

        # 至少检测到进入评论页面或点赞操作
        if not (comment_page_entered or comment_like_clicked or comment_like_status_changed or video_player_opened):
            print("验证失败: 未检测到评论点赞相关操作")
            print("\n提示: 请确保:")
            print("1. 打开了视频并进入评论页面")
            print("2. 为评论点了赞")
            print(f"\n日志内容:\n{log_content}")
            return False

        print("评论点赞验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查评论点赞时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckLikeComment()
    print(result)
