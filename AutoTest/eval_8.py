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

def CheckWatchRecommend():
    """
    检验逻辑:在首页观看一条推荐中的视频
    验证用户是否在首页点击并观看推荐视频
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
        print("2. 在首页点击一条推荐视频")
        print("3. 观看视频")
        print("=" * 60)

        input("\n完成上述操作后，按回车键继续验证...")

        # step2. 读取logcat日志
        print("\n正在检查日志...")
        result = subprocess.run(
            [adb_cmd, 'logcat', '-d', '-s', 'BilibiliAutoTest:D'],
            capture_output=True,
            text=True,
            timeout=10
        )

        log_content = result.stdout

        # step3. 验证是否在首页
        if 'HOME_PAGE_ACTIVE' not in log_content:
            print("验证失败: 未检测到在首页")
            return False

        # step4. 验证是否点击了推荐视频
        if 'RECOMMEND_VIDEO_CLICKED' not in log_content:
            print("验证失败: 未检测到点击推荐视频")
            return False

        # step5. 验证是否进入视频播放页
        if 'VIDEO_PLAYER_OPENED' not in log_content:
            print("验证失败: 未进入视频播放页")
            return False

        # step6. 验证视频是否开始播放
        if 'VIDEO_PLAYBACK_STARTED' not in log_content:
            print("验证失败: 视频未开始播放")
            return False

        print("观看推荐视频验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查观看推荐视频时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckWatchRecommend()
    print(result)
