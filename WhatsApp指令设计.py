from ..base import AppTasks, TaskItem
from .eval_1 import validate_task_one
from .eval_2 import validate_task_two
from .eval_3 import validate_task_three
from .eval_4 import validate_task_four
from .eval_5 import validate_task_five
from .eval_6 import validate_task_six
from .eval_7 import validate_task_seven
from .eval_8 import validate_task_eight
from .eval_9 import validate_task_nine
from .eval_10 import validate_task_ten
from .eval_11 import validate_task_eleven
from .eval_12 import validate_task_twelve
from .eval_13 import validate_task_thirteen
from .eval_14 import validate_task_fourteen
from .eval_15 import validate_task_fifteen
from .eval_16 import validate_task_sixteen
from .eval_17 import validate_task_seventeen
from .eval_18 import validate_task_eighteen
from .eval_19 import validate_task_nineteen
from .eval_20 import validate_task_twenty
from .eval_21 import validate_task_twenty_one
from .eval_22 import validate_task_twenty_two
from .eval_23 import validate_task_twenty_three
from .eval_24 import validate_task_twenty_four
from .eval_25 import validate_task_twenty_five
from .eval_26 import validate_task_twenty_six
from .eval_27 import validate_task_twenty_seven
from .eval_28 import validate_task_twenty_eight
from .eval_29 import validate_task_twenty_nine
from .eval_30 import validate_task_thirty
from .eval_31 import validate_task_thirty_one
from .eval_32 import validate_task_thirty_two
from .eval_33 import validate_task_thirty_three
from .eval_34 import validate_task_thirty_four
from .eval_35 import validate_task_thirty_five
from .eval_36 import validate_task_thirty_six
from .eval_37 import validate_task_thirty_seven
from .eval_38 import validate_task_thirty_eight
from .eval_39 import validate_task_thirty_nine
from .eval_40 import validate_task_forty

