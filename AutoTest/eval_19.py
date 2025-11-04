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

def CheckLikeComment():
    """
    检验逻辑:在首页推荐视频评论页面，为最新评论点赞
    验证用户是否为最新评论点赞
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
        print("2. 在首页打开推荐视频的评论页面")
        print("3. 为最新评论点赞")
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

        # step4. 验证是否打开了评论页面
        if 'COMMENT_PAGE_ENTERED' not in log_content:
            print("验证失败: 未进入评论页面")
            return False

        # step5. 验证是否加载了评论列表
        if 'COMMENT_LIST_LOADED' not in log_content:
            print("验证失败: 评论列表未加载")
            return False

        # step6. 验证是否为评论点赞
        if 'COMMENT_LIKE_CLICKED' not in log_content:
            print("验证失败: 未检测到为评论点赞")
            return False

        # step7. 验证评论点赞状态是否更新
        if 'COMMENT_LIKE_STATUS_CHANGED' not in log_content:
            print("验证失败: 评论点赞状态未更新")
            return False

        print("评论点赞验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查评论点赞时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckLikeComment()
    print(result)
