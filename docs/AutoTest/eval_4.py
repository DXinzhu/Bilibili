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

def CheckLuoxiangVideoStats():
    """
    检验逻辑:查看首页罗翔老师的视频点赞加投币一共多少
    验证用户是否在APP中查看了罗翔老师视频的统计数据
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
        print("2. 在首页找到罗翔老师的视频（第一个视频）")
        print("3. 点击进入视频播放页面")
        print("4. 查看页面下方的点赞数和投币数")
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

        # step3. 验证是否打开了视频播放器
        if 'VIDEO_PLAYER_OPENED' not in log_content:
            print("❌ 验证失败: 未检测到打开视频")
            print("\n可能的原因:")
            print("1. 您没有点击视频")
            print("2. APP未正确安装或需要重新编译")
            print("\n日志内容:")
            print(log_content if log_content else "(无相关日志)")
            return False

        # step4. 验证是否显示了视频统计数据
        if 'VIDEO_STATS_DISPLAYED' not in log_content:
            print("❌ 验证失败: 未检测到视频统计数据显示")
            print("\n日志内容:")
            print(log_content)
            return False

        # step5. 验证视频ID是否为罗翔老师的视频 (vid001)
        if 'videoId=vid001' not in log_content:
            print("❌ 验证失败: 您查看的不是罗翔老师的视频")
            print("提示: 请点击首页第一个视频（罗翔老师的视频）")
            print("\n日志内容:")
            print(log_content)
            return False

        # 从日志中提取点赞数、投币数和总计
        import re
        stats_match = re.search(r'VIDEO_STATS_DISPLAYED:.*likes=(\d+).*coins=(\d+).*total=(\d+)', log_content)

        if stats_match:
            likes = int(stats_match.group(1))
            coins = int(stats_match.group(2))
            total = int(stats_match.group(3))

            print("✓ 检测到打开罗翔老师的视频")
            print("✓ 成功显示视频统计数据")
            print(f"✓ 点赞数: {likes} ({likes/10000:.1f}万)")
            print(f"✓ 投币数: {coins} ({coins/10000:.1f}万)")
            print(f"✓ 总计: {total} ({total/10000:.1f}万)")
        else:
            print("⚠️ 无法解析统计数据")

        print("\n" + "=" * 60)
        print("罗翔老师视频数据查看验证成功!")
        print("=" * 60)
        return True

    except subprocess.TimeoutExpired:
        print("❌ 验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"❌ 检查视频数据时发生错误: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    result = CheckLuoxiangVideoStats()
    print(f"\n{'='*60}")
    print(f"最终检验结果: {'✓ 通过' if result else '✗ 失败'}")
    print(f"{'='*60}")
