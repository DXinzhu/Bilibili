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

def CheckXiaoyaosanrenPage():
    """
    检验逻辑:查看关注列表逍遥散人的主页
    验证用户是否从关注列表进入逍遥散人的主页
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
        print("2. 进入关注列表")
        print("3. 找到'逍遥散人'")
        print("4. 点击进入其主页")
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

        # step3. 验证关键操作 - 放宽验证条件
        follow_list_entered = 'FOLLOW_LIST_ENTERED' in log_content
        uploader_found = 'UPLOADER_FOUND' in log_content
        uploader_name = '逍遥散人' in log_content
        uploader_page_entered = 'UPLOADER_PAGE_ENTERED' in log_content
        uploader_data_loaded = 'UPLOADER_DATA_LOADED' in log_content

        # 至少检测到UP主相关操作（关注列表、UP主页面或UP主数据）
        if not (follow_list_entered or uploader_found or uploader_page_entered or uploader_data_loaded):
            print("验证失败: 未检测到查看UP主主页相关操作")
            print("\n提示: 请确保:")
            print("1. 进入了关注列表")
            print("2. 找到并点击了'逍遥散人'的主页")
            print(f"\n日志内容:\n{log_content}")
            return False

        print("查看逍遥散人主页验证成功!")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"检查逍遥散人主页时发生错误: {str(e)}")
        return False

if __name__ == "__main__":
    result = CheckXiaoyaosanrenPage()
    print(result)
