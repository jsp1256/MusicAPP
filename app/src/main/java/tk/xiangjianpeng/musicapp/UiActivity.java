package tk.xiangjianpeng.musicapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author xiangjianpeng
 *         UI界面实体类
 */

public class UiActivity extends AppCompatActivity implements View.OnClickListener {
    private Player_Status player_status;            //播放器状态

    private Button mainBtn;             //返回主Activity
    private Button previousBtn;         // 上一首
    private Button repeatBtn;           // 重复（单曲循环、全部循环）
    private Button playBtn;             // 播放（播放、暂停）
    private Button randomBtn;           //随机播放
    private Button nextBtn;             // 下一首
    private TextView musicTitle;        //歌名
    private TextView musicArtist;       //歌手
    private SeekBar audioTrack;         //播放器进度条
    private TextView current_progress;  //当前时间
    private TextView final_progress;    //结束时间
    public LrcTextView lrcTextView;    //歌词秀

    private PlayServiceConnection playServiceConnection;
    private PlayerService.PlayBinder playBinder;
    private UiHandle uiHandle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout);
        init();
        ServiceBind();
    }

    /**
     * 各种按钮控件的初始化
     */
    private void init() {
        uiHandle = new UiHandle();
        mainBtn = (Button) findViewById(R.id.btn_to_main);
        previousBtn = (Button) findViewById(R.id.previous_music);
        repeatBtn = (Button) findViewById(R.id.repeat_music);
        playBtn = (Button) findViewById(R.id.play_music);
        randomBtn = (Button) findViewById(R.id.random_music);
        nextBtn = (Button) findViewById(R.id.next_music);
        musicArtist = (TextView) findViewById(R.id.musicArtist);
        musicTitle = (TextView) findViewById(R.id.musicTitle);
        audioTrack = (SeekBar) findViewById(R.id.audioTrack);
        current_progress = (TextView) findViewById(R.id.current_progress);
        final_progress = (TextView) findViewById(R.id.final_progress);
        lrcTextView = (LrcTextView) findViewById(R.id.lrcTextView);

        mainBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        repeatBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        randomBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        audioTrack.setMax(1000);
        audioTrack.setOnSeekBarChangeListener(new SeekBarChangeListener());
    }

    private void initView() {
        //获得当前播放器状态
        player_status = playBinder.callgetPlayer_status();
        //静态变量初始化
        if (player_status.isPlayed()) {
            Mp3Info mp3Info = playBinder.callgetlocalmusic();
            setStaticValue(mp3Info);
        }
        //动态按钮初始化
        setPlayBtn(player_status);
        setRandomBtn(player_status);
        setRepeatBtn(player_status);
        //显示歌词
        playBinder.callinitLrc(lrcTextView);
    }

    /**
     * bind服务绑定
     */
    public void ServiceBind() {
        if (playServiceConnection == null) {
            playServiceConnection = new PlayServiceConnection();
        }
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, playServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * bind服务解绑
     */
    public void ServiceUnbind() {
        if (playServiceConnection != null) {
            unbindService(playServiceConnection);
            playServiceConnection = null;
        }
    }

    /**
     * 服务连接
     */
    private class PlayServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder PlayBind) {
            playBinder = (PlayerService.PlayBinder) PlayBind;
            playBinder.callsetUiHandle(uiHandle);
            initView();
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /**
     * 设置页面静态变量
     *
     * @param mp3Info
     */
    private void setStaticValue(Mp3Info mp3Info) {
        musicArtist.setText(mp3Info.getArtist());
        musicTitle.setText(mp3Info.getTitle());
        final_progress.setText(MediaUtils.formatTime(mp3Info.getDuration()));
    }

    /**
     * 设置random按钮
     *
     * @param player_status
     */
    public void setRandomBtn(Player_Status player_status) {
        switch (player_status.getRandom_status()) {
            case AppConstant.Status.status_random:
                randomBtn.setBackground(getResources().getDrawable(R.drawable.random_onclick_selector));
                break;
            case AppConstant.Status.status_random_none:
                randomBtn.setBackground(getResources().getDrawable(R.drawable.random_selector));
                break;
        }
    }

    /**
     * 设置播放按钮
     *
     * @param player_status
     */
    public void setPlayBtn(Player_Status player_status) {
        if (player_status.isPlay()) {
            playBtn.setBackground(getResources().getDrawable(R.drawable.pause_selector));
        } else {
            playBtn.setBackground(getResources().getDrawable(R.drawable.play_selector));
        }
    }

    /**
     * 设置重复按钮
     *
     * @param player_status
     */
    public void setRepeatBtn(Player_Status player_status) {
        switch (player_status.getRepeat_status()) {
            case AppConstant.Status.status_repeat_none:
                repeatBtn.setBackground(getResources().getDrawable(R.drawable.repeat_none_selector));
                break;
            case AppConstant.Status.status_repeat:
                repeatBtn.setBackground(getResources().getDrawable(R.drawable.repeat_selector));
                break;
            case AppConstant.Status.status_repeat_onlyone:
                repeatBtn.setBackground(getResources().getDrawable(R.drawable.repeat_onlyone_selector));
                break;
        }
    }

    /**
     * 页面按钮点击监听器
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_to_main:
                Intent intent = new Intent(UiActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.previous_music:
                playBinder.callprevious();
                playBinder.callLrcViewRestart(lrcTextView);
                break;
            case R.id.next_music:
                playBinder.callnext();
                playBinder.callLrcViewRestart(lrcTextView);
                break;
            case R.id.play_music:
                playBinder.callplay_pause();
                setPlayBtn(playBinder.callgetPlayer_status());
                break;
            case R.id.random_music:
                playBinder.callsetRandom();
                setRandomBtn(playBinder.callgetPlayer_status());
                break;
            case R.id.repeat_music:
                playBinder.callsetRepeat();
                setRepeatBtn(playBinder.callgetPlayer_status());
                break;
        }
    }

    /**
     * 进度条点击事件监听器
     */
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int dest = seekBar.getProgress();
                int musicMax = (int) playBinder.callgetlocalmusic().getDuration();
                int audioTrackMax = audioTrack.getMax();
                playBinder.callPlayPosition(musicMax * dest / audioTrackMax);
                lrcTextView.setIndex(playBinder.calllrcIndex());
                current_progress.setText(MediaUtils.formatTime(playBinder.callgetCurrentProgress()));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 接受来自PlayerService的消息，控制页面刷新
     */
    public class UiHandle extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == AppConstant.ActionTypes.UPDATE_ACTION) {
                Mp3Info mp3Info = playBinder.callgetlocalmusic();
                setStaticValue(mp3Info);
                setPlayBtn(playBinder.callgetPlayer_status());
            } else if (msg.what == AppConstant.ActionTypes.MUSIC_CURRENT) {
                float position = playBinder.callgetCurrentProgress();
                current_progress.setText(MediaUtils.formatTime(playBinder.callgetCurrentProgress()));
                Mp3Info mp3Info = playBinder.callgetlocalmusic();
                float musicMax = mp3Info.getDuration();
                float audiotrackMax = audioTrack.getMax();
                audioTrack.setProgress((int) (audiotrackMax * position / musicMax));
            } else if (msg.what == AppConstant.ActionTypes.COMPLETE_ACTION) {
                audioTrack.setProgress(0);
                setPlayBtn(playBinder.callgetPlayer_status());
                current_progress.setText("0:00");
            }
        }
    }

    protected void onDestroy() {
        ServiceUnbind();
        super.onDestroy();
    }
}
