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

def CheckLikeVideo(result=None,device_id=None,backup_dir=None):
    """
    检验逻辑:在视频播放页，点击「点赞」按钮
    验证用户是否在视频播放页点击了点赞按钮
    """
    try:
        adb_path = find_adb()
        if not adb_path:
            print("错误: 找不到 adb 命令")
            return False

        print("\n正在检查日志...")
        cmd_logcat = [adb_path]
        if device_id:
            cmd_logcat.extend(['-s', device_id])
        cmd_logcat.extend(['logcat', '-d', '-s', 'BilibiliAutoTest:D'])

        result1 = subprocess.run(
            cmd_logcat,
            capture_output=True,
            text=True,
            timeout=10,
            encoding='utf-8',
            errors='ignore'
        )

        log_content = result1.stdout
        if backup_dir:
            logcat_file_path = os.path.join(backup_dir, 'logcat.txt')
            open(logcat_file_path, 'w', encoding='utf-8').write(log_content)

        # step3. 验证是否进入视频播放页
        if 'VIDEO_PLAYER_OPENED' not in log_content:
            print("验证失败: 未检测到进入视频播放页")
            print(f"日志内容:\n{log_content}")
            return False

        # step4. 验证是否点击了点赞按钮
        if 'LIKE_BUTTON_CLICKED' not in log_content:
            print("验证失败: 未检测到点击点赞按钮")
            print(f"日志内容:\n{log_content}")
            return False

        # step5. 验证点赞状态是否更新
        if 'LIKE_STATUS_CHANGED' not in log_content:
            print("验证失败: 点赞状态未更新")
            print(f"日志内容:\n{log_content}")
            return False

        # 检查是否包含 liked 或 unliked
        if 'LIKE_STATUS_CHANGED: liked' not in log_content and 'LIKE_STATUS_CHANGED: unliked' not in log_content:
            print("验证失败: 点赞状态格式不正确")
            print(f"日志内容:\n{log_content}")
            return False

        print("点赞操作验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    finally:
        # 无论成功失败，最后都清除日志
        try:
            adb_path = find_adb()
            if adb_path:
                cmd_clear = [adb_path]
                if device_id:
                    cmd_clear.extend(['-s', device_id])
                cmd_clear.extend(['logcat', '-c'])
                subprocess.run(cmd_clear, timeout=5)
                print("🔄 已清除日志缓存")
            else:
                print("⚠️ 找不到 adb，无法清除日志")
        except subprocess.TimeoutExpired:
            print("⚠️ 清除日志超时")
        except Exception as e:
            print(f"⚠️ 清除日志失败: {str(e)}")

if __name__ == "__main__":
    result1 = CheckLikeVideo()
    print(result1)
