package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.MediaPlayer;

import javafx.scene.media.Media;
import java.io.File;
import java.net.URL;
import java.util.*;

public class PlaylistController implements Initializable {

    @FXML
    public Label currentSongNameLbl;
    @FXML
    public Button playBtn;
    @FXML
    public Button prevBtn;
    @FXML
    public Button nextBtn;
    @FXML
    public Button addNewBtn;
    @FXML
    public Button pauseBtn;
    @FXML
    public TableView<SongEntity> songsTable;
    @FXML
    public TableColumn<SongEntity, String> songNameColumn;
    @FXML
    public TableColumn<SongEntity, Double> durationSongColumn;
    @FXML
    public ProgressBar songProgressBar;

    private Timer timer;
    private TimerTask task;
    private boolean isRun;
    private LinkedList<File> songsList;
    private Media media;
    private MediaPlayer mediaPlayer;
    private int songNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songsList = new LinkedList<>();
        File directory = new File("src/sample/songs");
        if (directory.listFiles() != null) {
            songsList.addAll(Arrays.asList(directory.listFiles()));
        }

        initSong();

    }

    public void play() {
        beginTimer();
        mediaPlayer.play();
    }

    public void pause() {
        cancelTimer();
        mediaPlayer.pause();
    }

    public void addSong() {

    }

    public void next() {
        if (songNumber < songsList.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        if (isRun) cancelTimer();
        mediaPlayer.stop();
        initSong();
        play();
    }

    public void prev() {
        if (songNumber > 0) {
            songNumber--;
        } else {
            songNumber = songsList.size() - 1;
        }
        if (isRun) cancelTimer();
        mediaPlayer.stop();
        initSong();
        play();
    }

    public void initSong() {
        media = new Media(songsList.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        currentSongNameLbl.setText(songsList.get(songNumber).getName());
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                isRun = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current / end);

                if (current/end == 1) cancelTimer();
            }
        };
        timer.scheduleAtFixedRate(task, 0,100);
    }

    public void cancelTimer() {
        isRun = false;
        timer.cancel();
    }

}
