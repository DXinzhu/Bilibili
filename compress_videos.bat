@echo off
chcp 65001 >nul
echo ========================================
echo 视频压缩脚本
echo ========================================
echo.

REM 检查 Python 是否安装
python --version >nul 2>&1
if errorlevel 1 (
    echo 错误：未找到 Python
    echo 请先安装 Python 3.x
    pause
    exit /b 1
)

REM 运行 Python 脚本
python compress_videos.py

echo.
pause
