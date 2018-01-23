package tk.xiangjianpeng.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/12/21.
 */

public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(Context context) {
        super(context, "musicplayer.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE history(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,artist TEXT,duration INTEGER,url TEXT,last_time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * 数据库管理插入实体类，负责插入新增的历史记录到数据库中
     *
     * @param id
     * @param title
     * @param artist
     * @param duration
     * @param url
     * @param last_time
     */
    public void insert(long id, String title, String artist, long duration, String url, long last_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConstant.HistoryDB.ID, id);
        values.put(AppConstant.HistoryDB.TITLE, title);
        values.put(AppConstant.HistoryDB.ARTIST, artist);
        values.put(AppConstant.HistoryDB.DURATION, duration);
        values.put(AppConstant.HistoryDB.URL, url);
        values.put(AppConstant.HistoryDB.LAST_TIME, last_time);
        //rid：新增行的行id值
        long rid = db.insert("history", null, values);
        db.close();
    }

    /**
     * 数据库管理更新实体类，负责在原有数据基础上更新新的播放记录
     * 主要修改上一次播放时间last_time
     * 通过传入歌曲id和曲名定位到指定位置修改，最大程度避免错误更新
     *
     * @param id
     * @param title
     * @param last_time
     */
    public void update(long id, String title, String last_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_time", last_time);
        //number：受到影响的行数
        int number = db.update("history", values, "id=? AND title=?", new String[]{id + "", title});
        db.close();
    }

    public void delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //number：受到影响的行数
        int number = db.delete("history", "id=? AND title=?", new String[]{id + ""});
        db.close();
    }

    public List<Mp3Info> find_history() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("history", null, null, null, null, null, null, null);
        List<Mp3Info> mp3Infos = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor
                    .getColumnIndex(AppConstant.HistoryDB.ID));               //音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(AppConstant.HistoryDB.TITLE)));            //音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(AppConstant.HistoryDB.ARTIST));            //艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(AppConstant.HistoryDB.DURATION));          //时长
            String url = cursor.getString(cursor
                    .getColumnIndex(AppConstant.HistoryDB.URL));              //文件路径
            long last_time = cursor.getLong(cursor
                    .getColumnIndex(AppConstant.HistoryDB.LAST_TIME));        //上一次的播放时间
            mp3Info.setId(id);
            mp3Info.setTitle(title);
            mp3Info.setArtist(artist);
            mp3Info.setSize(last_time);     //重映射：size==last_time
            mp3Info.setDuration(duration);
            mp3Info.setUrl(url);
            mp3Infos.add(mp3Info);
        }
        cursor.close();
        db.close();
        return mp3Infos;
    }
}
