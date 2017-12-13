package tk.xiangjianpeng.musicapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by user on 2017/12/13.
 */

public class UiActivity extends AppCompatActivity {
    private int repeatState;                        //循环标识
    private boolean isFirstTime = true;             // 是否是第一次
    private boolean isPlaying;                      // 正在播放
    private boolean isPause;                        // 暂停
    private boolean isNoneShuffle = true;           // 顺序播放
    private boolean isShuffle = false;              // 随机播放

    private Button previousBtn;         // 上一首
    private Button repeatBtn;           // 重复（单曲循环、全部循环）
    private Button playBtn;             // 播放（播放、暂停）
    private Button shuffleBtn;          // 随机播放
    private Button nextBtn;             // 下一首

    private int currentTime;    //当前时间
    private int duration;       //持续时间
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout);
    }
}
