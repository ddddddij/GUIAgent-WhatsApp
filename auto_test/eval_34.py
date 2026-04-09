# 任务34：找到通话记录中出现次数最多的联系人，进入聊天列表与其对话，发送消息"Feel free to contact me at any time."。
import json
import os
import subprocess


def validate_task_thirty_four(result=None, device_id=None, backup_dir=None):
    """验证任务34：messages.json中最后一条数据的conversationId为conv_001且textContent匹配。"""
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
    if isinstance(last_item, dict) and last_item.get("conversationId") == "conv_001" and last_item.get("textContent") == "Feel free to contact me at any time.":
        return True

    return False


if __name__ == "__main__":
    result = validate_task_thirty_four()
    print(result)
