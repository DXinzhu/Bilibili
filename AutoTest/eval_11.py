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

def CheckUploaderFans():
    """
    检验逻辑:在UP主逍遥散人主页查看其粉丝数
    验证用户是否在逍遥散人主页查看粉丝数
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
        print("2. 进入UP主'逍遥散人'主页")
        print("3. 查看其粉丝数")
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

        # step3. 验证是否进入UP主主页
        if 'UPLOADER_PAGE_ENTERED' not in log_content:
            print("验证失败: 未检测到进入UP主主页")
            return False

        # step4. 验证UP主名称是否为逍遥散人
        if '逍遥散人' not in log_content:
            print("验证失败: 未检测到逍遥散人")
            return False

        # step5. 验证是否加载了UP主数据
        if 'UPLOADER_DATA_LOADED' not in log_content:
            print("验证失败: UP主数据未加载")
            return False

        # step6. 验证是否显示粉丝数
        if 'FANS_COUNT_DISPLAYED' not in log_content:
            print("验证失败: 粉丝数未显示")
            return False

        print("UP主粉丝数验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查UP主粉丝数时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckUploaderFans()
    print(result)
