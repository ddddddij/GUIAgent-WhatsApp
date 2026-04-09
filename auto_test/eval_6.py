# 任务6：告诉我最近一条视频通话是和谁的，告诉我名字即可。
def validate_task_six(result=None, device_id=None, backup_dir=None):
    """验证任务6：回答"Marcus Davis"。"""
    if result and "final_message" in result and result["final_message"] is not None:
        if "Marcus Davis" in result["final_message"]:
            return True
    return False


if __name__ == "__main__":
    result = validate_task_six()
    print(result)
