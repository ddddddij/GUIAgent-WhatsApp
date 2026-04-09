# 任务9：查看我加入了多少个社群。
def validate_task_nine(result=None, device_id=None, backup_dir=None):
    """验证任务9：回答"3"。"""
    if result and "final_message" in result and result["final_message"] is not None:
        if "3" in result["final_message"]:
            return True
    return False


if __name__ == "__main__":
    result = validate_task_nine()
    print(result)
