package ru.code23rus.pomodoro;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Changes the application icon
 */
public class IconChanger {

    private final Image idleIcon = new Image(getClass().getResourceAsStream("/icons/idle.png"));
    private final Image workIcon = new Image(getClass().getResourceAsStream("/icons/work.png"));
    private final Image finishedFirst = new Image(getClass().getResourceAsStream("/icons/work-finished-first.png"));
    private final Image finishedSecond = new Image(getClass().getResourceAsStream("/icons/work-finished-second.png"));

    private final Stage stage;
    private boolean first;

    public IconChanger(Stage stage) {
        this.stage = stage;
    }

    public void setIdleIcon() {
        stage.getIcons().setAll(idleIcon);
    }

    public void setWorkIcon() {
        stage.getIcons().setAll(workIcon);
    }

    public void updateFinishedIcon() {
        first = !first;
        stage.getIcons().setAll(first ? finishedFirst : finishedSecond);
    }

}
