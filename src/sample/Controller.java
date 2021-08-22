package sample;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    
    public Button playBtn;
    public Button nextBtn;
    public Button previous;
    public ComboBox<String> speedSelector;
    public Slider volumeSlider;
    public AnchorPane root;
    public Label songTitle;
    public ImageView SongCoverView;
    public Button restartBtn;
    public ImageView playIcon;
    public ImageView previousIcon;
    public ImageView nextIcon;
    public Button shuffleBtn;
    public ImageView shuffle;
    public ImageView volumeIcon;
    private Media media;
    private MediaPlayer mediaPlayer;
    public ProgressBar songProgress;
    private File[] songFiles;
    private File songDirectory;
    private ArrayList<File> playList;
    private int trackNumber;

    private int[] songSpeeds={25,50,75,100,125,200,300};
    private boolean inPlay;
    private Timer timer;
    private TimerTask task;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {



            Icons();
        try {
            SetUp();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void SetUp() throws MalformedURLException {
        playList=new ArrayList<>();
        File file=new File("resources/songs");
        songDirectory=file;
        songFiles=songDirectory.listFiles();
        songProgress.setStyle("-fx-accent:#02d9b6");
        if(songFiles!=null)
        {
            for(File current:songFiles)
            {
                playList.add(current);
               // System.out.println(current);
            }
        }

        String url=playList.get(trackNumber).toURI().toURL().toString();
        media=new Media(url);
        mediaPlayer=new MediaPlayer(media);
        songTitle.setText(playList.get(trackNumber).getName());

        for(int i=0;i<songSpeeds.length;i++){
            speedSelector.getItems().add(songSpeeds[i]+"%");
        }
        speedSelector.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
            }
        });
    }

    private void Icons()
    {
        SongCoverView.setImage(new Image("/images/icon.jpeg"));
        playIcon.setImage(new Image("/images/play.png"));
        previousIcon.setImage(new Image("/images/previous.png"));
        nextIcon.setImage(new Image("/images/next.png"));
        shuffle.setImage(new Image("/images/shuffle.png"));
        volumeIcon.setImage(new Image("/images/volume.png"));
    }

    public void play(ActionEvent actionEvent)
    {
        mediaPlayer.setVolume(volumeSlider.getValue()*0.01);

        if(inPlay)
        {
            pause(actionEvent);
            playIcon.setImage(new Image("/images/play.png"));
            return;
        }
        StarTimer();
        mediaPlayer.play();
        inPlay=true;
        playIcon.setImage(new Image("/images/pause.png"));
    }

    public void pause(ActionEvent actionEvent)
    {
        StopTimer();
        mediaPlayer.pause();
        inPlay=false;
    }
    public void next(ActionEvent actionEvent) throws MalformedURLException {
        if(trackNumber<0||trackNumber>=playList.size())
            return;
        else
            if(trackNumber==playList.size()-1)
                trackNumber=0;
            else
             trackNumber++;

        if(inPlay)
        {
            StopTimer();
            mediaPlayer.stop();
        }
        String url=playList.get(trackNumber).toURI().toURL().toString();
        media=new Media(url);
        mediaPlayer=new MediaPlayer(media);
        songTitle.setText(playList.get(trackNumber).getName());
        mediaPlayer.play();
    }

    public void previous(ActionEvent actionEvent) throws MalformedURLException {
        trackNumber--;
        if(trackNumber<0)
            trackNumber=playList.size()-1; /**play the last one*/

            if(inPlay)
                StopTimer();
        mediaPlayer.stop();
        String url=playList.get(trackNumber).toURI().toURL().toString();
        media=new Media(url);
        mediaPlayer=new MediaPlayer(media);
        songTitle.setText(playList.get(trackNumber).getName());
        play(actionEvent);
    }
    public void changeSpeed(ActionEvent actionEvent)
    {
        //mediaPlayer.setRate(Integer.parseInt(speedSelector.getValue())*0.01);
        String sub=speedSelector.getValue().substring(0,speedSelector.getValue().length()-1);
        mediaPlayer.setRate(Integer.parseInt(sub)*0.01);
    }
    public void StarTimer()
    {
        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                inPlay=true;
                double current=mediaPlayer.getCurrentTime().toSeconds();
                double end=media.getDuration().toSeconds();
                songProgress.setProgress(current/end);
                if(current/end==1)
                    StopTimer();
            }
        };
        timer.scheduleAtFixedRate(task,1000,1000);

    }
    public void StopTimer()
    {
        timer.cancel();
    }

    public void restart(ActionEvent actionEvent)
    {
        songProgress.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0.0));
    }
    public void shuffleSong(ActionEvent actionEvent) throws MalformedURLException {
        if(inPlay)
            StopTimer();

        mediaPlayer.stop();

        Random rand=new Random();
        trackNumber=rand.nextInt(playList.size()); //randomize track number
        String url=playList.get(trackNumber).toURI().toURL().toString();
        media=new Media(url);
        mediaPlayer=new MediaPlayer(media);
        songTitle.setText(playList.get(trackNumber).getName());
        mediaPlayer.play();
    }
}
