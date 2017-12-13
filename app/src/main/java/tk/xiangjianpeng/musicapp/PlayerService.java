package tk.xiangjianpeng.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPause;
    private List<Mp3Info> mp3Infos;
    private int position;               //当前歌曲位置
    private boolean isPlaying=false;    //当前播放器状态

    public PlayerService() {
    }

    public class PlayBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public void onCreate(){
        mp3Infos=MediaUtils.getMp3Infos(this);   //获取歌曲对象集合
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
            position = intent.getIntExtra("position",0);
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
/*
    private class ListenerRunable implements Runnable{

        @Override
        public void run() {
            while (isPlaying){
                if(!mediaPlayer.isPlaying()){next();Log.i("runablenext","自动播放下一曲");}
                Log.i("runablemsg","监听运行");
                SystemClock.sleep(200);
            }
        }
    }
*/
    /**
     * 播放音乐
     *
     * @param position
     */
    private void play(int position) {
        try {
            Mp3Info mp3Info=mp3Infos.get(position);
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(mp3Info.getUrl());
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
            mediaPlayer.setOnCompletionListener(new CompletionListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下一曲
     *
     * 当前播放的位置
     */
    public void next() {
        if (position >= mp3Infos.size() - 1) {
             play(0);
        }
        position++;
        play(position);
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 停止音乐
     */
    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying=false;
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前音乐播放进度
     */
    public long getCurrentProgress() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        return 0;
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
    private final class CompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next();
        }
    }
}
