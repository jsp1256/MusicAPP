package tk.xiangjianpeng.musicapp.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import tk.xiangjianpeng.musicapp.R;

/**
 * Created by user on 2017/12/20.
 */

public class HistoryListFragment extends Fragment {
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_historylist,container,false);
        listView=(ListView) view.findViewById(R.id.history_list);
        return view;
    }
}
