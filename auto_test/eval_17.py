# 任务17：新建一个联系人Jaye Zhang，电话号码为(415) 555-1230。
import json
import os
import subprocess


def validate_task_seventeen(result=None, device_id=None, backup_dir=None):
    """验证任务17：contacts.json中存在displayName为Jaye Zhang且phone为+1 (415) 555-1230的数据。"""
    file_path = os.path.join(backup_dir, "contacts.json") if backup_dir else "contacts.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/contacts.json"])
    subprocess.run(cmd, stdout=open(file_path, "w"))

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)
            items = data if isinstance(data, list) else []
    except:
        return False

    for item in items:
        if isinstance(item, dict) and item.get("displayName") == "Jaye Zhang" and item.get("phone") == "+1 (415) 555-1230":
            return True

    return False


if __name__ == "__main__":
    result = validate_task_seventeen()
    print(result)
