# 任务8：查看有未读消息的会话有多少个，给我一个阿拉伯数字即可。
def validate_task_eight(result=None, device_id=None, backup_dir=None):
    """验证任务8：回答"8"。"""
    if result and "final_message" in result and result["final_message"] is not None:
        if "8" in result["final_message"]:
            return True
    return False


if __name__ == "__main__":
    result = validate_task_eight()
    print(result)
