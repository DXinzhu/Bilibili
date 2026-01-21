#!/usr/bin/env python3
"""
视频压缩脚本
使用 FFmpeg 压缩 app/src/main/res/raw 目录下的所有视频文件
"""

import os
import subprocess
import shutil
from pathlib import Path

# 配置参数
VIDEO_DIR = r"app\src\main\res\raw"
BACKUP_DIR = r"app\src\main\res\raw\backup_original"
OUTPUT_DIR = r"app\src\main\res\raw\compressed"

# 压缩参数配置
COMPRESSION_SETTINGS = {
    # 视频编码器：H.264
    'video_codec': 'libx264',
    # 视频码率：降低到 500k (可根据需要调整：300k-1000k)
    'video_bitrate': '500k',
    # 音频编码器：AAC
    'audio_codec': 'aac',
    # 音频码率：降低到 64k
    'audio_bitrate': '64k',
    # 分辨率：缩放到 720p (可选：480p=854x480, 360p=640x360)
    'scale': '1280:720',
    # 帧率：降低到 24fps
    'fps': '24',
    # 预设：faster (可选：ultrafast, superfast, veryfast, faster, fast, medium, slow)
    'preset': 'faster',
    # CRF质量：23 (范围18-28，数值越大文件越小但质量越低)
    'crf': '23'
}


def check_ffmpeg():
    """检查 FFmpeg 是否已安装"""
    try:
        result = subprocess.run(['ffmpeg', '-version'],
                              capture_output=True,
                              text=True,
                              check=True)
        print("✓ FFmpeg 已安装")
        return True
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("✗ 错误：未找到 FFmpeg")
        print("\n请先安装 FFmpeg：")
        print("1. 访问 https://ffmpeg.org/download.html")
        print("2. 下载 Windows 版本")
        print("3. 解压并将 bin 目录添加到系统 PATH")
        print("\n或使用 Chocolatey 安装：choco install ffmpeg")
        return False


def get_video_info(video_path):
    """获取视频信息"""
    try:
        cmd = [
            'ffprobe',
            '-v', 'error',
            '-show_entries', 'format=size,duration',
            '-of', 'default=noprint_wrappers=1',
            video_path
        ]
        result = subprocess.run(cmd, capture_output=True, text=True, check=True)

        info = {}
        for line in result.stdout.strip().split('\n'):
            if '=' in line:
                key, value = line.split('=')
                info[key] = value

        return info
    except Exception as e:
        print(f"  警告：无法获取视频信息 - {e}")
        return {}


def format_size(size_bytes):
    """格式化文件大小"""
    for unit in ['B', 'KB', 'MB', 'GB']:
        if size_bytes < 1024.0:
            return f"{size_bytes:.2f} {unit}"
        size_bytes /= 1024.0
    return f"{size_bytes:.2f} TB"


def compress_video(input_path, output_path, settings):
    """压缩单个视频文件"""
    cmd = [
        'ffmpeg',
        '-i', input_path,
        '-c:v', settings['video_codec'],
        '-b:v', settings['video_bitrate'],
        '-vf', f"scale={settings['scale']},fps={settings['fps']}",
        '-preset', settings['preset'],
        '-crf', settings['crf'],
        '-c:a', settings['audio_codec'],
        '-b:a', settings['audio_bitrate'],
        '-movflags', '+faststart',  # 优化流媒体播放
        '-y',  # 覆盖输出文件
        output_path
    ]

    try:
        subprocess.run(cmd, check=True, capture_output=True)
        return True
    except subprocess.CalledProcessError as e:
        print(f"  错误：压缩失败 - {e}")
        return False


def main():
    print("=" * 60)
    print("视频压缩脚本")
    print("=" * 60)

    # 检查 FFmpeg
    if not check_ffmpeg():
        return

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
    print(f"  视频码率: {COMPRESSION_SETTINGS['video_bitrate']}")
    print(f"  音频码率: {COMPRESSION_SETTINGS['audio_bitrate']}")
    print(f"  分辨率: {COMPRESSION_SETTINGS['scale']}")
    print(f"  帧率: {COMPRESSION_SETTINGS['fps']} fps")
    print(f"  CRF质量: {COMPRESSION_SETTINGS['crf']}")

    # 确认是否继续
    print("\n" + "=" * 60)
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
        if not backup_path.exists():
            print(f"  备份到: {backup_path.relative_to(script_dir)}")
            shutil.copy2(video_file, backup_path)

        # 压缩视频
        output_path = output_dir / video_file.name
        print(f"  压缩中...")

        if compress_video(str(video_file), str(output_path), COMPRESSION_SETTINGS):
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
            print(f"  ✗ 压缩失败，跳过")

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
    print(f"压缩文件保存在: {output_dir.relative_to(script_dir)}")
    print("\n提示：如果压缩效果满意，可以删除 backup_original 和 compressed 目录")


if __name__ == "__main__":
    main()
