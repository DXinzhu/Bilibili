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

def CheckWatchHistory():
    """
    检验逻辑:查看观看历史
    验证用户是否在APP中真正查看了观看历史
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
        print("3. 点击'历史记录'入口")
        print("4. 查看观看历史")
        print("=" * 60)

        # 等待用户操作
        input("\n完成上述操作后，按回车键继续验证...")

        # step2. 读取logcat日志,查找特定的日志标记
        print("\n正在检查日志...")
        result = subprocess.run(
            [adb_cmd, 'logcat', '-d', '-s', 'BilibiliAutoTest:D'],
            capture_output=True,
            text=True,
            timeout=10
        )

        log_content = result.stdout

        # step3. 验证是否包含历史记录页面访问的日志
        if 'HISTORY_TAB_VIEWED' not in log_content:
            print("❌ 验证失败: 未检测到进入历史记录页面")
            print("\n可能的原因:")
            print("1. 您没有点击进入历史记录页面")
            print("2. APP未正确安装或需要重新编译")
            print("\n日志内容:")
            print(log_content if log_content else "(无相关日志)")
            return False

        # step4. 验证是否成功加载了历史记录数据
        if 'HISTORY_DATA_LOADED' not in log_content:
            print("❌ 验证失败: 历史记录数据未加载")
            print("\n日志内容:")
            print(log_content)
            return False

        # 提取加载的历史记录数量
        history_count = 0
        for line in log_content.split('\n'):
            if 'HISTORY_DATA_LOADED' in line:
                try:
                    history_count = int(line.split(':')[-1].strip())
                except:
                    pass

        print("✓ 检测到进入历史记录页面")
        print(f"✓ 成功加载历史记录数据 (共{history_count}条)")

        # step5. 额外验证：从assets读取预期的历史记录数量，对比是否一致
        project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        assets_path = os.path.join(project_root, 'app', 'src', 'main', 'assets', 'data', 'watch_history.json')

        if os.path.exists(assets_path):
            with open(assets_path, 'r', encoding='utf-8') as f:
                expected_data = json.load(f)
                expected_count = len(expected_data) if isinstance(expected_data, list) else 0

                if history_count == expected_count:
                    print(f"✓ 历史记录数量验证通过 (期望:{expected_count}, 实际:{history_count})")
                else:
                    print(f"⚠ 历史记录数量不匹配 (期望:{expected_count}, 实际:{history_count})")
                    print("  这可能是因为历史记录被删除或修改")

        print("\n" + "=" * 60)
        print("观看历史验证成功!")
        print("=" * 60)
        return True

    except subprocess.TimeoutExpired:
        print("❌ 验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"❌ 检查观看历史时发生错误: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    result = CheckWatchHistory()
    print(f"\n{'='*60}")
    print(f"最终检验结果: {'✓ 通过' if result else '✗ 失败'}")
    print(f"{'='*60}")
