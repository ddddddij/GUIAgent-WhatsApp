# 任务19：将聊天列表第一个会话设为通知免打扰，返回列表确认已显示静音图标。
import json
import os
import subprocess


def validate_task_nineteen(result=None, device_id=None, backup_dir=None):
    """验证任务19：group_details.json中第一条数据的isMuted为true。"""
    file_path = os.path.join(backup_dir, "group_details.json") if backup_dir else "group_details.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/group_details.json"])
    subprocess.run(cmd, stdout=open(file_path, "w"))

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)
            items = data if isinstance(data, list) else []
    except:
        return False

    if not items:
        return False

    first_item = items[0]
    if isinstance(first_item, dict) and first_item.get("isMuted") == True:
        return True

    return False


if __name__ == "__main__":
    result = validate_task_nineteen()
    print(result)
