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

def CheckAnimationChannel():
    """
    检验逻辑:点击首页动画频道图标，进入动画频道页
    验证用户是否进入动画频道页面
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
        print("2. 在首页点击动画频道图标")
        print("3. 进入动画频道页")
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

        # step3. 验证是否点击了动画频道图标
        if 'CHANNEL_ICON_CLICKED' not in log_content or '动画' not in log_content:
            print("验证失败: 未检测到点击动画频道图标")
            print(f"日志内容:\n{log_content}")
            return False

        # step4. 验证是否进入动画频道页面
        if 'ANIMATION_CHANNEL_ENTERED' not in log_content:
            print("验证失败: 未进入动画频道页面")
            print(f"日志内容:\n{log_content}")
            return False

        # step5. 验证是否加载了频道内容
        if 'CHANNEL_CONTENT_LOADED' not in log_content:
            print("验证失败: 频道内容未加载")
            print(f"日志内容:\n{log_content}")
            return False

        print("动画频道验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查动画频道时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckAnimationChannel()
    print(result)
