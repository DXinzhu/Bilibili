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

def CheckFirstFavoriteDuration():
    """
    检验逻辑:查看收藏的第一个视频的视频时长
    验证用户是否在APP中真正查看了第一个收藏视频的时长
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
        print("3. 点击'我的收藏'")
        print("4. 点击第一个收藏视频查看时长")
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

        # step3. 验证是否进入收藏页面
        if 'FAVORITE_PAGE_ENTERED' not in log_content:
            print("验证失败: 未检测到进入收藏页面")
            return False

        # step4. 验证是否成功加载收藏列表
        if 'FAVORITE_DATA_LOADED' not in log_content:
            print("验证失败: 未检测到收藏列表加载")
            return False

        # step5. 验证是否点击了第一个收藏视频
        if 'FIRST_FAVORITE_VIDEO_CLICKED' not in log_content:
            print("验证失败: 未检测到点击第一个收藏视频")
            return False

        # step6. 验证是否成功获取视频时长
        if 'VIDEO_DURATION_DISPLAYED' not in log_content:
            print("验证失败: 未检测到视频时长显示")
            return False

        # step7. 提取并验证时长格式
        duration_pattern = r'VIDEO_DURATION_DISPLAYED:\s*(\d{1,2}:\d{2}(?::\d{2})?)'
        duration_match = re.search(duration_pattern, log_content)

        if not duration_match:
            print("验证失败: 无法提取视频时长")
            return False

        duration = duration_match.group(1)

        # 验证时长格式是否正确(MM:SS 或 HH:MM:SS)
        if not re.match(r'^\d{1,2}:\d{2}(:\d{2})?$', duration):
            print("验证失败: 视频时长格式不正确")
            return False

        # step8. 验证时长与assets数据是否一致(可选)
        project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        assets_path = os.path.join(project_root, 'app', 'src', 'main', 'assets', 'data', 'favorites.json')

        if os.path.exists(assets_path):
            with open(assets_path, 'r', encoding='utf-8') as f:
                favorites_data = json.load(f)
                if isinstance(favorites_data, list) and len(favorites_data) > 0:
                    expected_duration = favorites_data[0].get('duration', '')
                    if duration == expected_duration:
                        print(f"时长验证通过: {duration}")
                    else:
                        print(f"警告: 时长不匹配 (期望:{expected_duration}, 实际:{duration})")

        print("查看收藏视频时长验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查收藏视频时长时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckFirstFavoriteDuration()
    print(result)
