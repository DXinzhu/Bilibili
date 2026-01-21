#!/usr/bin/env python3
"""
视频压缩脚本 - 使用 moviepy 库
自动安装依赖并压缩视频
"""

import os
import sys
import subprocess
import shutil
from pathlib import Path

# 配置参数
VIDEO_DIR = r"app\src\main\res\raw"
BACKUP_DIR = r"app\src\main\res\raw\backup_original"
OUTPUT_DIR = r"app\src\main\res\raw\compressed"

# 压缩参数
TARGET_RESOLUTION = (1280, 720)  # 720p
TARGET_BITRATE = "500k"
TARGET_FPS = 24


def install_dependencies():
    """安装必要的依赖"""
    print("检查并安装依赖...")
    dependencies = ['moviepy', 'imageio-ffmpeg']

    for package in dependencies:
        try:
            __import__(package.replace('-', '_'))
            print(f"✓ {package} 已安装")
        except ImportError:
            print(f"正在安装 {package}...")
            try:
                subprocess.check_call([sys.executable, '-m', 'pip', 'install', package])
                print(f"✓ {package} 安装成功")
            except subprocess.CalledProcessError:
                print(f"✗ {package} 安装失败")
                return False

    return True


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

        # 加载视频
        clip = VideoFileClip(str(input_path))

        # 调整分辨率
        if clip.size[0] > TARGET_RESOLUTION[0] or clip.size[1] > TARGET_RESOLUTION[1]:
            clip = clip.resize(height=TARGET_RESOLUTION[1])

        # 调整帧率
        if clip.fps > TARGET_FPS:
            clip = clip.set_fps(TARGET_FPS)

        # 写入压缩后的视频
        clip.write_videofile(
            str(output_path),
            codec='libx264',
            bitrate=TARGET_BITRATE,
            audio_codec='aac',
            audio_bitrate='64k',
            preset='medium',
            threads=4,
            logger=None  # 禁用详细日志
        )

        clip.close()
        return True

    except Exception as e:
        print(f"  错误：{e}")
        return False


def main():
    print("=" * 60)
    print("视频压缩脚本 (MoviePy版本)")
    print("=" * 60)

    # 安装依赖
    if not install_dependencies():
        print("\n✗ 依赖安装失败，无法继续")
        return

    print("\n" + "=" * 60)

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

    # 确认是否继续
    print("\n" + "=" * 60)
    print("注意：原始文件将被备份后删除，只保留压缩后的视频")
    response = input("是否继续压缩？(y/n): ").strip().lower()
    if response != 'y':
        print("已取消")
        return

    # 创建备份和输出目录
    backup_dir.mkdir(parents=True, exist_ok=True)
    output_dir.mkdir(parents=True, exist_ok=True)

    print("\n开始压缩...")
    print("=" * 60)

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
        print(f"  备份到: {backup_path.relative_to(script_dir)}")
        shutil.copy2(video_file, backup_path)

        # 压缩视频
        output_path = output_dir / video_file.name
        print(f"  压缩中...")

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
    print("\n" + "=" * 60)
    print("压缩完成！")
    print("=" * 60)
    print(f"成功压缩: {success_count}/{len(video_files)} 个文件")
    print(f"原始总大小: {format_size(total_original_size)}")
    print(f"压缩后总大小: {format_size(total_compressed_size)}")

    if total_original_size > 0:
        total_compression_ratio = (1 - total_compressed_size / total_original_size) * 100
        saved_size = total_original_size - total_compressed_size
        print(f"总压缩率: {total_compression_ratio:.1f}%")
        print(f"节省空间: {format_size(saved_size)}")

    print(f"\n原始文件已备份到: {backup_dir.relative_to(script_dir)}")

    # 询问是否删除备份
    print("\n" + "=" * 60)
    response = input("是否删除备份的原始文件？(y/n): ").strip().lower()
    if response == 'y':
        print("正在删除备份文件...")
        shutil.rmtree(backup_dir)
        print("✓ 备份文件已删除")
    else:
        print(f"备份文件保留在: {backup_dir.relative_to(script_dir)}")

    # 询问是否删除临时压缩目录
    response = input("是否删除临时压缩目录？(y/n): ").strip().lower()
    if response == 'y':
        print("正在删除临时目录...")
        shutil.rmtree(output_dir)
        print("✓ 临时目录已删除")
    else:
        print(f"临时文件保留在: {output_dir.relative_to(script_dir)}")


if __name__ == "__main__":
    main()
