package tk.xiangjianpeng.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private String path;
    private boolean isPause;

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        if(mediaPlayer.isPlaying()){
            stop();
        }
        path=intent.getStringExtra("url");
        int msg=intent.getIntExtra("MSG",0);
        switch (msg){
            case AppConstant.PlayerMsg.PLAY_MSG:
                play(0);
                break;
            case AppConstant.PlayerMsg.STOP_MSG:
                stop();
                break;
            case AppConstant.PlayerMsg.PAUSE_MSG:
                pause();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 播放音乐
     * @param position
     */
    private void play(int position) {
        try {
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
    private void stop(){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    /**
     *
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     *
     */
    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int position;

        public PreparedListener(int positon) {
            this.position = positon;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();    //开始播放
            if(position > 0) {    //如果音乐不是从头播放
                mediaPlayer.seekTo(position);
            }
        }
    }
}
