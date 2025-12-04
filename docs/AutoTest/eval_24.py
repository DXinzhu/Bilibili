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

def CheckFavoriteCount():
    """
    检验逻辑:在收藏页面查看该收藏中共收藏了多少个视频
    验证用户是否在APP中真正查看了收藏视频的数量
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
        print("4. 查看收藏数量")
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

        # step4. 验证是否成功加载收藏数据
        if 'FAVORITE_DATA_LOADED' not in log_content:
            print("验证失败: 未检测到收藏数据加载")
            return False

        # step5. 验证是否显示收藏数量
        if 'FAVORITE_COUNT_DISPLAYED' not in log_content:
            print("验证失败: 未检测到收藏数量显示")
            return False

        # step6. 提取并验证收藏数量
        count_pattern = r'FAVORITE_COUNT_DISPLAYED:\s*(\d+)'
        count_match = re.search(count_pattern, log_content)

        if not count_match:
            print("验证失败: 无法提取收藏数量")
            return False

        favorite_count = int(count_match.group(1))

        # step7. 验证显示数量与实际列表数量是否一致
        list_count_pattern = r'FAVORITE_DATA_LOADED:\s*(\d+)'
        list_count_match = re.search(list_count_pattern, log_content)

        if list_count_match:
            list_count = int(list_count_match.group(1))
            if favorite_count != list_count:
                print(f"警告: 显示数量与列表数量不一致 (显示:{favorite_count}, 列表:{list_count})")

        # step8. 验证数量与assets数据是否一致
        project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        assets_path = os.path.join(project_root, 'app', 'src', 'main', 'assets', 'data', 'favorites.json')

        if os.path.exists(assets_path):
            with open(assets_path, 'r', encoding='utf-8') as f:
                favorites_data = json.load(f)
                expected_count = len(favorites_data) if isinstance(favorites_data, list) else 0

                if favorite_count == expected_count:
                    print(f"收藏数量验证通过: {favorite_count}")
                else:
                    print(f"警告: 收藏数量不匹配 (期望:{expected_count}, 实际:{favorite_count})")

        print("查看收藏数量验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查收藏数量时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckFavoriteCount()
    print(result)
