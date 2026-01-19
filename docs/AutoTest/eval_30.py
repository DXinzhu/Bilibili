import subprocess
import json
import os
import re

def validate_task_30(result=None, device_id=None, backup_dir=None):
    """
    任务30: 看一下会员购里的商品一共卖了多少件
    改进: 从APP数据源获取商品数据并统计总销量
    """
    if result is None:
        return False

    final_msg = result.get("final_message", "")

    try:
        # 1. 使用 ADB 从设备拉取会员购商品数据
        cmd = ["adb"]
        if device_id:
            cmd.extend(["-s", device_id])
        cmd.extend(["exec-out", "run-as", "com.example.bilibili",
                   "cat", "files/shop_products.json"])

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
            shop_file_path = os.path.join(backup_dir, 'shop_products.json')
            with open(shop_file_path, 'w', encoding='utf-8') as f:
                f.write(result_data.stdout)

        # 检查命令是否成功执行
        if result_data.returncode != 0 or not result_data.stdout:
            print("Cannot read shop products data, fallback validation")
            pattern = r'(?<!\d)4963(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 2. 解析JSON数据
        try:
            data = json.loads(result_data.stdout)
        except json.JSONDecodeError:
            print("JSON decode error, fallback validation")
            pattern = r'(?<!\d)4963(?!\d)'
            return bool(re.search(pattern, final_msg))

        # 3. 统计所有商品的总销量
        products_list = data.get("products", [])
        total_sales = sum(product.get("sales_count", 0) for product in products_list)

        # 4. 验证 final_message 中是否包含正确答案
        pattern = rf'(?<!\d){total_sales}(?!\d)'
        if re.search(pattern, final_msg):
            print(f"Validation SUCCESS: Total sales = {total_sales}")
            return True
        else:
            print(f"Validation FAILED: Expected={total_sales}, Actual={final_msg}")
            return False

    except subprocess.TimeoutExpired:
        print("ADB timeout, fallback validation")
        pattern = r'(?<!\d)4963(?!\d)'
        return bool(re.search(pattern, final_msg))
    except Exception as e:
        print(f"Validation error: {str(e)}, fallback validation")
        pattern = r'(?<!\d)4963(?!\d)'
        return bool(re.search(pattern, final_msg))

if __name__ == '__main__':
    result = validate_task_30()
    print(result)

