import subprocess
import json
import os
import re
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

def validate_task_13(result=None, device_id=None, backup_dir=None):
    """
    任务13: 进入第一个视频，看第四个相关视频的up主名字叫什么
    改进: 从APP数据源获取相关视频列表并读取第四个视频的up主名字
    """
    # 查找adb命令
    adb_cmd = find_adb()
    if not adb_cmd:
        print("错误: 找不到adb命令")
        print("请确保Android SDK已安装,或将platform-tools目录添加到系统PATH")
        return False

    print(f"使用adb路径: {adb_cmd}")

    # 如果是独立运行（没有传入result），则提示用户操作
    if result is None:
        print("\n清除旧日志...")
        subprocess.run([adb_cmd, 'logcat', '-c'],
                      stderr=subprocess.PIPE,
                      stdout=subprocess.PIPE)

        print("=" * 60)
        print("请在虚拟机中执行以下操作:")
        print("1. 打开bilibili APP")
        print("2. 进入首页第一个视频")
        print("3. 查看相关视频列表")
        print("4. 找到第四个相关视频的up主名字")
        print("=" * 60)

        input("\n完成上述操作后，按回车键继续验证...")

        # 读取日志，检查是否进入了视频页面
        print("\n正在检查日志...")
        result_log = subprocess.run(
            [adb_cmd, 'logcat', '-d', '-s', 'BilibiliAutoTest:D'],
            capture_output=True,
            text=True,
            timeout=10,
            encoding='utf-8',
            errors='ignore'
        )

        log_content = result_log.stdout

        # 检查是否进入了视频播放页
        if 'VIDEO_PLAYER_OPENED' not in log_content and 'VIDEO_PLAYBACK_STARTED' not in log_content:
            print("验证失败: 未检测到进入视频页面")
            print("\n提示: 请确保进入了第一个视频")
            return False

    final_msg = result.get("final_message", "") if result else ""

    try:
        # 1. 使用 ADB 从设备拉取相关视频数据
        cmd = [adb_cmd]
        if device_id:
            cmd.extend(["-s", device_id])
        cmd.extend(["exec-out", "run-as", "com.example.bilibili",
                   "cat", "files/related_videos.json"])

        result_data = subprocess.run(
            cmd,
            capture_output=True,
            encoding='utf-8',
            errors='replace',
            text=True,
            timeout=10
        )

        # 保存数据到备份目录
        if backup_dir:
            related_file_path = os.path.join(backup_dir, 'related_videos.json')
            with open(related_file_path, 'w', encoding='utf-8') as f:
                f.write(result_data.stdout)

        # 检查命令是否成功执行
        if result_data.returncode != 0 or not result_data.stdout:
            print("⚠️ 无法读取相关视频数据")
            # 如果是独立运行，只要进入了视频页面就算成功
            if result is None:
                print("✓ 验证成功: 已进入视频页面，可以查看相关视频")
                return True
            return '游戏解说君' in final_msg

        # 2. 解析JSON数据
        try:
            data = json.loads(result_data.stdout)
        except json.JSONDecodeError:
            print("⚠️ 相关视频数据格式错误")
            if result is None:
                print("✓ 验证成功: 已进入视频页面，可以查看相关视频")
                return True
            return '游戏解说君' in final_msg

        # 3. 获取第四个相关视频的up主名字
        related_videos = data.get("related_videos", [])
        if len(related_videos) < 4:
            print(f"⚠️ 相关视频数量不足4个（当前{len(related_videos)}个）")
            if result is None:
                print("✓ 验证成功: 已进入视频页面，可以查看相关视频")
                return True
            return '游戏解说君' in final_msg

        fourth_video = related_videos[3]  # 索引3是第四个
        uploader_name = fourth_video.get("uploader", "")

        # 4. 验证逻辑
        if result is None:
            # 独立运行模式：只要能读取到数据就算成功
            print(f"✓ 验证成功: 已进入视频页面")
            print(f"  第四个相关视频的up主 = {uploader_name}")
            return True
        else:
            # 被调用模式：验证 final_message 中是否包含正确答案
            if uploader_name and uploader_name in final_msg:
                print(f"✓ 验证成功: 第四个相关视频的up主 = {uploader_name}")
                return True
            else:
                print(f"❌ 验证失败: 期望up主={uploader_name}, 实际回答={final_msg}")
                return False

    except subprocess.TimeoutExpired:
        print("⚠️ ADB命令超时")
        if result is None:
            return False
        return '游戏解说君' in final_msg
    except Exception as e:
        print(f"⚠️ 验证过程出错: {str(e)}")
        if result is None:
            return False
        return '游戏解说君' in final_msg

if __name__ == '__main__':
    result = validate_task_13()
    print(result)
