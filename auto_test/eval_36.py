# 任务36：给所有超过3条未读消息的会话发送"ok"。
import json
import os
import subprocess


def validate_task_thirty_six(result=None, device_id=None, backup_dir=None):
    """验证任务36：messages.json中最后3条的conversationId分别为conv_004/010/015且textContent都为ok。"""
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

    if len(items) < 3:
        return False

    last_three = items[-3:]
    expected_conv_ids = ["conv_004", "conv_010", "conv_015"]

    for i, item in enumerate(last_three):
        if not isinstance(item, dict):
            return False
        if item.get("conversationId") != expected_conv_ids[i]:
            return False
        if item.get("textContent") != "ok":
            return False

    return True


if __name__ == "__main__":
    result = validate_task_thirty_six()
    print(result)
