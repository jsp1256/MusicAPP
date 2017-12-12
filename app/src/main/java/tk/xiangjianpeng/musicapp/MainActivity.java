package tk.xiangjianpeng.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int stautus_repeat_none = 0;
    final int stautus_repeat = 1;
    final int stautus_repeat_onlyone = 2;

    SimpleAdapter adapter;
    ListView MusicList;
    List<Mp3Info> mp3Infos;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MusicList = (ListView) findViewById(R.id.music_list);
        MusicList.setOnItemClickListener(new MusicListItemClickListener());
    }
    private class MusicListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(mp3Infos != null){
                Mp3Info mp3Info=mp3Infos.get(position);
                Log.d("mp3Info-->",mp3Info.toString());
                Intent intent=new Intent();
                intent.putExtra("url",mp3Info.getUrl());
                intent.putExtra("MSG",AppConstant.PlayerMsg.PLAY_MSG);
                intent.setClass(MainActivity.this,PlayerService.class);
                startService(intent);
            }
        }
    }

    /**
     * 填充列表
     *
     * @param mp3Infos
     */
    public void setListAdpter(List<Mp3Info> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", mp3Info.getTitle());
            map.put("Artist", mp3Info.getArtist());
            map.put("duration", String.valueOf(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        adapter = new SimpleAdapter(this, mp3list,
                R.layout.music_list_layout, new String[]{"title", "Artist", "duration"},
                new int[]{R.id.music_title, R.id.music_Artist, R.id.music_duration});
        MusicList.setAdapter(adapter);
    }


}
