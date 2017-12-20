package tk.xiangjianpeng.musicapp.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import tk.xiangjianpeng.musicapp.MediaUtils;
import tk.xiangjianpeng.musicapp.R;

/**
 * Created by user on 2017/12/19.
 */

public class MusicListFragment extends Fragment {
    private ListView listView;
    private SimpleAdapter adapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_menulist,container,false);
        listView=(ListView) view.findViewById(R.id.menulist_item);
        setListAdpter();

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
}
