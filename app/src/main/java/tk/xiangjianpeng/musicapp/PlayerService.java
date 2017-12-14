package tk.xiangjianpeng.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.List;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Player_Status player_status;
    private List<Mp3Info> mp3Infos;
    private int position;                           //当前歌曲位置
    private MainActivity.MainHandle mainHandle;   //主界面消息通信
    private UiActivity.UiHandle uiHandle;           //UI界面消息通信

    public PlayerService() {
    }

    /**
     * PlayBinder提供的通信
     */
    public class PlayBinder extends Binder {
        //获取当前服务
        public PlayerService getService() {
            return PlayerService.this;
        }

        //获取当前曲目
        public Mp3Info callgetlocalmusic() {
            return getlocalmusic();
        }

        //获取当前播放位置
        public int callgetposition() {
            return position;
        }

        //获取当前播放器状态
        public Player_Status callgetPlayer_status() {
            return getPlayer_status();
        }

        //设置主界面消息通信对象
        public void callsetMainHandle(MainActivity.MainHandle mainHandle1) {
            mainHandle = mainHandle1;
        }

        //设置UI界面消息通信对象
        public void callsetUiHandle(UiActivity.UiHandle uiHandle1) {
            uiHandle = uiHandle1;
        }

        //更新音乐列表
        public void callupdate() {
            update();
        }

        //下一首
        public void callnext() {
            next();
        }
    }

    public void onCreate() {
        player_status = new Player_Status(AppConstant.Status.status_random_none, AppConstant.Status.status_repeat, false, false, false);
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
                case AppConstant.PlayerMsg.PAUSE_MSG:
                    pause();
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
     * 播放音乐
     */
    private void play(int position) {
        if (player_status.isPause()) {
            player_status.setPause(false);
            player_status.setPlay(true);
            mediaPlayer.start();
        } else {
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
                e.printStackTrace();
            }
        }
    }

    /**
     * 下一曲
     */
    public void next() {
        if (position >= mp3Infos.size() - 1) {
            position = 0;
        } else {
            position++;
        }
        play(position);
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            player_status.setPlay(false);
            player_status.setPause(true);
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
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取随机位置
     *
     * @param end
     * @return
     */
    private int getRandomIndex(int end) {
        int index = (int) (Math.random() * end);
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

    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
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

    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next();
            Message message = new Message();
            message.what = AppConstant.ActionTypes.UPDATE_ACTION;
            if (mainHandle != null) mainHandle.sendMessage(message);
            if (uiHandle != null) uiHandle.sendMessage(message);
        }
    }
}
