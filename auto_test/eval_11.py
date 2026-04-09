# 任务11：创建与James Walker的新对话，通知他"We should have a meeting at 2pm"。
import json
import os
import subprocess


def validate_task_eleven(result=None, device_id=None, backup_dir=None):
    """验证任务11：conversations.json中最后一条数据的participantNames包含JiayiDai和James Walker。"""
    file_path = os.path.join(backup_dir, "conversations.json") if backup_dir else "conversations.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/conversations.json"])
    subprocess.run(cmd, stdout=open(file_path, "w"))

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)
            items = data if isinstance(data, list) else []
    except:
        return False

    if not items:
        return False

    last_item = items[-1]
    if not isinstance(last_item, dict):
        return False

    participant_names = last_item.get("participantNames", [])
    if "JiayiDai" in participant_names and "James Walker" in participant_names:
        if last_item.get("lastMessagePreview") == "We should have a meeting at 2pm":
            return True

    return False


if __name__ == "__main__":
    result = validate_task_eleven()
    print(result)
