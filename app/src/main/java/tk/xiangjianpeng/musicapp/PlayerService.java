package tk.xiangjianpeng.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiangjianpeng
 *         音乐播放服务类
 */
public class PlayerService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Player_Status player_status;
    private List<Mp3Info> mp3Infos;
    private int position;                           //当前歌曲位置
    private MainActivity.MainHandle mainHandle;     //主界面消息通信
    private UiActivity.UiHandle uiHandle;           //UI界面消息通信
    DelayThread delayThread = new DelayThread(200);

    private int currentTime;    //当前时间
    private int duration;       //持续时间

    private LrcProcess mLrcProcess; //歌词处理
    private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
    private int index = 0;          //歌词检索值

    public PlayerService() {
    }

    /**
     * PlayBinder提供的通信
     */
    public class PlayBinder extends Binder {
        //获取当前服务
        protected PlayerService getService() {
            return PlayerService.this;
        }

        //获取当前曲目
        protected Mp3Info callgetlocalmusic() {
            return getlocalmusic();
        }

        //获取当前播放位置
        protected int callgetposition() {
            return position;
        }

        //获取当前音乐播放进度
        protected long callgetCurrentProgress() {
            return getCurrentProgress();
        }

        //获取当前播放器状态
        protected Player_Status callgetPlayer_status() {
            return getPlayer_status();
        }

        //设置主界面消息通信对象
        protected void callsetMainHandle(MainActivity.MainHandle mainHandle1) {
            mainHandle = mainHandle1;
        }

        //播放指定位置
        protected void callPlayPosition(int position) {
            seekTo(position);
        }

        //设置UI界面消息通信对象
        protected void callsetUiHandle(UiActivity.UiHandle uiHandle1) {
            uiHandle = uiHandle1;
        }

        //更新音乐列表
        protected void callupdate() {
            update();
        }

        //上一首
        protected void callprevious() {
            player_status.setPause(false);
            if (player_status.getRandom_status() == AppConstant.Status.status_random)
                playRandom();
            else
                previous();
        }

        //下一首
        protected void callnext() {
            player_status.setPause(false);
            if (player_status.getRandom_status() == AppConstant.Status.status_random)
                playRandom();
            else
                next();
        }

        //播放/继续
        protected void callplay_pause() {
            play_pause();
        }

        //设置播放器状态：随机
        protected void callsetRandom() {
            setRandom();
        }

        //设置播放器状态：循环
        protected void callsetRepeat() {
            setRepeat();
        }

        //初始化歌词显示
        protected void callinitLrc(LrcTextView lrcTextView) {
            initLrc(lrcTextView);
        }
    }

    public void onCreate() {
        player_status = new Player_Status(AppConstant.Status.status_random_none, AppConstant.Status.status_repeat, false, false, false);
        delayThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer.isPlaying()) {
            stop();
        }
        try {
            position = intent.getIntExtra("position", 0);
            int msg = intent.getIntExtra("MSG", 0);
            switch (msg) {
                case AppConstant.PlayerMsg.PLAY_MSG:
                    play(position);
                    break;
                case AppConstant.PlayerMsg.STOP_MSG:
                    stop();
                    break;
            }
        } catch (Exception e) {
            Log.e("start-error:", e.toString());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新乐曲库
     */
    private void update() {
        try {
            mp3Infos = MediaUtils.getMp3Infos(this);   //获取歌曲对象集合
        } catch (Exception e) {
            Log.e("service_error:", e.toString());
        }
    }

    /**
     * 播放指定位置的音乐
     */
    private void play(int position) {
        try {
            player_status.setPlay(true);
            player_status.setPlayed(true);
            Mp3Info mp3Info = mp3Infos.get(position);
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(mp3Info.getUrl());
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
            mediaPlayer.setOnCompletionListener(new CompletionListener());
        } catch (Exception e) {
            Toast.makeText(this, "播放器错误，找不到文件！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 调整播放进度
     *
     * @param position
     */
    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    /**
     * 上一曲
     */
    public void previous() {
        if (!player_status.isPlayed()) return;
        if (position == 0) {
            position = mp3Infos.size() - 1;
        } else {
            position--;
        }
        play(position);
        msgSend_Update();
    }

    /**
     * 下一曲
     */
    public void next() {
        if (!player_status.isPlayed()) return;
        if (player_status.getRepeat_status() == AppConstant.Status.status_repeat_onlyone) {
            play(position);
            return;
        }
        if (position >= mp3Infos.size() - 1) {
            if (player_status.getRepeat_status() == AppConstant.Status.status_repeat_none) {
                stop();
                msgSend_Complete();
                return;
            }
            position = 0;
        } else {
            position++;
        }
        play(position);
        msgSend_Update();
    }

    /**
     * 暂停/继续播放音乐
     */
    private void play_pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            player_status.setPlay(false);
            player_status.setPause(true);
        } else if (mediaPlayer != null && player_status.isPause()) {
            player_status.setPause(false);
            player_status.setPlay(true);
            mediaPlayer.start();
        } else if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            player_status.setPlay(true);
            play(position);
        }
    }

    /**
     * 停止音乐
     */
    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            player_status.setPlay(false);
            try {
                //mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 随机播放音乐
     */
    private void playRandom() {
        position = (int) (Math.random() * mp3Infos.size());
        play(position);
        msgSend_Update();
    }

    /**
     * 初始化歌词配置
     */
    public void initLrc(LrcTextView lrcTextView) {
        mLrcProcess = new LrcProcess();
        //读取歌词文件
        mLrcProcess.readLRC(mp3Infos.get(position).getUrl());
        //传回处理后的歌词文件
        lrcList = mLrcProcess.getLrcList();
        if (lrcList.size() > 0) {
            lrcTextView.setmLrcList(lrcList);
            //切换带动画显示歌词
            lrcTextView.setAnimation(AnimationUtils.loadAnimation(PlayerService.this, R.anim.alpha_z));
            LrcRunnable lrcRunnable=new LrcRunnable();
            lrcRunnable.setLrcTextView(lrcTextView);
            uiHandle.post(lrcRunnable);
        }
    }

    /**
     * 自定义线程，负责歌词显示的刷新
     */
    private class LrcRunnable implements Runnable{
        private LrcTextView lrcTextView;

        public void setLrcTextView(LrcTextView lrcTextView) {
            this.lrcTextView = lrcTextView;
        }

        @Override
        public void run() {
            lrcTextView.setIndex(lrcIndex());
            lrcTextView.invalidate();
            uiHandle.postDelayed(this, 100);
        }
    }


    /**
     * 根据时间获取歌词显示的索引值
     *
     * @return
     */
    public int lrcIndex() {
        if (mediaPlayer.isPlaying()) {
            currentTime = mediaPlayer.getCurrentPosition();
            duration = mediaPlayer.getDuration();
        }
        if (currentTime < duration) {
            for (int i = 0; i < lrcList.size(); i++) {
                if (i < lrcList.size() - 1) {
                    if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
                        index = i;
                    }
                    if (currentTime > lrcList.get(i).getLrcTime()
                            && currentTime < lrcList.get(i + 1).getLrcTime()) {
                        index = i;
                    }
                }
                if (i == lrcList.size() - 1
                        && currentTime > lrcList.get(i).getLrcTime()) {
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * 获取当前音乐播放进度
     */
    public long getCurrentProgress() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        return 0;
    }

    /**
     * 获取当前曲目
     */
    public Mp3Info getlocalmusic() {
        return mp3Infos.get(position);
    }

    /**
     * 获取当前播放器状态
     */
    public Player_Status getPlayer_status() {
        return player_status;
    }

    /**
     * 设置播放器状态：随机
     */
    public void setRandom() {
        if (player_status.getRandom_status() == AppConstant.Status.status_random)
            player_status.setRandom_status(AppConstant.Status.status_random_none);
        else player_status.setRandom_status(AppConstant.Status.status_random);
    }

    /**
     * 设置播放器状态：重复
     */
    public void setRepeat() {
        switch (player_status.getRepeat_status()) {
            case AppConstant.Status.status_repeat:
                player_status.setRepeat_status(AppConstant.Status.status_repeat_onlyone);
                break;
            case AppConstant.Status.status_repeat_onlyone:
                player_status.setRepeat_status(AppConstant.Status.status_repeat_none);
                break;
            case AppConstant.Status.status_repeat_none:
                player_status.setRepeat_status(AppConstant.Status.status_repeat);
        }
    }

    /**
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     */
    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int position;

        public PreparedListener(int positon) {
            this.position = positon;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();    //开始播放
            if (position > 0) {    //如果音乐不是从头播放
                mediaPlayer.seekTo(position);
            }
        }
    }

    /**
     * 消息发送机制：更新操作
     */
    private void msgSend_Update() {
        Message message = new Message();
        message.what = AppConstant.ActionTypes.UPDATE_ACTION;
        Message message_ui = new Message();
        message_ui.what = AppConstant.ActionTypes.UPDATE_ACTION;
        try {
            if (mainHandle != null) mainHandle.sendMessage(message);
            if (uiHandle != null) uiHandle.sendMessage(message_ui);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息发送机制：播放完成
     */
    private void msgSend_Complete() {
        Message message = new Message();
        message.what = AppConstant.ActionTypes.COMPLETE_ACTION;
        try {
            if (uiHandle != null) uiHandle.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现一个OnCompletionListener接口,当音乐播放完成时执行操作
     */
    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next();
        }

    }

    /**
     * 实现一个延时线程，每隔一段时间发消息更新播放进度
     */
    public class DelayThread extends Thread {
        long delaytime;

        public DelayThread(long i) {
            delaytime = i;
        }

        public void run() {
            while (true) {
                try {
                    sleep(delaytime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.i("delaythread-->", "" + uiHandle);
                try {
                    if (player_status.isPlay()) {
                        if (uiHandle != null) {
                            Message message = new Message();
                            message.what = AppConstant.ActionTypes.MUSIC_CURRENT;
                            uiHandle.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
