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

def CheckVideoPause():
    """
    检验逻辑:在视频播放页面，暂停播放
    验证用户是否在APP中真正暂停了视频播放
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
        print("2. 打开任意视频播放页面")
        print("3. 等待视频开始播放")
        print("4. 点击暂停按钮暂停播放")
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

        # step3. 验证关键操作 - 只验证暂停操作，不验证播放开始
        video_player_opened = 'VIDEO_PLAYER_OPENED' in log_content
        pause_action_triggered = 'PAUSE_ACTION_TRIGGERED' in log_content
        video_paused = 'VIDEO_PAUSED' in log_content

        # 只要检测到暂停相关操作即可
        if not (pause_action_triggered or video_paused):
            print("验证失败: 未检测到暂停操作")
            print("\n提示: 请确保:")
            print("1. 在视频播放页点击了暂停按钮")
            print(f"\n日志内容:\n{log_content}")
            return False

        print("视频暂停验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查视频暂停时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckVideoPause()
    print(result)
