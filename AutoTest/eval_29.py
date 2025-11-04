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

def CheckTimerShutdownStatus():
    """
    检验逻辑:在设置中，查看当前定时关闭是否开启
    验证用户是否在APP中真正查看了定时关闭的状态
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
        print("3. 点击'设置'")
        print("4. 找到'定时关闭'选项")
        print("5. 查看当前状态(开启/关闭)")
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

        # step3. 验证是否进入设置页面
        if 'SETTINGS_PAGE_ENTERED' not in log_content:
            print("验证失败: 未检测到进入设置页面")
            return False

        # step4. 验证是否找到定时关闭选项
        if 'TIMER_SHUTDOWN_OPTION_FOUND' not in log_content:
            print("验证失败: 未检测到定时关闭选项")
            return False

        # step5. 验证是否点击查看定时关闭设置
        if 'TIMER_SHUTDOWN_CLICKED' not in log_content:
            print("验证失败: 未检测到点击定时关闭")
            return False

        # step6. 验证是否成功获取当前状态
        if 'TIMER_SHUTDOWN_STATUS_LOADED' not in log_content:
            print("验证失败: 未检测到加载定时关闭状态")
            return False

        # step7. 提取并验证状态值
        status = "未知"
        if '开启' in log_content or 'enabled' in log_content or 'on' in log_content:
            status = "开启"
        elif '关闭' in log_content or 'disabled' in log_content or 'off' in log_content:
            status = "关闭"

        print(f"定时关闭状态: {status}")
        print("查看定时关闭状态验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查定时关闭状态时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckTimerShutdownStatus()
    print(result)
