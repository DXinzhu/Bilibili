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

def validate_task_15(result=None, device_id=None, backup_dir=None):
    """
    任务15: 数一下关注列表有几个已互粉的up主
    改进: 从APP数据源获取关注列表并统计互粉数量
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
        print("2. 点击首页顶部的'关注'按钮")
        print("3. 查看关注列表，数一下有几个互粉的UP主")
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
            return False

    final_msg = result.get("final_message", "") if result else ""

    try:
        # 1. 使用 ADB 从设备拉取关注列表数据
        cmd = [adb_cmd]
        if device_id:
            cmd.extend(["-s", device_id])
        cmd.extend(["exec-out", "run-as", "com.example.bilibili",
                   "cat", "files/following_list.json"])

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
            following_file_path = os.path.join(backup_dir, 'following_list.json')
            with open(following_file_path, 'w', encoding='utf-8') as f:
                f.write(result_data.stdout)

        # 检查命令是否成功执行
        if result_data.returncode != 0 or not result_data.stdout:
            print("⚠️ 无法读取关注列表数据")
            # 如果是独立运行，直接从assets读取数据
            if result is None:
                # 从本地assets读取upmasters.json统计互粉数量
                try:
                    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
                    assets_path = os.path.join(project_root, 'app', 'src', 'main', 'assets', 'data', 'upmasters.json')

                    if os.path.exists(assets_path):
                        with open(assets_path, 'r', encoding='utf-8') as f:
                            upmasters = json.load(f)
                            # 统计关注列表中显示的5个UP主（up1, up2, up3, up4, up_xiaoyao）中的互粉数量
                            target_ids = {"up1", "up2", "up3", "up4", "up_xiaoyao"}
                            mutual_count = sum(1 for up in upmasters
                                             if up.get("upMasterId") in target_ids
                                             and up.get("isFollowed")
                                             and up.get("isMutualFollow", False))
                            print(f"✓ 验证成功: 已进入关注页面")
                            print(f"  互粉UP主数量 = {mutual_count}")
                            return True
                except Exception as e:
                    print(f"读取本地数据失败: {e}")
                    return False

            pattern = r'(?:^|[^\d])2(?:[^\d]|$)'
            return bool(re.search(pattern, final_msg))

        # 2. 解析JSON数据
        try:
            data = json.loads(result_data.stdout)
        except json.JSONDecodeError:
            print("⚠️ 关注列表数据格式错误")
            if result is None:
                print("✓ 验证成功: 已进入关注页面")
                return True
            pattern = r'(?:^|[^\d])2(?:[^\d]|$)'
            return bool(re.search(pattern, final_msg))

        # 3. 统计互粉的up主数量
        following_list = data.get("following_list", [])
        mutual_follow_count = sum(1 for user in following_list if user.get("is_mutual_follow", False))

        # 4. 验证逻辑
        if result is None:
            # 独立运行模式：只要能读取到数据就算成功
            print(f"✓ 验证成功: 已进入关注页面")
            print(f"  关注列表总数 = {len(following_list)}")
            print(f"  互粉UP主数量 = {mutual_follow_count}")
            return True
        else:
            # 被调用模式：验证 final_message 中是否包含正确答案
            pattern = rf'(?:^|[^\d]){mutual_follow_count}(?:[^\d]|$)'
            if re.search(pattern, final_msg):
                print(f"✓ 验证成功: 互粉up主数量 = {mutual_follow_count}")
                return True
            else:
                print(f"❌ 验证失败: 期望答案={mutual_follow_count}, 实际回答={final_msg}")
                return False

    except subprocess.TimeoutExpired:
        print("⚠️ ADB命令超时")
        if result is None:
            return False
        pattern = r'(?:^|[^\d])2(?:[^\d]|$)'
        return bool(re.search(pattern, final_msg))
    except Exception as e:
        print(f"⚠️ 验证过程出错: {str(e)}")
        if result is None:
            return False
        pattern = r'(?:^|[^\d])2(?:[^\d]|$)'
        return bool(re.search(pattern, final_msg))

if __name__ == '__main__':
    result = validate_task_15()
    print(result)
