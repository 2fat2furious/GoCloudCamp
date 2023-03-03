package sample;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class SongEntity {

    private String name;
    private String duration;
    private File song;

    public File getSong() {
        return song;
    }

    public void setSong(File song) {
        this.song = song;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public SongEntity(String name, double duration, File song) {
        this.name = name;
        this.duration = String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) duration),
                TimeUnit.MILLISECONDS.toSeconds((long) duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) duration)));
        this.song = song;
    }
}
