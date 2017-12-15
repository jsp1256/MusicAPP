package tk.xiangjianpeng.musicapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends CheckPermissionsActivity {
    private SimpleAdapter adapter;      //简单适配器
    private ListView MusicList;         // 音乐列表
    private List<Mp3Info> mp3Infos = null;
    private TextView musicArtist;        //艺术家
    private TextView musicTitle;        //歌曲标题

    private PlayServiceConnection playServiceConnection;//服务连接所需
    private PlayerService.PlayBinder playBinder;        //连接成功返回的自定义iBinder对象
    private MainHandle mainHandle;                    //消息通信

    private RelativeLayout singleSong_layout;   //歌曲显示栏
    private int listPosition = 0;               //标识当前列表位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MusicList = (ListView) findViewById(R.id.music_list);
        MusicList.setOnItemClickListener(new MusicListItemClickListener());
        init(); //控件的初始化，设置监听器
        ServiceBind();//绑定服务
    }

    private void init() {
        musicArtist = (TextView) findViewById(R.id.music_Artist_tv);
        musicTitle = (TextView) findViewById(R.id.music_Title_tv);
        singleSong_layout = (RelativeLayout) findViewById(R.id.singleSong_layout);
        singleSong_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UiActivity.class);
                startActivity(intent);
            }
        });
        mainHandle = new MainHandle();
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
            playBinder.callsetMainHandle(mainHandle);
            playBinder.callupdate();
            if (playBinder.callgetPlayer_status().isPlay()) {
                setMenuInfo(playBinder.callgetlocalmusic());
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /**
     * ListView条目点击监听器
     */
    private class MusicListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (mp3Infos != null) {
                Mp3Info mp3Info = mp3Infos.get(position);
                listPosition = position;
                Log.d("mp3Info-->", mp3Info.toString());
                setMenuInfo(mp3Info);
                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
                intent.setClass(MainActivity.this, PlayerService.class);
                startService(intent);
            }
        }
    }

    /**
     * 设置下部菜单栏显示文本
     *
     * @param mp3Info
     */
    private void setMenuInfo(Mp3Info mp3Info) {
        musicArtist.setText(mp3Info.getArtist());
        musicTitle.setText(mp3Info.getTitle());
    }

    /**
     * 填充列表(List<HashMap<String, String>>)
     */
    public void setListAdpter(List<HashMap<String, String>> mp3list) {
        adapter = new SimpleAdapter(this, mp3list,
                R.layout.music_list_layout, new String[]{"title",
                "Artist", "duration"}, new int[]{R.id.music_title,
                R.id.music_Artist, R.id.music_duration});
        MusicList.setAdapter(adapter);
    }

/*  //退出程序的对话框
    public boolean onKeyDown(int jspkeyCode, KeyEvent event) {
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

    public class MainHandle extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == AppConstant.ActionTypes.UPDATE_ACTION) {
                try {
                    Mp3Info mp3Info = playBinder.callgetlocalmusic();
                    listPosition = playBinder.callgetposition();
                    setMenuInfo(mp3Info);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "好像出了点错误哦！", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mp3Infos = MediaUtils.getMp3Infos(getApplicationContext());   //获取歌曲对象集合
            setListAdpter(MediaUtils.getMusicMaps(mp3Infos));             //显示歌曲列表
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        ServiceUnbind();
        // Intent intent=new Intent(this,PlayerService.class);
        //stopService(intent);
        super.onDestroy();
    }
}
