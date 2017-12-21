package tk.xiangjianpeng.musicapp;

/**
 * @author xiangjianpeng
 * 各种状态宏定义实体类
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
    public class HistoryDB{
        public static final String ID="id";
        public static final String TITLE="title";
        public static final String ARTIST="artist";
        public static final String DURATION="duration";
        public static final String URL="url";
        public static final String LAST_TIME="last_time";
    }
}
