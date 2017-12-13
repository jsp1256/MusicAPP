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
        public static final String UPDATE_ACTION = "tk.xiangjianpeng.UPDATE_ACTION";
        public static final String CTL_ACTION = "tk.xiangjianpeng.CTL_ACTION";
        public static final String MUSIC_CURRENT = "tk.xiangjianpeng.MUSIC_CURRENT";
        public static final String MUSIC_DURATION = "tk.xiangjianpeng.MUSIC_DURATION";
        public static final String REPEAT_ACTION = "tk.xiangjianpeng.REPEAT_ACTION";
        public static final String SHUFFLE_ACTION = "tk.xiangjianpeng.action.SHUFFLE_ACTION";
    }
    public class Stautus{
        private final int status_repeat_none = 0;      // 无重复播放
        private final int status_repeat = 1;           // 全部循环
        private final int status_repeat_onlyone = 2;   // 单曲循环
    }
}
