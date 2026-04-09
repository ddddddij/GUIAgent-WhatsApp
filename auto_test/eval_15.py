# 任务15：将Isabella Martinez加入Startup Ideas群聊。
import json
import os
import subprocess


def validate_task_fifteen(result=None, device_id=None, backup_dir=None):
    """验证任务15：group_details.json中conv_010的memberIds包含user_012。"""
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

    for item in items:
        if isinstance(item, dict) and item.get("conversationId") == "conv_010":
            member_ids = item.get("memberIds", [])
            if "user_012" in member_ids:
                return True

    return False


if __name__ == "__main__":
    result = validate_task_fifteen()
    print(result)
