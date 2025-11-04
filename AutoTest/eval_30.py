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

def CheckLiveViewerCount():
    """
    检验逻辑:在直播推荐页面，查看第一个直播的在线观看人数
    验证用户是否在APP中真正查看了第一个直播的在线观看人数
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
        print("2. 点击底部'直播'标签")
        print("3. 进入直播推荐页面")
        print("4. 查看第一个直播的在线观看人数")
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

        # step3. 验证是否进入直播标签页
        if 'LIVE_TAB_ENTERED' not in log_content:
            print("验证失败: 未检测到进入直播标签页")
            return False

        # step4. 验证是否成功加载直播推荐列表
        if 'LIVE_RECOMMEND_LOADED' not in log_content:
            print("验证失败: 未检测到直播推荐列表加载")
            return False

        # step5. 验证是否找到第一个直播
        if 'FIRST_LIVE_FOUND' not in log_content:
            print("验证失败: 未检测到第一个直播")
            return False

        # step6. 验证是否显示在线观看人数
        if 'LIVE_VIEWER_COUNT_DISPLAYED' not in log_content:
            print("验证失败: 未检测到在线观看人数显示")
            return False

        # step7. 提取并验证观看人数是否>0
        count_pattern = r'LIVE_VIEWER_COUNT_DISPLAYED:\s*(\d+(?:\.\d+)?[万千]?)'
        count_match = re.search(count_pattern, log_content)

        if not count_match:
            print("验证失败: 无法提取观看人数")
            return False

        viewer_count_str = count_match.group(1)

        # step8. 验证人数格式是否正确
        if not re.match(r'^\d+(?:\.\d+)?[万千]?$', viewer_count_str):
            print("验证失败: 观看人数格式不正确")
            return False

        # 简单验证是否>0
        if viewer_count_str.startswith('0') and '.' not in viewer_count_str:
            print("验证失败: 观看人数为0")
            return False

        print(f"在线观看人数: {viewer_count_str}")
        print("查看直播观看人数验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查直播观看人数时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckLiveViewerCount()
    print(result)
