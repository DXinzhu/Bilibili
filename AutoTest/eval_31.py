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

def CheckVipExpiry():
    """
    检验逻辑:查看大会员还有多久到期
    验证用户是否在APP中真正查看了大会员的到期时间
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
        print("2. 点击底部'我的'页面")
        print("3. 进入大会员页面")
        print("4. 查看大会员到期时间")
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
            errors='ignore'
        )

        log_content = result.stdout

        # step3. 验证是否进入大会员页面
        if 'VIP_PAGE_ENTERED' not in log_content:
            print("❌ 验证失败: 未检测到进入大会员页面")
            print("\n可能的原因:")
            print("1. 您没有点击进入大会员页面")
            print("2. APP未正确安装或需要重新编译")
            print("\n日志内容:")
            print(log_content if log_content else "(无相关日志)")
            return False

        # step4. 验证是否显示了到期时间
        if 'VIP_EXPIRE_DATE_DISPLAYED' not in log_content and 'MEMBERSHIP_INFO' not in log_content:
            print("❌ 验证失败: 未检测到大会员到期时间显示")
            print("\n日志内容:")
            print(log_content)
            return False

        print("✓ 检测到进入大会员页面")
        print("✓ 检测到大会员到期时间显示")

        print("\n" + "=" * 60)
        print("大会员到期时间查询验证成功!")
        print("=" * 60)
        return True

    except subprocess.TimeoutExpired:
        print("❌ 验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"❌ 检查大会员到期时间时发生错误: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    result = CheckVipExpiry()
    print(f"\n{'='*60}")
    print(f"最终检验结果: {'✓ 通过' if result else '✗ 失败'}")
    print(f"{'='*60}")
