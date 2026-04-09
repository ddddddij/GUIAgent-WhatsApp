# 任务31：新建一个广播列表，加入Ava、Emily Chen、Ethan Garcia、Isabella Martinez，向该列表发送指定消息。
import json
import os
import subprocess


def validate_task_thirty_one(result=None, device_id=None, backup_dir=None):
    """验证任务31：messages.json中最后四条数据的conversationId分别为conv_003/011/012/013且textContent一致。"""
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

    if len(items) < 4:
        return False

    last_four = items[-4:]
    expected_conv_ids = ["conv_003", "conv_011", "conv_012", "conv_013"]
    expected_text = "Please submit this week\u0027s report by Friday. Make sure to complete it on time."

    for i, item in enumerate(last_four):
        if not isinstance(item, dict):
            return False
        if item.get("conversationId") != expected_conv_ids[i]:
            return False
        if item.get("textContent") != expected_text:
            return False

    return True


if __name__ == "__main__":
    result = validate_task_thirty_one()
    print(result)
