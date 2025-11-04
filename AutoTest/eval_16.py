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

def CheckReplyComment():
    """
    检验逻辑:对首页第一条视频评论，点击回复，输入"谢谢分享！"并发送
    验证用户是否完成评论回复
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
        print("4. 点击回复按钮")
        print("5. 输入'谢谢分享！'")
        print("6. 点击发送")
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
            print("验证失败: 未进入评论页面")
            return False

        # step6. 验证是否点击了回复按钮
        if 'REPLY_BUTTON_CLICKED' not in log_content:
            print("验证失败: 未检测到点击回复按钮")
            return False

        # step7. 验证是否输入了评论内容
        if 'COMMENT_INPUT_TEXT' not in log_content or '谢谢分享！' not in log_content:
            print("验证失败: 未检测到输入评论内容")
            return False

        # step8. 验证是否点击了发送按钮
        if 'SEND_BUTTON_CLICKED' not in log_content:
            print("验证失败: 未检测到点击发送按钮")
            return False

        # step9. 验证评论是否发送成功
        if 'COMMENT_SENT_SUCCESS' not in log_content:
            print("验证失败: 评论未发送成功")
            return False

        print("评论回复验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查评论回复时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckReplyComment()
    print(result)
