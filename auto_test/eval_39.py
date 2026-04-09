# 任务39：向所有首字母为L的联系人发起通话，然后转成视频通话，持续10s后挂断。
import json
import os
import subprocess


def validate_task_thirty_nine(result=None, device_id=None, backup_dir=None):
    """验证任务39：calls.json中第一条的callType为VIDEO，contactIds含指定ids，durationSeconds大于10。"""
    file_path = os.path.join(backup_dir, "calls.json") if backup_dir else "calls.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/calls.json"])
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
    if not isinstance(first_item, dict):
        return False

    if first_item.get("callType") != "VIDEO":
        return False

    contact_ids = first_item.get("contactIds", [])
    if "contact_011" not in contact_ids or "contact_007" not in contact_ids or "contact_017" not in contact_ids:
        return False

    duration = first_item.get("durationSeconds", 0)
    if isinstance(duration, (int, float)) and duration > 10:
        return True

    return False


if __name__ == "__main__":
    result = validate_task_thirty_nine()
    print(result)
