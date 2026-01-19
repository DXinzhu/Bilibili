@echo off
chcp 65001 >nul
echo ============================================================
echo 批量测试 Bilibili 任务
echo ============================================================
echo.

cd /d "%~dp0"

echo 测试任务 17: 评论点赞数
python simple_test.py 17 378
echo.

echo 测试任务 18: 消息通知开关
python simple_test.py 18 "已关闭"
echo.

echo 测试任务 19: 等级最低评论点赞数
python simple_test.py 19 67
echo.

echo 测试任务 20: 搜索游戏
python simple_test.py 20 3
echo.

echo 测试任务 21: 相关视频数量
python simple_test.py 21 4
echo.

echo 测试任务 25: UID
python simple_test.py 25 1668348161
echo.

echo 测试任务 26: 历史记录
python simple_test.py 26 8
echo.

echo 测试任务 29: 直播人数
python simple_test.py 29 8623
echo.

echo 测试任务 30: 会员购销量
python simple_test.py 30 4963
echo.

echo ============================================================
echo 所有测试完成
echo ============================================================
pause
