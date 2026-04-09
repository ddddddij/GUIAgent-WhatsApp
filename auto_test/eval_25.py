# 任务25：新建一个社群，名称为"Hip-hop music enthusiasts"，社群描述为指定内容。
import json
import os
import subprocess


def validate_task_twenty_five(result=None, device_id=None, backup_dir=None):
    """验证任务25：communities.json中第一条数据的name和description匹配。"""
    file_path = os.path.join(backup_dir, "communities.json") if backup_dir else "communities.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/communities.json"])
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
    if isinstance(first_item, dict):
        if first_item.get("name") == "Hip-hop music enthusiasts" and first_item.get("description") == "Welcome all hip-hop music enthusiasts to join this community. Please share and discuss your favorite music!":
            return True

    return False


if __name__ == "__main__":
    result = validate_task_twenty_five()
    print(result)
