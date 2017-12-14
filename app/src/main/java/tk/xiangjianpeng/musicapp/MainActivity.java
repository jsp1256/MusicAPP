package tk.xiangjianpeng.musicapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends CheckPermissionsActivity {
    private int repeatState;                        //循环标识
    private boolean isFirstTime = true;             // 是否是第一次
    private boolean isPlaying;                      // 正在播放
    private boolean isPause;                        // 暂停
    private boolean isNoneShuffle = true;           // 顺序播放
    private boolean isShuffle = false;              // 随机播放

    private SimpleAdapter adapter;      //简单适配器
    private ListView MusicList;         // 音乐列表
    private List<Mp3Info> mp3Infos = null;
    private TextView musicArtist;        //艺术家
    private TextView musicTitle;        //歌曲标题

    private PlayerService.PlayBinder playBinder;

    private RelativeLayout singleSong_layout; //歌曲显示栏

    private int listPosition = 0;   //标识列表位置
    private MainReceiver mainReceiver;  //自定义的广播接收器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MusicList = (ListView) findViewById(R.id.music_list);
        MusicList.setOnItemClickListener(new MusicListItemClickListener());
        mp3Infos=MediaUtils.getMp3Infos(getApplicationContext());   //获取歌曲对象集合
        setListAdpter(MediaUtils.getMusicMaps(mp3Infos));             //显示歌曲列表
        init(); //控件的初始化，设置监听器
    }

    private void init() {
        musicArtist =(TextView) findViewById(R.id.music_Artist_tv);
        musicTitle = (TextView) findViewById(R.id.music_Title_tv);
        singleSong_layout = (RelativeLayout) findViewById(R.id.singleSong_layout);
        singleSong_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,UiActivity.class);
                startActivity(intent);
            }
        });

    }
    //bind服务绑定
    public void ServiceBind(View view){

    }

    private class PlayServiceConnection implements ServiceConnection{
        public void onServiceConnected(ComponentName name, IBinder PlayBind){
            playBinder= (PlayerService.PlayBinder) PlayBind;
        }
        public void onServiceDisconnected(ComponentName name){}
    }
    /**
     * ListView条目点击监听器
     */
    private class MusicListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (mp3Infos != null) {
                Mp3Info mp3Info = mp3Infos.get(position);
                Log.d("mp3Info-->", mp3Info.toString());
                musicArtist.setText(mp3Info.getArtist());
                musicTitle.setText(mp3Info.getTitle());
                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
                intent.setClass(MainActivity.this, PlayerService.class);
                startService(intent);
            }
        }
    }
    /**
     * 填充列表(List<HashMap<String, String>>)
     */
    public void setListAdpter(List<HashMap<String, String>> mp3list) {
        adapter = new SimpleAdapter(this, mp3list,
                R.layout.music_list_layout, new String[] { "title",
                "Artist", "duration" }, new int[] { R.id.music_title,
                R.id.music_Artist, R.id.music_duration });
        MusicList.setAdapter(adapter);
    }

    //自定义的BroadcastReceiver，负责监听从Service传回来的广播
    public class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(AppConstant.ActionTypes.UPDATE_ACTION)) {
                //获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                if(listPosition >= 0) {
                    musicTitle.setText(mp3Infos.get(listPosition).getTitle());
                }
            }
        }
    }
/*  //退出程序的对话框
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            new AlertDialog.Builder(this)
                    .setTitle("退出")
                    .setMessage("您确定要退出？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                    Intent intent = new Intent(
                                            MainActivity.this,
                                            PlayerService.class);
                                    unregisterReceiver(mainReceiver);
                                    stopService(intent); // 停止后台服务
                                }
                            }).show();

        }
        return super.onKeyDown(keyCode, event);
    }
*/
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
