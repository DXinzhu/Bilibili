import subprocess
import json
import os
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

def CheckSmartFilterStatus():
    """
    检验逻辑:查看私信智能拦截的开启状态
    验证用户是否在APP中真正查看了消息设置页面
    """
    try:
        adb_cmd = find_adb()
        if not adb_cmd:
            print("错误: 找不到adb命令")
            print("请确保Android SDK已安装,或将platform-tools目录添加到系统PATH")
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
        print("3. 点击'消息设置'入口")
        print("4. 查看私信智能拦截的开启状态")
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

        # step3. 验证是否进入了消息设置页面
        if 'MESSAGE_SETTINGS_PAGE_ENTERED' not in log_content:
            print("❌ 验证失败: 未检测到进入消息设置页面")
            print("\n可能的原因:")
            print("1. 您没有点击进入消息设置页面")
            print("2. APP未正确安装或需要重新编译")
            print("\n日志内容:")
            print(log_content if log_content else "(无相关日志)")
            return False

        # step4. 验证是否加载了消息设置数据
        if 'MESSAGE_SETTINGS_DATA_LOADED' not in log_content:
            print("❌ 验证失败: 消息设置数据未加载")
            print("\n日志内容:")
            print(log_content)
            return False

        # step5. 验证是否查看了私信智能拦截状态
        if 'SMART_FILTER_STATUS_VIEWED' not in log_content:
            print("❌ 验证失败: 未检测到查看私信智能拦截状态")
            print("\n日志内容:")
            print(log_content)
            return False

        # 提取私信智能拦截的状态
        smart_filter_status = "未知"
        for line in log_content.split('\n'):
            if 'SMART_FILTER_STATUS_VIEWED' in line:
                if '开启' in line:
                    smart_filter_status = "开启"
                elif '关闭' in line:
                    smart_filter_status = "关闭"
                break

        print("✓ 检测到进入消息设置页面")
        print("✓ 成功加载消息设置数据")
        print(f"✓ 私信智能拦截状态: {smart_filter_status}")

        print("\n" + "=" * 60)
        print("私信智能拦截状态验证成功!")
        print("=" * 60)
        return True

    except subprocess.TimeoutExpired:
        print("❌ 验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"❌ 检查消息设置时发生错误: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    result = CheckSmartFilterStatus()
    print(f"\n{'='*60}")
    print(f"最终检验结果: {'✓ 通过' if result else '✗ 失败'}")
    print(f"{'='*60}")
