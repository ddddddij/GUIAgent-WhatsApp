# 任务22：将Updates页面followers数量最多的频道设置静音。
import json
import os
import subprocess


def validate_task_twenty_two(result=None, device_id=None, backup_dir=None):
    """验证任务22：channels.json中real_madrid的isNotificationMuted为true。"""
    file_path = os.path.join(backup_dir, "channels.json") if backup_dir else "channels.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/channels.json"])
    subprocess.run(cmd, stdout=open(file_path, "w"))

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)
            items = data if isinstance(data, list) else []
    except:
        return False

    for item in items:
        if isinstance(item, dict) and item.get("id") == "real_madrid":
            if item.get("isNotificationMuted") == True:
                return True

    return False


if __name__ == "__main__":
    result = validate_task_twenty_two()
    print(result)
