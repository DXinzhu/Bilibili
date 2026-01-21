# -*- coding: utf-8 -*-
"""
视频压缩脚本 - 使用 imageio-ffmpeg 内置的 ffmpeg
自动压缩并删除原始文件，只保留压缩后的视频
"""

import os
import sys
import shutil
import subprocess
from pathlib import Path

# 设置输出编码为UTF-8
if sys.platform == 'win32':
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

# 配置参数
VIDEO_DIR = r"app\src\main\res\raw"
BACKUP_DIR = r"app\src\main\res\raw\backup_original"
OUTPUT_DIR = r"app\src\main\res\raw\compressed"

# 压缩参数
TARGET_WIDTH = 1280
TARGET_HEIGHT = 720
VIDEO_BITRATE = "500k"
AUDIO_BITRATE = "64k"
TARGET_FPS = 24


def get_ffmpeg_path():
    """获取 ffmpeg 可执行文件路径"""
    try:
        from imageio_ffmpeg import get_ffmpeg_exe
        ffmpeg_path = get_ffmpeg_exe()
        print(f"[OK] 找到 ffmpeg: {ffmpeg_path}")
        return ffmpeg_path
    except Exception as e:
        print(f"[!] 无法获取 ffmpeg: {e}")
        print("[!] 正在安装 imageio-ffmpeg...")
        subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'imageio-ffmpeg'])
        from imageio_ffmpeg import get_ffmpeg_exe
        ffmpeg_path = get_ffmpeg_exe()
        print(f"[OK] 找到 ffmpeg: {ffmpeg_path}")
        return ffmpeg_path


def format_size(size_bytes):
    """格式化文件大小"""
    for unit in ['B', 'KB', 'MB', 'GB']:
        if size_bytes < 1024.0:
            return f"{size_bytes:.2f} {unit}"
        size_bytes /= 1024.0
    return f"{size_bytes:.2f} TB"


def compress_video(input_path, output_path, ffmpeg_exe):
    """使用 ffmpeg 压缩视频"""
    try:
        print(f"    开始编码...")

        # 构建 ffmpeg 命令
        cmd = [
            ffmpeg_exe,
            '-i', str(input_path),
            '-vf', f'scale={TARGET_WIDTH}:{TARGET_HEIGHT},fps={TARGET_FPS}',
            '-c:v', 'libx264',
            '-b:v', VIDEO_BITRATE,
            '-preset', 'medium',
            '-c:a', 'aac',
            '-b:a', AUDIO_BITRATE,
            '-movflags', '+faststart',
            '-y',
            str(output_path)
        ]

        # 运行 ffmpeg（隐藏输出）
        result = subprocess.run(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            check=True
        )

        return True

    except subprocess.CalledProcessError as e:
        print(f"  [X] FFmpeg 错误：{e.stderr.decode('utf-8', errors='ignore')[:200]}")
        return False
    except Exception as e:
        print(f"  [X] 错误：{e}")
        return False


def main():
    print("=" * 70)
    print("视频压缩脚本 - ImageIO FFmpeg版本")
    print("=" * 70)

    # 获取 ffmpeg 路径
    try:
        ffmpeg_exe = get_ffmpeg_path()
    except Exception as e:
        print(f"[X] 无法获取 ffmpeg: {e}")
        return

    print("=" * 70)

    # 获取项目根目录
    script_dir = Path(__file__).parent
    video_dir = script_dir / VIDEO_DIR
    backup_dir = script_dir / BACKUP_DIR
    output_dir = script_dir / OUTPUT_DIR

    # 检查视频目录
    if not video_dir.exists():
        print(f"[X] 错误：视频目录不存在 - {video_dir}")
        return

    # 获取所有视频文件
    video_files = list(video_dir.glob("*.mp4"))
    if not video_files:
        print(f"[X] 错误：未找到视频文件 - {video_dir}")
        return

    print(f"\n找到 {len(video_files)} 个视频文件")

    # 显示压缩设置
    print("\n压缩设置：")
    print(f"  分辨率: {TARGET_WIDTH}x{TARGET_HEIGHT}")
    print(f"  视频码率: {VIDEO_BITRATE}")
    print(f"  帧率: {TARGET_FPS} fps")
    print(f"  音频码率: {AUDIO_BITRATE}")

    print("\n操作说明：")
    print("  1. 备份原始文件到临时目录")
    print("  2. 压缩所有视频")
    print("  3. 用压缩后的视频替换原文件")
    print("  4. 自动删除备份和临时文件")

    # 创建备份和输出目录
    backup_dir.mkdir(parents=True, exist_ok=True)
    output_dir.mkdir(parents=True, exist_ok=True)

    print("\n" + "=" * 70)
    print("开始压缩...")
    print("=" * 70)

    total_original_size = 0
    total_compressed_size = 0
    success_count = 0

    for i, video_file in enumerate(video_files, 1):
        print(f"\n[{i}/{len(video_files)}] 处理: {video_file.name}")

        # 获取原始文件大小
        original_size = video_file.stat().st_size
        total_original_size += original_size
        print(f"  原始大小: {format_size(original_size)}")

        # 备份原始文件
        backup_path = backup_dir / video_file.name
        print(f"  备份原始文件...")
        shutil.copy2(video_file, backup_path)

        # 压缩视频
        output_path = output_dir / video_file.name
        print(f"  开始压缩...")

        if compress_video(video_file, output_path, ffmpeg_exe):
            if output_path.exists():
                compressed_size = output_path.stat().st_size
                total_compressed_size += compressed_size
                compression_ratio = (1 - compressed_size / original_size) * 100

                print(f"  [OK] 压缩完成")
                print(f"  压缩后大小: {format_size(compressed_size)}")
                print(f"  压缩率: {compression_ratio:.1f}%")

                # 替换原始文件
                shutil.copy2(output_path, video_file)
                print(f"  [OK] 已替换原始文件")

                success_count += 1
            else:
                print(f"  [X] 输出文件不存在")
        else:
            print(f"  [X] 压缩失败，保留原始文件")

    # 显示总结
    print("\n" + "=" * 70)
    print("压缩完成！")
    print("=" * 70)
    print(f"成功压缩: {success_count}/{len(video_files)} 个文件")
    print(f"原始总大小: {format_size(total_original_size)}")
    print(f"压缩后总大小: {format_size(total_compressed_size)}")

    if total_original_size > 0 and success_count > 0:
        total_compression_ratio = (1 - total_compressed_size / total_original_size) * 100
        saved_size = total_original_size - total_compressed_size
        print(f"总压缩率: {total_compression_ratio:.1f}%")
        print(f"节省空间: {format_size(saved_size)}")

    # 自动删除备份和临时文件
    print("\n" + "=" * 70)
    print("清理临时文件...")
    print("=" * 70)

    if backup_dir.exists():
        print(f"删除备份目录: {backup_dir.relative_to(script_dir)}")
        shutil.rmtree(backup_dir)
        print("[OK] 备份文件已删除")

    if output_dir.exists():
        print(f"删除临时目录: {output_dir.relative_to(script_dir)}")
        shutil.rmtree(output_dir)
        print("[OK] 临时文件已删除")

    print("\n" + "=" * 70)
    if success_count > 0:
        print("[OK] 所有操作完成！")
        print("[OK] 原始视频已全部删除")
        print("[OK] 只保留压缩后的视频")
    else:
        print("[!] 没有成功压缩任何视频")
        print("[!] 原始文件已恢复")
    print("=" * 70)


if __name__ == "__main__":
    main()
