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

def CheckTopLikedComment():
    """
    检验逻辑:在首页第一条视频评论页面，找到一条点赞数最高的评论
    验证用户是否在APP中真正找到了点赞数最高的评论
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
        print("2. 在首页点击第一条视频")
        print("3. 进入评论页面")
        print("4. 切换到'按热度排序'(如有)")
        print("5. 找到点赞数最高的评论")
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

        # step4. 验证是否点击了第一条视频
        if 'FIRST_VIDEO_CLICKED' not in log_content:
            print("验证失败: 未检测到点击第一条视频")
            return False

        # step5. 验证是否进入评论页面
        if 'COMMENT_PAGE_ENTERED' not in log_content:
            print("验证失败: 未检测到进入评论页面")
            return False

        # step6. 验证是否成功加载评论列表
        if 'COMMENT_LIST_LOADED' not in log_content:
            print("验证失败: 未检测到评论列表加载")
            return False

        # step7. 验证是否切换到"按热度排序"
        if 'SORT_BY_LIKES_SELECTED' not in log_content:
            print("验证失败: 未检测到切换按热度排序")
            return False

        # step8. 验证是否找到点赞数最高的评论
        if 'TOP_LIKED_COMMENT_FOUND' not in log_content:
            print("验证失败: 未检测到找到点赞数最高的评论")
            return False

        # step9. 提取并验证该评论的点赞数
        likes_pattern = r'TOP_LIKED_COMMENT_FOUND:\s*likes=(\d+)'
        likes_match = re.search(likes_pattern, log_content)

        if likes_match:
            top_likes = int(likes_match.group(1))
            print(f"找到点赞数最高的评论,点赞数: {top_likes}")
        else:
            print("警告: 无法提取点赞数")

        print("查找点赞数最高评论验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查点赞数最高评论时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckTopLikedComment()
    print(result)
