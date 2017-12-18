package tk.xiangjianpeng.musicapp;

/**
 * @author xiangjianpeng
 * 音乐属性实体类
 */

public class Mp3Info {
    private long id;        //歌曲id
    private String title;   //歌曲标题
    private String artist;  //艺术家
    private long duration;  //时长
    private long size;      //文件大小
    private String url;     //文件路径

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
