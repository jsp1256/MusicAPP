package tk.xiangjianpeng.musicapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import tk.xiangjianpeng.musicapp.MediaUtils;
import tk.xiangjianpeng.musicapp.R;
import tk.xiangjianpeng.musicapp.UiActivity;

/**
 * Created by user on 2017/12/19.
 */

public class MusicListFragment extends Fragment {
    Context MenuActivity;
    private ListView listView;
    private SimpleAdapter adapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MenuActivity=this.getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_menulist,container,false);
        listView=(ListView) view.findViewById(R.id.menulist_item);
        setListAdpter();
        listView.setOnItemClickListener(new MenuListItemClickListener());
        return view;
    }
    /**
     * 填充列表(List<HashMap<String, String>>)
     */
    public void setListAdpter() {
        adapter = new SimpleAdapter(this.getContext(), MediaUtils.getMenuListMaps(),
                R.layout.menu_list_layout, new String[]{"title"}, new int[]{R.id.menu_title});
        listView.setAdapter(adapter);
    }

    private class MenuListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(i==0){
                Intent intent = new Intent(MenuActivity, UiActivity.class);
                startActivity(intent);
            }
        }
    }
}
