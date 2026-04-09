# 任务10：查看Spotify频道一共发了多少条状态，给我一个阿拉伯数字即可。
def validate_task_ten(result=None, device_id=None, backup_dir=None):
    """验证任务10：回答"10"。"""
    if result and "final_message" in result and result["final_message"] is not None:
        if "10" in result["final_message"]:
            return True
    return False


if __name__ == "__main__":
    result = validate_task_ten()
    print(result)
