# 任务3：查看我的账户绑定的手机号码是多少，告诉我答案即可。
def validate_task_three(result=None, device_id=None, backup_dir=None):
    """验证任务3：回答"+1 (415) 555-0192"或"(415) 555-0192"。"""
    if result and "final_message" in result and result["final_message"] is not None:
        if "+1 (415) 555-0192" in result["final_message"] or "(415) 555-0192" in result["final_message"]:
            return True
    return False


if __name__ == "__main__":
    result = validate_task_three()
    print(result)
