import subprocess
import os
import shutil

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
    检验逻辑:在视频播放页面，点击"弹幕"开关
    验证用户是否点击了弹幕开关按钮（UI变化）
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
        print("3. 点击弹幕开关按钮（UI变化即可）")
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

        # step3. 验证是否在视频播放页
        if 'VIDEO_PLAYER_OPENED' not in log_content:
            print("验证失败: 未检测到进入视频播放页")
            print(f"日志内容:\n{log_content}")
            return False

        # step4. 验证是否点击弹幕开关
        if 'DANMAKU_SWITCH_CLICKED' not in log_content:
            print("验证失败: 未检测到弹幕开关点击")
            print(f"日志内容:\n{log_content}")
            return False

        # step5. 验证弹幕状态是否改变
        if 'DANMAKU_STATUS_CHANGED' not in log_content:
            print("验证失败: 未检测到弹幕状态变化")
            print(f"日志内容:\n{log_content}")
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
