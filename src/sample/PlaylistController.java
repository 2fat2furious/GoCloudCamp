package sample;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.MediaPlayer;

import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    public TableColumn<SongEntity, String> durationSongColumn;
    @FXML
    public ProgressBar songProgressBar;

    private Stage translatorStage;
    private final FileChooser fileChooser = new FileChooser();
    private Timer timer;
    private SimpleBooleanProperty isRun = new SimpleBooleanProperty(false);
    private LinkedList<SongEntity> songsList;
    private Media media;
    private MediaPlayer mediaPlayer;
    private int songNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        songsList = new LinkedList<>();
        File directory = new File("src/sample/songs");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 File (*.mp3)", "*.mp3");
        fileChooser.setInitialDirectory(directory);
        fileChooser.getExtensionFilters().add(extFilter);
        prevBtn.setDisable(true);
        playBtn.setDisable(true);
        nextBtn.setDisable(true);
        pauseBtn.setDisable(true);
    }

    public void initSong() {
        if (songsList.size() != 0) {
            media = new Media(songsList.get(songNumber).getSong().toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            currentSongNameLbl.setText(songsList.get(songNumber).getName());
            songsTable.getSelectionModel().select(songNumber);
        }
    }

    public void play() {
        beginTimer();
        setDisablePlayPauseBtn(true);
        mediaPlayer.play();
    }

    public void pause() {
        setDisablePlayPauseBtn(false);
        mediaPlayer.pause();
    }

    public void addSong() {
        File selectedFile = fileChooser.showOpenDialog(translatorStage);
        if (selectedFile != null) {
            Media currentSong = new Media(selectedFile.toURI().toString());

            mediaPlayer = new MediaPlayer(currentSong);
            mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == MediaPlayer.Status.READY) {
                    songsList.add(new SongEntity(selectedFile.getName(), currentSong.getDuration().toMillis(), selectedFile));
                    songsTable.setItems(FXCollections.observableArrayList(songsList));
                    songsTable.getSelectionModel().selectFirst();
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        initSong();
        if (songsList != null && !isRun.get()) {
            prevBtn.setDisable(false);
            playBtn.setDisable(false);
            nextBtn.setDisable(false);
        }
    }

    public void next() {
        if (songNumber < songsList.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        if (isRun.get()) cancelTimer();
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
        if (isRun.get()) cancelTimer();
        mediaPlayer.stop();
        initSong();
        play();
    }

    public void beginTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                isRun.set(true);
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current / end);

                if (current / end == 1) cancelTimer();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 100);
    }

    public void cancelTimer() {
        isRun.set(false);
        timer.cancel();
    }

    public void initTable() {
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        durationSongColumn.setCellValueFactory(new PropertyValueFactory<>("Duration"));
    }

    public void setDisablePlayPauseBtn(boolean sign) {
        isRun.set(sign);
        pauseBtn.setDisable(!sign);
        playBtn.setDisable(sign);
    }

}
