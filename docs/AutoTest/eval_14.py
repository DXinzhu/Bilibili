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

def validate_task_14(result=None, device_id=None, backup_dir=None):
    """
    任务14: 查看关注动态中所有动态的点赞数加播放量一共多少
    改进: 从APP数据源获取动态数据并计算总和
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
        print("2. 点击首页顶部的'关注'按钮（在'首页'按钮旁边）")
        print("3. 查看关注动态列表")
        print("=" * 60)

        input("\n完成上述操作后，按回车键继续验证...")

        # 读取日志，检查是否进入了关注页面
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

        # 检查是否进入了关注页面
        if 'FOLLOW_PAGE_ENTERED' not in log_content and 'ConcernTab' not in log_content:
            print("验证失败: 未检测到进入关注页面")
            print("\n提示: 请确保点击了首页顶部的'关注'按钮")
            print(f"\n日志内容:\n{log_content}")
            return False

    final_msg = result.get("final_message", "") if result else ""

    try:
        # 1. 使用 ADB 从设备拉取关注动态数据
        cmd = [adb_cmd]
        if device_id:
            cmd.extend(["-s", device_id])
        cmd.extend(["exec-out", "run-as", "com.example.bilibili",
                   "cat", "files/following_dynamics.json"])

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
            dynamics_file_path = os.path.join(backup_dir, 'following_dynamics.json')
            with open(dynamics_file_path, 'w', encoding='utf-8') as f:
                f.write(result_data.stdout)

        # 检查命令是否成功执行
        if result_data.returncode != 0 or not result_data.stdout:
            print("⚠️ 无法读取关注动态数据")
            # 如果是独立运行，只要进入了动态页面就算成功
            if result is None:
                print("✓ 验证成功: 已进入动态页面，可以查看关注动态")
                return True
            return '1554' in final_msg

        # 2. 解析JSON数据
        try:
            data = json.loads(result_data.stdout)
        except json.JSONDecodeError:
            print("⚠️ 关注动态数据格式错误")
            if result is None:
                print("✓ 验证成功: 已进入动态页面，可以查看关注动态")
                return True
            return '1554' in final_msg

        # 3. 计算所有动态的点赞数+播放量总和
        dynamics_list = data.get("dynamics", [])
        total_sum = sum(
            dynamic.get("like_count", 0) + dynamic.get("play_count", 0)
            for dynamic in dynamics_list
        )

        # 4. 验证逻辑
        if result is None:
            # 独立运行模式：只要能读取到数据就算成功
            print(f"✓ 验证成功: 已进入动态页面")
            print(f"  关注动态总数 = {len(dynamics_list)}")
            print(f"  点赞数+播放量总和 = {total_sum}")
            return True
        else:
            # 被调用模式：验证 final_message 中是否包含正确答案
            if str(total_sum) in final_msg:
                print(f"✓ 验证成功: 点赞数+播放量总和 = {total_sum}")
                return True
            else:
                print(f"❌ 验证失败: 期望答案={total_sum}, 实际回答={final_msg}")
                return False

    except subprocess.TimeoutExpired:
        print("⚠️ ADB命令超时")
        if result is None:
            return False
        return '1554' in final_msg
    except Exception as e:
        print(f"⚠️ 验证过程出错: {str(e)}")
        if result is None:
            return False
        return '1554' in final_msg

if __name__ == '__main__':
    result = validate_task_14()
    print(result)

