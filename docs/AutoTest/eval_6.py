import subprocess
import json
import os
import shutil
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

def CheckVipShopTotalPrice(result=None, device_id=None, backup_dir=None):
    """
    检验逻辑:在会员购页面，查看所有商品的总价
    验证用户是否进入会员购页面并查看了商品信息
    """
    try:
        adb_path = find_adb()
        if not adb_path:
            print("错误: 找不到 adb 命令")
            return False

        print("\n正在检查日志...")
        cmd_logcat = [adb_path]
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

        # step1. 验证是否进入会员购页面
        if 'VIP_SHOP_PAGE_ENTERED' not in log_content:
            print("验证失败: 未检测到进入会员购页面")
            print(f"日志内容:\n{log_content}")
            return False

        # step2. 验证是否加载了商品数据
        if 'VIP_SHOP_DATA_LOADED' not in log_content:
            print("验证失败: 未检测到商品数据加载")
            print(f"日志内容:\n{log_content}")
            return False

        # step3. 提取总价并验证
        # 日志格式: VIP_SHOP_DATA_LOADED: count=4, totalPrice=269.6
        match = re.search(r'VIP_SHOP_DATA_LOADED:.*totalPrice=([\d.]+)', log_content)
        if not match:
            print("验证失败: 无法从日志中提取总价")
            print(f"日志内容:\n{log_content}")
            return False

        total_price = float(match.group(1))
        expected_price = 269.6

        if abs(total_price - expected_price) < 0.01:
            print(f"✓ 验证成功: 会员购商品总价 = {total_price} 元")
            return True
        else:
            print(f"❌ 验证失败: 期望总价={expected_price}, 实际总价={total_price}")
            return False

    except subprocess.TimeoutExpired:
        print("验证失败: 读取日志超时")
        return False
    except Exception as e:
        print(f"验证失败: {str(e)}")
        return False
    finally:
        # 无论成功失败，最后都清除日志
        try:
            adb_path = find_adb()
            if adb_path:
                cmd_clear = [adb_path]
                if device_id:
                    cmd_clear.extend(['-s', device_id])
                cmd_clear.extend(['logcat', '-c'])
                subprocess.run(cmd_clear, timeout=5)
                print("🔄 已清除日志缓存")
            else:
                print("⚠️ 找不到 adb，无法清除日志")
        except subprocess.TimeoutExpired:
            print("⚠️ 清除日志超时")
        except Exception as e:
            print(f"⚠️ 清除日志失败: {str(e)}")

if __name__ == '__main__':
    result = CheckVipShopTotalPrice()
    print(result)
