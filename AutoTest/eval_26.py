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

def CheckVipExpireDate():
    """
    检验逻辑:在大会员页面，查看大会员有效期
    验证用户是否在APP中真正查看了大会员有效期
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
        print("3. 点击大会员入口")
        print("4. 查看大会员有效期")
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

        # step3. 验证是否进入大会员页面
        if 'VIP_PAGE_ENTERED' not in log_content:
            print("验证失败: 未检测到进入大会员页面")
            return False

        # step4. 验证是否成功加载大会员数据
        if 'VIP_DATA_LOADED' not in log_content:
            print("验证失败: 未检测到大会员数据加载")
            return False

        # step5. 验证是否显示有效期
        if 'VIP_EXPIRE_DATE_DISPLAYED' not in log_content:
            print("验证失败: 未检测到有效期显示")
            return False

        # step6. 提取并验证有效期格式(YYYY-MM-DD)
        date_pattern = r'VIP_EXPIRE_DATE_DISPLAYED:\s*(\d{4}-\d{2}-\d{2})'
        date_match = re.search(date_pattern, log_content)

        if not date_match:
            print("验证失败: 无法提取有效期或格式不正确")
            return False

        expire_date = date_match.group(1)

        # 验证日期格式
        if not re.match(r'^\d{4}-\d{2}-\d{2}$', expire_date):
            print("验证失败: 有效期格式不正确")
            return False

        # step7. 验证有效期与assets数据是否一致
        project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        assets_path = os.path.join(project_root, 'app', 'src', 'main', 'assets', 'data', 'user_profile.json')

        if os.path.exists(assets_path):
            with open(assets_path, 'r', encoding='utf-8') as f:
                user_data = json.load(f)
                expected_date = user_data.get('vip_expire_date', '')

                if expire_date == expected_date:
                    print(f"有效期验证通过: {expire_date}")
                else:
                    print(f"警告: 有效期不匹配 (期望:{expected_date}, 实际:{expire_date})")

        print("查看大会员有效期验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查大会员有效期时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckVipExpireDate()
    print(result)
