#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
简化版任务运行器 - 直接提供 final_message 进行验证
不依赖 ADB，直接测试 tasks 文件的回退验证逻辑
"""

import sys
import os

# 添加 tasks 目录到 Python 路径
TASKS_DIR = os.path.join(os.path.dirname(__file__), 'tasks')
sys.path.insert(0, TASKS_DIR)

def run_simple_test(task_number, final_message):
    """简单测试：直接提供 final_message"""
    print(f"\n{'='*60}")
    print(f"Testing Task {task_number}")
    print(f"Final Message: {final_message}")
    print(f"{'='*60}\n")

    # 动态导入模块
    try:
        module_name = f"eval_{task_number}"
        module = __import__(module_name)
        validate_func = getattr(module, f"validate_task_{task_number}")
    except Exception as e:
        print(f"Error loading module: {e}")
        return False

    # 创建 result 字典
    result = {"final_message": final_message}

    # 替换 print 函数来避免编码错误
    import builtins
    original_print = builtins.print

    def safe_print(*args, **kwargs):
        try:
            original_print(*args, **kwargs)
        except UnicodeEncodeError:
            try:
                safe_args = [str(arg).encode('ascii', errors='ignore').decode('ascii') for arg in args]
                original_print(*safe_args, **kwargs)
            except:
                pass

    builtins.print = safe_print

    try:
        # 调用验证函数
        validation_result = validate_func(result=result)
        return validation_result
    except Exception as e:
        print(f"Error: {str(e)}")
        return False
    finally:
        builtins.print = original_print

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print("Usage: python simple_test.py <task_number> <final_message>")
        print("Example: python simple_test.py 17 378")
        sys.exit(1)

    task_num = int(sys.argv[1])
    message = sys.argv[2]

    result = run_simple_test(task_num, message)

    print(f"\n{'='*60}")
    print(f"Result: {result}")
    print(f"{'='*60}")

    sys.exit(0 if result else 1)
