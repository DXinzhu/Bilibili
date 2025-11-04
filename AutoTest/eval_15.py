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

def CheckRecentVisit():
    """
    检验逻辑:点击关注页，查看最近访问的up主
    验证用户是否在关注页查看了最近访问的up主
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
        print("2. 点击底部'关注'页")
        print("3. 查看最近访问的up主")
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

        # step3. 验证是否进入关注页
        if 'FOLLOW_PAGE_ENTERED' not in log_content:
            print("验证失败: 未检测到进入关注页")
            return False

        # step4. 验证是否点击了最近访问标签
        if 'RECENT_VISIT_TAB_CLICKED' not in log_content:
            print("验证失败: 未检测到点击最近访问标签")
            return False

        # step5. 验证是否加载了最近访问列表
        if 'RECENT_VISIT_LOADED' not in log_content:
            print("验证失败: 最近访问列表未加载")
            return False

        print("查看最近访问验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查最近访问时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckRecentVisit()
    print(result)
