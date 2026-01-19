#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Bilibili 自动测试任务运行器
监控虚拟机操作，生成 result 字典，调用 tasks 下的验证函数
"""

import subprocess
import json
import os
import sys
import time
import importlib.util
from datetime import datetime

# 添加 tasks 目录到 Python 路径
TASKS_DIR = os.path.join(os.path.dirname(__file__), 'tasks')
sys.path.insert(0, TASKS_DIR)

class TaskRunner:
    """任务运行器"""

    def __init__(self, device_id=None):
        self.device_id = device_id
        self.backup_dir = os.path.join(os.path.dirname(__file__), 'test_results')

        # 创建备份目录
        if not os.path.exists(self.backup_dir):
            os.makedirs(self.backup_dir)

    def find_adb(self):
        """查找 ADB 命令路径"""
        import shutil
        adb_path = shutil.which('adb')
        if adb_path:
            return adb_path

        # Windows 常见路径
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

        return 'adb'

    def clear_logcat(self):
        """清除 logcat 日志"""
        adb_cmd = self.find_adb()
        cmd = [adb_cmd]
        if self.device_id:
            cmd.extend(['-s', self.device_id])
        cmd.extend(['logcat', '-c'])

        try:
            subprocess.run(cmd, timeout=5, capture_output=True)
            print("已清除 logcat 日志")
        except Exception as e:
            print(f"清除日志失败: {e}")

    def read_logcat(self):
        """读取 logcat 日志"""
        adb_cmd = self.find_adb()
        cmd = [adb_cmd]
        if self.device_id:
            cmd.extend(['-s', self.device_id])
        cmd.extend(['logcat', '-d', '-s', 'BilibiliAutoTest:D'])

        try:
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=10,
                encoding='utf-8',
                errors='ignore'
            )
            return result.stdout
        except Exception as e:
            print(f"读取日志失败: {e}")
            return ""

    def generate_result_from_logs(self, log_content, task_number=None):
        """
        从日志内容生成 result 字典
        这个函数会分析日志，提取用户操作信息，生成 final_message
        """
        result = {
            "final_message": "",
            "log_content": log_content,
            "timestamp": datetime.now().isoformat()
        }

        # 导入日志解析器
        from log_parser import LogParser

        # 如果指定了任务编号，使用对应的解析器
        if task_number:
            parser_method = f"parse_task_{task_number}"
            if hasattr(LogParser, parser_method):
                parse_func = getattr(LogParser, parser_method)
                parsed_message = parse_func(log_content)
                if parsed_message:
                    result["final_message"] = parsed_message
                    return result

        # 如果没有找到特定解析器或解析失败，使用通用解析逻辑
        import re

        # 提取数字信息（如点赞数、评论数等）
        numbers = re.findall(r'\d+', log_content)
        if numbers:
            # 如果找到数字，将第一个数字作为答案
            result["final_message"] = numbers[0]

        # 提取日期信息（如会员到期日期）
        date_pattern = r'\d{4}[-/年]\d{1,2}[-/月]\d{1,2}[日]?'
        dates = re.findall(date_pattern, log_content)
        if dates:
            result["final_message"] = f"{dates[0]}到期"

        # 提取关键词（如"已关闭"、"到期"等）
        keywords = ['已关闭', '已开启', '到期', '天', '个月']
        for keyword in keywords:
            if keyword in log_content:
                result["final_message"] = keyword
                break

        return result

    def load_task_module(self, task_number):
        """动态加载任务模块"""
        module_name = f"eval_{task_number}"
        module_path = os.path.join(TASKS_DIR, f"{module_name}.py")

        if not os.path.exists(module_path):
            print(f"任务文件不存在: {module_path}")
            return None

        try:
            spec = importlib.util.spec_from_file_location(module_name, module_path)
            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module)
            return module
        except Exception as e:
            print(f"加载任务模块失败: {e}")
            return None

    def run_task(self, task_number, result):
        """运行指定的任务验证"""
        print(f"\n{'='*60}")
        print(f"Running Task {task_number}")
        print(f"{'='*60}")

        # 加载任务模块
        module = self.load_task_module(task_number)
        if module is None:
            return False

        # 获取验证函数
        validate_func_name = f"validate_task_{task_number}"
        if not hasattr(module, validate_func_name):
            print(f"Function not found in task module: {validate_func_name}")
            return False

        validate_func = getattr(module, validate_func_name)

        # 调用验证函数
        try:
            # 临时替换 print 函数来避免编码错误
            import builtins
            original_print = builtins.print

            def safe_print(*args, **kwargs):
                """安全的 print 函数，忽略编码错误"""
                try:
                    original_print(*args, **kwargs)
                except UnicodeEncodeError:
                    # 尝试用 ASCII 编码
                    try:
                        safe_args = [str(arg).encode('ascii', errors='ignore').decode('ascii') for arg in args]
                        original_print(*safe_args, **kwargs)
                    except:
                        pass  # 完全忽略无法打印的内容

            # 替换 print 函数
            builtins.print = safe_print

            try:
                validation_result = validate_func(
                    result=result,
                    device_id=self.device_id,
                    backup_dir=self.backup_dir
                )
            finally:
                # 恢复原始 print 函数
                builtins.print = original_print

            return validation_result

        except Exception as e:
            print(f"Error running task validation: {str(e)}")
            return False


    def run_task_interactive(self, task_number):
        """交互式运行任务"""
        print(f"\n{'='*60}")
        print(f"准备运行任务 {task_number}")
        print(f"{'='*60}\n")

        # 清除旧日志
        self.clear_logcat()

        # 提示用户操作
        print("请在虚拟机中完成以下操作:")
        print(f"  - 执行任务 {task_number} 的相关操作")
        print(f"  - 完成后按回车键继续验证...\n")

        input("按回车键继续...")

        # 读取日志
        print("\n正在读取日志...")
        log_content = self.read_logcat()

        # 生成 result 字典（传入任务编号）
        result = self.generate_result_from_logs(log_content, task_number)

        # 运行任务验证
        validation_result = self.run_task(task_number, result)

        # 输出结果
        print(f"\n{'='*60}")
        if validation_result:
            print(f"任务 {task_number} 验证通过: True")
        else:
            print(f"任务 {task_number} 验证失败: False")
        print(f"{'='*60}\n")

        return validation_result


def main():
    """主函数"""
    import argparse

    parser = argparse.ArgumentParser(description='Bilibili 自动测试任务运行器')
    parser.add_argument('task_number', type=int, help='任务编号 (例如: 17)')
    parser.add_argument('--device', '-d', help='设备 ID (可选)')
    parser.add_argument('--message', '-m', help='直接提供 final_message (跳过交互式输入)')

    args = parser.parse_args()

    # 创建任务运行器
    runner = TaskRunner(device_id=args.device)

    if args.message:
        # 直接使用提供的 message
        result = {
            "final_message": args.message,
            "timestamp": datetime.now().isoformat()
        }
        validation_result = runner.run_task(args.task_number, result)
    else:
        # 交互式运行
        validation_result = runner.run_task_interactive(args.task_number)

    # 返回退出码
    sys.exit(0 if validation_result else 1)


if __name__ == '__main__':
    main()
