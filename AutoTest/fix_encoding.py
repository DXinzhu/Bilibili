import os
import re

# 修复所有eval文件的编码问题
for i in range(6, 31):
    filename = f'eval_{i}.py'
    if not os.path.exists(filename):
        continue

    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read()

    # 检查是否已经有encoding参数
    if 'encoding=' in content:
        print(f'{filename}: 已有编码处理，跳过')
        continue

    # 替换 subprocess.run 的调用，添加编码参数
    pattern = r'(result = subprocess\.run\(\s*\[adb_cmd, \'logcat\', \'-d\', \'-s\', \'BilibiliAutoTest:D\'\],\s*capture_output=True,\s*text=True,\s*timeout=10)\s*\)'
    replacement = r'\1,\n            encoding=\'utf-8\',\n            errors=\'ignore\'  # 忽略无法解码的字符\n        )'
    content = re.sub(pattern, replacement, content)

    # 在每个验证失败后添加日志输出
    content = re.sub(
        r'(print\(\"验证失败: [^\"]+\"\))\n(\s+return False)',
        r'\1\n            print(f\"日志内容:\\n{log_content}\")\n\2',
        content
    )

    with open(filename, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f'{filename}: 修复完成')

print("所有文件处理完成！")
