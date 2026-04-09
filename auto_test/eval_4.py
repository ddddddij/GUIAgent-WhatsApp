# 任务4：为Olivia新上传的状态点赞。
import json
import os
import subprocess


def validate_task_four(result=None, device_id=None, backup_dir=None):
    """验证任务4：user_statuses.json中status_004的likeCount为13。"""
    file_path = os.path.join(backup_dir, "user_statuses.json") if backup_dir else "user_statuses.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/user_statuses.json"])
    subprocess.run(cmd, stdout=open(file_path, "w"))

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)
            items = data if isinstance(data, list) else []
    except:
        return False

    for item in items:
        if isinstance(item, dict) and item.get("id") == "status_004":
            if item.get("likeCount") == 13:
                return True

    return False


if __name__ == "__main__":
    result = validate_task_four()
    print(result)
