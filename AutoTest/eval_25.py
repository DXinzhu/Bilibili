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

def CheckDanmakuToggle():
    """
    检验逻辑:在视频播放页面，点击"弹幕"开关将其关闭，然后重新打开
    验证用户是否在APP中真正完成了弹幕的关闭和打开操作
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
        print("3. 点击弹幕开关关闭弹幕")
        print("4. 再次点击弹幕开关打开弹幕")
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

        # step3. 验证是否在视频播放页
        if 'VIDEO_PLAYER_OPENED' not in log_content:
            print("验证失败: 未检测到进入视频播放页")
            return False

        # step4. 验证初始弹幕状态
        if 'DANMAKU_INITIAL_STATE' not in log_content or 'on' not in log_content:
            print("验证失败: 未检测到初始弹幕状态")
            return False

        # step5. 验证是否点击弹幕开关(第一次)
        danmaku_click_count = log_content.count('DANMAKU_SWITCH_CLICKED')
        if danmaku_click_count < 2:
            print(f"验证失败: 弹幕开关点击次数不足 (需要2次, 实际{danmaku_click_count}次)")
            return False

        # step6. 验证弹幕是否关闭
        if 'DANMAKU_STATUS_CHANGED' not in log_content or 'off' not in log_content:
            print("验证失败: 未检测到弹幕关闭")
            return False

        # step7. 验证弹幕是否重新打开
        lines = log_content.split('\n')
        status_changes = []
        for line in lines:
            if 'DANMAKU_STATUS_CHANGED' in line:
                if 'off' in line:
                    status_changes.append('off')
                elif 'on' in line:
                    status_changes.append('on')

        if len(status_changes) < 2 or status_changes[-2:] != ['off', 'on']:
            print("验证失败: 弹幕开关状态变化不正确")
            return False

        print("弹幕开关验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查弹幕开关时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckDanmakuToggle()
    print(result)