AMAZON_TASKS = AppTasks(
    package_name="com.example.amazon_sim",
    task_items=[
        TaskItem(
            instruction="进入聊天列表的第一个会话，查看最新一条消息是谁发的，告诉我名字即可。",
            verify_func=validate_task_one,
            human_steps=3,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="告诉我最近一条通话是和谁的，告诉我名字即可。",
            verify_func=validate_task_two,
            human_steps=3,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="查看我的账户绑定的手机号码是多少，告诉我答案即可。",
            verify_func=validate_task_three,
            human_steps=3,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="为Olivia新上传的状态点赞。",
            verify_func=validate_task_four,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="给“Friday Night Plans”群聊发送一条消息，内容是“I feel so excited!!!”。",
            verify_func=validate_task_five,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="告诉我最近一条视频通话是和谁的，告诉我名字即可。",
            verify_func=validate_task_six,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="Follow一下Netflix的频道。",
            verify_func=validate_task_seven,
            human_steps=3,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="查看有未读消息的会话有多少个，给我一个阿拉伯数字即可。",
            verify_func=validate_task_eight,
            human_steps=3,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="查看我加入了多少个社群。",
            verify_func=validate_task_nine,
            human_steps=2,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="查看Spotify频道一共发了多少条状态，给我一个阿拉伯数字即可。",
            verify_func=validate_task_ten,
            human_steps=5,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="创建与James Walker的新对话，通知他“We should have a meeting at 2pm”。",
            verify_func=validate_task_eleven,
            human_steps=8,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="将个人在线状态改为Busy。",
            verify_func=validate_task_twelve,
            human_steps=6,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="在Updates页发布一条状态，背景采用黄色，内容为“Sunshine”。",
            verify_func=validate_task_thirteen,
            human_steps=7,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="打开聊天列表，找到有未读消息的第一个群聊，进入后回复“OK”。",
            verify_func=validate_task_fourteen,
            human_steps=6,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="将Isabella Martinez加入Starup Ideas群聊。",
            verify_func=validate_task_fifteen,
            human_steps=7,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="将Netflix频道中评论量最高的状态转发给Emily Chen。",
            verify_func=validate_task_sixteen,
            human_steps=8,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="新建一个联系人Jaye Zhang，电话号码为(415) 555-1230。",
            verify_func=validate_task_seventeen,
            human_steps=8,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="与Ethan Garcia和Lucas Anderson一起通话，然后再挂断。",
            verify_func=validate_task_eighteen,
            human_steps=8,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="将聊天列表第一个会话设为通知免打扰，返回列表确认已显示静音图标。",
            verify_func=validate_task_nineteen,
            human_steps=7,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="进入Calls页面，向通话记录中的第一个联系人发送一条文字消息“Hello, are you free?”。",
            verify_func=validate_task_twenty,
            human_steps=8,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="在Spotify频道找到有关Taylor Swift的状态，发一个火焰的表情。",
            verify_func=validate_task_twenty_one,
            human_steps=6,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="将Updates页面followers数量最多的频道设置静音。",
            verify_func=validate_task_twenty_two,
            human_steps=6,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="把Mia Harris和Noah Kim加入NYC Foodies社群。",
            verify_func=validate_task_twenty_three,
            human_steps=7,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="进入Weekend Hiking Crew群聊打视频，持续5秒以上再挂断。",
            verify_func=validate_task_twenty_four,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="新建一个社群，名称为“Hip-hop music enthusiasts”，社群描述为“Welcome all hip-hop music enthusiasts to join this community. Please share and discuss your favorite music!”",
            verify_func=validate_task_twenty_five,
            human_steps=8,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="新建一个群聊，群成员包括Rachel Green、Sophia Lee、Tom Briggs，将群名设置为“Project Discussion Group”，进入群聊后发送消息“Hello everyone, welcome to join the project discussion group!”。",
            verify_func=validate_task_twenty_six,
            human_steps=14,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="创建一个新社区，命名为“Book Club”，选择前两个联系人加入。",
            verify_func=validate_task_twenty_seven,
            human_steps=11,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="看看Noah Kim发给我的最新消息，阅读他交代的事情，按他说的做。",
            verify_func=validate_task_twenty_eight,
            human_steps=12,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="从聊天列表进入第3个群聊，查看群成员列表，找到群管理员的名字，然后给该管理员单独发一条消息“Hello, I'm a member of the group. I have a question to ask.”。",
            verify_func=validate_task_twenty_nine,
            human_steps=12,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="找到Spotify频道中有关本周最火播放列表的状态，转发至Weekend Hiking Crew群聊中，并询问大家“Which song do you pick?”",
            verify_func=validate_task_thirty,
            human_steps=15,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="新建一个广播列表，加入Ava、Emily Chen、Ethan Garcia、Isabella Martinez，向该列表发送消息“Please submit this week's report by Friday. Make sure to complete it on time.”。",
            verify_func=validate_task_thirty_one,
            human_steps=13,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="进入Marcus Davis分享的状态所在的频道，并给那一条状态发送红色爱心，然后返回聊天给他发送“Thanks for sharing!”",
            verify_func=validate_task_thirty_two,
            human_steps=13,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="依次将前三个群聊会话全部设为静音。",
            verify_func=validate_task_thirty_three,
            human_steps=14,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="找到通话记录中出现次数最多的联系人，进入聊天列表与其对话，发送消息“Feel free to contact me at any time.”。",
            verify_func=validate_task_thirty_four,
            human_steps=12,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="进入第一个社区，查看公告栏最新的一条内容并记录，然后进入该社群的General，在群内发送消息“Tip：[公告内容]”（将公告内容填入括号）。",
            verify_func=validate_task_thirty_five,
            human_steps=16,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="给所有超过3条未读消息的会话发送“ok”。",
            verify_func=validate_task_thirty_six,
            human_steps=13,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="添加新联系人Jessie Brown，电话为(415) 555-1013，将她加入SF Tech Squad群聊。",
            verify_func=validate_task_thirty_seven,
            human_steps=12,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="给所有好友的状态都评论一个紫色爱心。",
            verify_func=validate_task_thirty_eight,
            human_steps=13,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="向所有首字母为L的联系人发起通话，然后转成视频通话，持续10s后挂断。",
            verify_func=validate_task_thirty_nine,
            human_steps=12,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="去Netflix频道查找Black Mirror S7的上映时间，并且把这个消息告诉Sophia Lee。",
            verify_func=validate_task_forty,
            human_steps=14,
            is_reasoning=True,
        ),
    ],
)
