# 任务14：打开聊天列表，找到有未读消息的第一个群聊，进入后回复"OK"。
import json
import os
import subprocess


def validate_task_fourteen(result=None, device_id=None, backup_dir=None):
    """验证任务14：messages.json中最后一条数据的conversationId为conv_004且textContent为OK。"""
    file_path = os.path.join(backup_dir, "messages.json") if backup_dir else "messages.json"

    cmd = ["adb"]
    if device_id:
        cmd.extend(["-s", device_id])
    cmd.extend(["exec-out", "run-as", "com.example.whatsapp_sim", "cat", "files/data/messages.json"])
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
    if isinstance(last_item, dict) and last_item.get("conversationId") == "conv_004" and last_item.get("textContent") == "OK":
        return True

    return False


if __name__ == "__main__":
    result = validate_task_fourteen()
    print(result)
