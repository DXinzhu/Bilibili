import subprocess
import json
import os
import re

def validate_task_21(result=None, device_id=None, backup_dir=None):
    """
    任务21: 看第一个视频，查看相关视频有几个
    改进: 从APP数据源获取相关视频数量，而非hardcode
    """
    # 验证 result 存在
    if result is None:
        return False

    final_msg = result.get("final_message", "")

    try:
        # 1. 使用 ADB 从设备拉取相关视频数据
        cmd = ["adb"]
        if device_id:
            cmd.extend(["-s", device_id])
        cmd.extend(["exec-out", "run-as", "com.example.bilibili",
                   "cat", "files/related_videos.json"])

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
            related_file_path = os.path.join(backup_dir, 'related_videos.json')
            with open(related_file_path, 'w', encoding='utf-8') as f:
                f.write(result_data.stdout)

        # 检查命令是否成功执行
        if result_data.returncode != 0 or not result_data.stdout:
            print("Cannot read related videos data, fallback validation")
            pattern = r'(?<!\d)4(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 2. 解析JSON数据
        try:
            data = json.loads(result_data.stdout)
        except json.JSONDecodeError:
            print("JSON decode error, fallback validation")
            pattern = r'(?<!\d)4(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 3. 计算相关视频数量
        related_count = len(data.get("related_videos", []))

        # 4. 验证 final_message 中是否包含正确答案
        pattern = rf'(?<!\d){related_count}(?!\d)'
        if re.search(pattern, final_msg):
            print(f"Validation SUCCESS: Related videos count = {related_count}")
            return True
        else:
            print(f"Validation FAILED: Expected={related_count}, Actual={final_msg}")
            return False

    except subprocess.TimeoutExpired:
        print("ADB timeout, fallback validation")
        pattern = r'(?<!\d)4(?!\d)'
        return bool(re.search(pattern, final_msg))
    except Exception as e:
        print(f"Validation error: {str(e)}, fallback validation")
        pattern = r'(?<!\d)4(?!\d)'
        return bool(re.search(pattern, final_msg))

if __name__ == '__main__':
    result = validate_task_21()
    print(result)

