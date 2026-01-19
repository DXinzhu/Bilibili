import sys
sys.path.insert(0, r'D:\AndroidStudioProject\Bilibili-main\docs\AutoTest')

from eval_17 import validate_task_17

# 模拟不同的 result 参数来测试
test_cases = [
    {"final_message": "378"},
    {"final_message": "评论区一共有378个赞"},
    {"final_message": "总共378个"},
    {"final_message": "378个赞"},
    {"final_message": "一共3780个"},  # 应该失败
    {"final_message": "1378"},  # 应该失败
]

print("Testing eval_17.py with different inputs:\n")
for i, test_case in enumerate(test_cases, 1):
    print(f"Test {i}: final_message = '{test_case['final_message']}'")
    result = validate_task_17(result=test_case)
    print(f"Result: {result}")
    print("-" * 60)
