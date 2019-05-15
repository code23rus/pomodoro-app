package ru.code23rus.pomodoro;

import java.net.URL;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    @FXML
    private Label labelStatus;

    @FXML
    private Button buttonWork;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private CheckBox cbPlaySounds;

    @FXML
    private Canvas canvasActivity;

    private boolean inWork;
    private Timer timer;
    private IconChanger iconChanger;
    private final SoundPlayer player = new SoundPlayer();
    private boolean playSounds = true;
    private Storage storage;
    private Pomodoro activePom;
    private ActivityChart activityChart;

    @FXML
    private void workAction(ActionEvent event) {
        if (!inWork) {
            doWork();
        } else {
            doIdle();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        AnchorPane.setLeftAnchor(progressBar, 14d);
        AnchorPane.setRightAnchor(progressBar, 14d);

        AnchorPane.setLeftAnchor(labelStatus, 20d);
        AnchorPane.setRightAnchor(labelStatus, 20d);
        labelStatus.setAlignment(Pos.CENTER);

        // play sounds checkbox change value listener
        cbPlaySounds.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            playSounds = newValue;
        });

        storage = new Storage();
        activityChart = new ActivityChart(canvasActivity);
        activityChart.draw(getCurrentDayPoms());

        doIdle();
    }

    private void doWork() {
        cancelTimers();
        inWork = true;
        setWorkIcon();
        buttonWork.setText("Остановить");
        if (playSounds) {
            player.playWorkStarted();
        }

        activePom = storage.create();

        timer = new Timer("Timer");
        TimerTask task = new TimerTask() {
            private final long timeStart = System.currentTimeMillis();
            private final int workPeriodInSeconds = 25 * 60;
            private long lastSoundTime = 0;

            @Override
            public void run() {
                storage.update(activePom);

                long timeCurrent = System.currentTimeMillis();
                long seconds = (timeCurrent - timeStart) / 1000;
                if (seconds > workPeriodInSeconds) { // time is up
                    if (timeCurrent - lastSoundTime >= 30000) { // play sound to attract attention (not every run)
                        if (playSounds) {
                            player.playWorkFinished();
                        }
                        lastSoundTime = timeCurrent;
                    }

                    updateFinishedIcon(); // change the icon to attract attention
                }
                LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                double progress = (double) seconds / (double) workPeriodInSeconds;
                Platform.runLater(() -> {
                    labelStatus.setText("Работаем: " + timeOfDay.toString());
                    progressBar.setProgress(progress);
                    activityChart.draw(getCurrentDayPoms());
                });

            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void doIdle() {
        cancelTimers();
        inWork = false;
        setIdleIcon();
        progressBar.setProgress(0);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            long timeStart = System.currentTimeMillis();

            @Override
            public void run() {
                long seconds = (System.currentTimeMillis() - timeStart) / 1000;
                LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                Platform.runLater(() -> {
                    labelStatus.setText("Отдыхаем: " + timeOfDay.toString());
                    activityChart.draw(getCurrentDayPoms());
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
        Platform.runLater(() -> buttonWork.setText("Работать"));
    }

    private void cancelTimers() {
        if (timer != null) {
            timer.cancel();
        }
    }

    void setStage(Stage stage) {
        iconChanger = new IconChanger(stage);
    }

    private void updateFinishedIcon() {
        if (iconChanger != null) {
            Platform.runLater(() -> iconChanger.updateFinishedIcon());
        }
    }

    private void setWorkIcon() {
        if (iconChanger != null) {
            Platform.runLater(() -> iconChanger.setWorkIcon());
        }
    }

    private void setIdleIcon() {
        if (iconChanger != null) {
            Platform.runLater(() -> iconChanger.setIdleIcon());
        }
    }

    private List<Pomodoro> getCurrentDayPoms() {
        return storage.findInPeriod(
                Date.from(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()),
                Date.from(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1).toInstant()));
    }

}
