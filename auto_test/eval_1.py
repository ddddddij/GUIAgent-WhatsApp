# 任务1：进入聊天列表的第一个会话，查看最新一条消息是谁发的，告诉我名字即可。
def validate_task_one(result=None, device_id=None, backup_dir=None):
    """验证任务1：回答"Emily Chen"。"""
    if result and "final_message" in result and result["final_message"] is not None:
        if "Emily Chen" in result["final_message"]:
            return True
    return False


if __name__ == "__main__":
    result = validate_task_one()
    print(result)
