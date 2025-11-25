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
    检验逻辑:点击关注页
    验证用户是否进入了关注页面
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

        # 打印捕获到的日志内容用于调试
        print("\n捕获到的日志内容:")
        print("-" * 60)
        print(log_content if log_content.strip() else "(无日志)")
        print("-" * 60)

        # step3. 验证关键操作 - 只需要检测到进入关注页即可
        # 检测多种可能的关注页标记
        follow_page_entered = any([
            'FOLLOW_PAGE_ENTERED' in log_content,
            'FollowPage' in log_content,
            'follow_page' in log_content,
            '关注页' in log_content,
        ])

        if not follow_page_entered:
            print("\n验证失败: 未检测到进入关注页")
            print("\n提示: 请确保点击了底部导航栏的'关注'页")
            return False

        print("\n进入关注页验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查关注页时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckRecentVisit()
    print(result)
