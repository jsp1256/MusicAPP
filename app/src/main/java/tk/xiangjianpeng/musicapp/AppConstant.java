package tk.xiangjianpeng.musicapp;

/**
 * Created by user on 2017/12/12.
 */

public class AppConstant {
    public class PlayerMsg{
        public static final int STOP_MSG=0;
        public static final int PLAY_MSG=1;
        public static final int PAUSE_MSG=2;
    }
    public class ActionTypes{
        //一系列动作
        public static final int UPDATE_ACTION = 3;
        public static final int COMPLETE_ACTION = 4;
        public static final int MUSIC_CURRENT = 5;
        public static final int MUSIC_DURATION = 6;
        public static final int REPEAT_ACTION = 7;
        public static final int SHUFFLE_ACTION = 8;
    }
    public class Status{
        public static final int status_repeat_none = 9;       // 无重复播放
        public static final int status_repeat = 10;           // 全部循环
        public static final int status_repeat_onlyone = 11;   // 单曲循环
        public static final int status_random_none=12;        //不随机播放
        public static final int status_random=13;             //随机播放
    }
}
