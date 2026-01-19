import subprocess
import json
import os
import re

def validate_task_17(result=None, device_id=None, backup_dir=None):
    """
    任务17: 看第一个视频，不算我点的赞，看看评论区所有赞加起来有多少
    改进: 从APP数据源获取评论数据并计算点赞总数
    """
    if result is None:
        print("result is None")
        return False

    final_msg = result.get("final_message", "")
    print(f"final_message: {final_msg}")

    try:
        # 1. 使用 ADB 从设备拉取评论数据
        cmd = ["adb"]
        if device_id:
            cmd.extend(["-s", device_id])
        cmd.extend(["exec-out", "run-as", "com.example.bilibili",
                   "cat", "files/video_comments.json"])

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
            comments_file_path = os.path.join(backup_dir, 'video_comments.json')
            with open(comments_file_path, 'w', encoding='utf-8') as f:
                f.write(result_data.stdout)

        # 检查命令是否成功执行
        if result_data.returncode != 0 or not result_data.stdout:
            print("Cannot read comment data, fallback validation")
            pattern = r'(?<!\d)378(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 2. 解析JSON数据
        try:
            data = json.loads(result_data.stdout)
        except json.JSONDecodeError:
            print("JSON decode error, fallback validation")
            pattern = r'(?<!\d)378(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 3. 计算评论区所有点赞数的总和
        comments_list = data.get("comments", [])

        # 统计所有评论的点赞总数
        total_likes = sum(
            comment.get("like_count", 0)
            for comment in comments_list
        )

        print(f"Comment count: {len(comments_list)}")
        print(f"Total likes: {total_likes}")

        # 4. 验证 final_message 中是否包含正确答案
        # 使用正则表达式匹配独立的数字（支持中文环境）
        # 匹配前后不是数字的情况
        pattern = rf'(?<!\d){total_likes}(?!\d)'
        if re.search(pattern, final_msg):
            print(f"Validation SUCCESS: Total likes = {total_likes}")
            return True
        else:
            print(f"Validation FAILED: Expected={total_likes}, Actual={final_msg}")
            return False

    except subprocess.TimeoutExpired:
        print("ADB timeout, fallback validation")
        pattern = r'(?<!\d)378(?!\d)'
        return bool(re.search(pattern, final_msg))
    except Exception as e:
        print(f"Validation error: {str(e)}, fallback validation")
        pattern = r'(?<!\d)378(?!\d)'
        return bool(re.search(pattern, final_msg))

if __name__ == '__main__':
    result = validate_task_17()
    print(result)

