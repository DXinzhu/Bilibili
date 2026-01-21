#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
视频压缩脚本 - 自动化版本
自动压缩并删除原始文件，只保留压缩后的视频
"""

import os
import sys
import shutil
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
TARGET_RESOLUTION = (1280, 720)  # 720p
TARGET_BITRATE = "500k"
TARGET_FPS = 24


def format_size(size_bytes):
    """格式化文件大小"""
    for unit in ['B', 'KB', 'MB', 'GB']:
        if size_bytes < 1024.0:
            return f"{size_bytes:.2f} {unit}"
        size_bytes /= 1024.0
    return f"{size_bytes:.2f} TB"


def compress_video_moviepy(input_path, output_path):
    """使用 moviepy 压缩视频"""
    try:
        from moviepy.editor import VideoFileClip

        print(f"    加载视频...")
        # 加载视频
        clip = VideoFileClip(str(input_path))

        # 调整分辨率
        if clip.size[0] > TARGET_RESOLUTION[0] or clip.size[1] > TARGET_RESOLUTION[1]:
            print(f"    调整分辨率: {clip.size} -> {TARGET_RESOLUTION}")
            clip = clip.resize(height=TARGET_RESOLUTION[1])

        # 调整帧率
        if clip.fps > TARGET_FPS:
            print(f"    调整帧率: {clip.fps} -> {TARGET_FPS}")
            clip = clip.set_fps(TARGET_FPS)

        # 写入压缩后的视频
        print(f"    编码中...")
        clip.write_videofile(
            str(output_path),
            codec='libx264',
            bitrate=TARGET_BITRATE,
            audio_codec='aac',
            audio_bitrate='64k',
            preset='medium',
            threads=4,
            verbose=False,
            logger=None
        )

        clip.close()
        return True

    except Exception as e:
        print(f"  ✗ 错误：{e}")
        return False


def main():
    print("=" * 70)
    print("视频压缩脚本 - 自动化版本")
    print("=" * 70)

    # 检查依赖
    try:
        import moviepy
        print("✓ MoviePy 已安装")
    except ImportError:
        print("✗ MoviePy 未安装，正在安装...")
        import subprocess
        subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'moviepy', 'imageio-ffmpeg'])
        print("✓ MoviePy 安装完成")

    print("=" * 70)

    # 获取项目根目录
    script_dir = Path(__file__).parent
    video_dir = script_dir / VIDEO_DIR
    backup_dir = script_dir / BACKUP_DIR
    output_dir = script_dir / OUTPUT_DIR

    # 检查视频目录
    if not video_dir.exists():
        print(f"✗ 错误：视频目录不存在 - {video_dir}")
        return

    # 获取所有视频文件
    video_files = list(video_dir.glob("*.mp4"))
    if not video_files:
        print(f"✗ 错误：未找到视频文件 - {video_dir}")
        return

    print(f"\n找到 {len(video_files)} 个视频文件")

    # 显示压缩设置
    print("\n压缩设置：")
    print(f"  分辨率: {TARGET_RESOLUTION[0]}x{TARGET_RESOLUTION[1]}")
    print(f"  视频码率: {TARGET_BITRATE}")
    print(f"  帧率: {TARGET_FPS} fps")
    print(f"  音频码率: 64k")

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

        if compress_video_moviepy(video_file, output_path):
            compressed_size = output_path.stat().st_size
            total_compressed_size += compressed_size
            compression_ratio = (1 - compressed_size / original_size) * 100

            print(f"  ✓ 压缩完成")
            print(f"  压缩后大小: {format_size(compressed_size)}")
            print(f"  压缩率: {compression_ratio:.1f}%")

            # 替换原始文件
            shutil.copy2(output_path, video_file)
            print(f"  ✓ 已替换原始文件")

            success_count += 1
        else:
            print(f"  ✗ 压缩失败，保留原始文件")

    # 显示总结
    print("\n" + "=" * 70)
    print("压缩完成！")
    print("=" * 70)
    print(f"成功压缩: {success_count}/{len(video_files)} 个文件")
    print(f"原始总大小: {format_size(total_original_size)}")
    print(f"压缩后总大小: {format_size(total_compressed_size)}")

    if total_original_size > 0:
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
        print("✓ 备份文件已删除")

    if output_dir.exists():
        print(f"删除临时目录: {output_dir.relative_to(script_dir)}")
        shutil.rmtree(output_dir)
        print("✓ 临时文件已删除")

    print("\n" + "=" * 70)
    print("✓ 所有操作完成！")
    print("✓ 原始视频已全部删除")
    print("✓ 只保留压缩后的视频")
    print("=" * 70)


if __name__ == "__main__":
    main()
