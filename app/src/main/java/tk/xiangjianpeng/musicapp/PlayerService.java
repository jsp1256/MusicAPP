package tk.xiangjianpeng.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Player_Status player_status;
    private List<Mp3Info> mp3Infos;
    private int position;                           //当前歌曲位置
    private MainActivity.MainHandle mainHandle;   //主界面消息通信
    private UiActivity.UiHandle uiHandle;           //UI界面消息通信
    DelayThread delayThread = new DelayThread(200);

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
            previous();
        }

        //下一首
        protected void callnext() {
            next();
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
                Toast.makeText(this, "播放器错误，找不到文件！", Toast.LENGTH_SHORT).show();
            }
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
        if(!player_status.isPlayed()) return;
        if (position == 0) {
            position = mp3Infos.size();
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
        if(!player_status.isPlayed()) return;
        if (position >= mp3Infos.size() - 1) {
            position = 0;
        } else {
            position++;
        }
        play(position);
        msgSend_Update();
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
    private void msgSend_Update(){
        Message message = new Message();
        message.what = AppConstant.ActionTypes.UPDATE_ACTION;
        try {
            if (mainHandle != null) mainHandle.sendMessage(message);
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
