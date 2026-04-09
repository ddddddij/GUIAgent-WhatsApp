# 任务2：告诉我最近一条通话是和谁的，告诉我名字即可。
def validate_task_two(result=None, device_id=None, backup_dir=None):
    """验证任务2：回答"Sarah Mitchell"。"""
    if result and "final_message" in result and result["final_message"] is not None:
        if "Sarah Mitchell" in result["final_message"]:
            return True
    return False


if __name__ == "__main__":
    result = validate_task_two()
    print(result)
