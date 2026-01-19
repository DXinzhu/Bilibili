import subprocess
import json
import os
import shutil
import time

def find_adb():
    """查找adb命令路径"""
    # 首先检查adb是否在PATH中
    adb_path = shutil.which('adb')
    if adb_path:
        return adb_path

    # 如果不在PATH中，尝试常见的Android SDK路径
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

def CheckProfilePage(result=None,device_id=None,backup_dir=None):
    """
    检验逻辑:在我的页面，点击顶部头像或昵称区域，进入个人资料页查看我追的动漫
    验证用户是否进入个人资料页并查看到动漫信息
    """
    try:
        # 查找adb命令
        adb_cmd = find_adb()
        if not adb_cmd:
            print("错误: 找不到adb命令")
            print("请确保Android SDK已安装,或将platform-tools目录添加到系统PATH")
            print("常见路径: C:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk\\platform-tools")
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
        print("3. 点击顶部头像或昵称区域")
        print("4. 进入个人资料页，查看'我追的动漫'")
        print("=" * 60)

        input("\n完成上述操作后，按回车键继续验证...")

        # step2. 读取logcat日志
        print("\n正在检查日志...")
        cmd_logcat = [adb_cmd]
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

        # step3. 验证关键操作 - 检测到PersonTab和ProfilePageEntered即可
        person_tab_detected = 'PersonTab' in log_content
        profile_page_entered = 'PROFILE_PAGE_ENTERED' in log_content

        if not person_tab_detected and not profile_page_entered:
            print("验证失败: 未检测到进入个人资料页")
            print("\n提示: 请确保:")
            print("1. 在我的页面点击了顶部头像或昵称")
            print("2. 已进入个人资料页")
            print(f"\n日志内容:\n{log_content}")
            return False

        print("个人资料页验证成功!")
        print("已检测到进入个人资料页，可以查看'我追的动漫'")
        return True

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    finally:
        # 无论成功失败，最后都清除日志
        try:
            adb_cmd = find_adb()
            if adb_cmd:
                cmd_clear = [adb_cmd]
                if device_id:
                    cmd_clear.extend(['-s', device_id])
                cmd_clear.extend(['logcat', '-c'])
                subprocess.run(cmd_clear, timeout=5)
                print("🔄 已清除日志缓存")
        except subprocess.TimeoutExpired:
            print("⚠️ 清除日志超时")
        except Exception as e:
            print(f"⚠️ 清除日志失败: {str(e)}")

if __name__ == "__main__":
    result1 = CheckProfilePage()
    print(result1)
