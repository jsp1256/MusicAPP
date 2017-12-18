package tk.xiangjianpeng.musicapp;

/**
 * @author xiangjianpeng
 * 播放器状态实体类
 */

public class Player_Status {
    private int random_status;
    private int repeat_status;
    private boolean isPlay;
    private boolean isPause;
    private boolean isPlayed;

    public Player_Status(int random_status, int repeat_status, boolean isPlay, boolean isPause, boolean isPlayed) {
        this.random_status = random_status;
        this.repeat_status = repeat_status;
        this.isPlay = isPlay;
        this.isPause = isPause;
        this.isPlayed = isPlayed;
    }

    public int getRandom_status() {
        return random_status;
    }

    public int getRepeat_status() {
        return repeat_status;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setRandom_status(int random_status) {
        this.random_status = random_status;
    }

    public void setRepeat_status(int repeat_status) {
        this.repeat_status = repeat_status;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }
}
