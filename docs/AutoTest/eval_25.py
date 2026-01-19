import subprocess
import json
import os
import re

def validate_task_25(result=None, device_id=None, backup_dir=None):
    """
    任务25: 去设置里找一下uid是多少
    改进: 从APP配置中读取实际UID
    """
    if result is None:
        return False

    final_msg = result.get("final_message", "")

    try:
        # 1. 使用 ADB 从设备拉取用户配置
        cmd = ["adb"]
        if device_id:
            cmd.extend(["-s", device_id])
        cmd.extend(["exec-out", "run-as", "com.example.bilibili",
                   "cat", "files/user_profile.json"])

        result_data = subprocess.run(
            cmd,
            capture_output=True,
            encoding='utf-8',
            errors='replace',
            text=True,
            timeout=10
        )

        # 保存数据到备份目录
        if backup_dir:
            profile_file_path = os.path.join(backup_dir, 'user_profile.json')
            with open(profile_file_path, 'w', encoding='utf-8') as f:
                f.write(result_data.stdout)

        # 检查命令是否成功执行
        if result_data.returncode != 0 or not result_data.stdout:
            print("Cannot read user profile data, fallback validation")
            pattern = r'(?<!\d)1668348161(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 2. 解析JSON数据
        try:
            data = json.loads(result_data.stdout)
        except json.JSONDecodeError:
            print("JSON decode error, fallback validation")
            pattern = r'(?<!\d)1668348161(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 3. 获取UID
        uid = str(data.get("uid", ""))

        # 4. 验证 final_message 中是否包含正确答案
        if uid:
            pattern = rf'(?<!\d){uid}(?!\d)'
            if re.search(pattern, final_msg):
                print(f"Validation SUCCESS: UID = {uid}")
                return True
            else:
                print(f"Validation FAILED: Expected UID={uid}, Actual={final_msg}")
                return False
        else:
            print(f"Validation FAILED: Cannot get UID")
            return False

    except subprocess.TimeoutExpired:
        print("ADB timeout, fallback validation")
        pattern = r'(?<!\d)1668348161(?!\d)'
        return bool(re.search(pattern, final_msg))
    except Exception as e:
        print(f"Validation error: {str(e)}, fallback validation")
        pattern = r'(?<!\d)1668348161(?!\d)'
        return bool(re.search(pattern, final_msg))

if __name__ == '__main__':
    result = validate_task_25()
    print(result)

