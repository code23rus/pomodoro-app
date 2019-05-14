package ru.code23rus.pomodoro;

import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    private final Image idleIcon = new Image(getClass().getResourceAsStream("/icons/idle.png"));
    private final Image workIcon = new Image(getClass().getResourceAsStream("/icons/work.png"));

    @FXML
    private Label labelStatus;

    @FXML
    private Button buttonWork;

    @FXML
    ProgressBar progressBar;

    private boolean inWork;
    private Timer timer;
    private Stage stage;

    private final SoundPlayer player = new SoundPlayer();

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
        doIdle();
    }

    private void doWork() {
        cancelTimers();
        inWork = true;
        stage.getIcons().setAll(workIcon);
        buttonWork.setText("Остановить");
        player.playWorkStarted();

        timer = new Timer("Timer");
        TimerTask task = new TimerTask() {
            private final long timeStart = System.currentTimeMillis();
            private final int workPeriodInSeconds = 25 * 60;

            @Override
            public void run() {
                long seconds = (System.currentTimeMillis() - timeStart) / 1000;
                if (inWork && seconds >= workPeriodInSeconds) {
                    cancel();
                    doIdle();
                    return;
                }
                LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                double progress = (double) seconds / (double) workPeriodInSeconds;
                Platform.runLater(() -> {
                    labelStatus.setText("Работаем: " + timeOfDay.toString());
                    progressBar.setProgress(progress);
                });

            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void doIdle() {
        cancelTimers();
        inWork = false;
        if (stage != null) {
            Platform.runLater(() -> stage.getIcons().setAll(idleIcon));
            player.playWorkFinished();
        }
        progressBar.setProgress(0);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            long timeStart = System.currentTimeMillis();

            @Override
            public void run() {
                long seconds = (System.currentTimeMillis() - timeStart) / 1000;
                LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
                Platform.runLater(() -> labelStatus.setText("Отдыхаем: " + timeOfDay.toString()));
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
        this.stage = stage;
    }
}
