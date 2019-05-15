package ru.code23rus.pomodoro;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Draw current day activity chart
 */
public class ActivityChart {

    private final Canvas canvas;
    private List<PomTooltip> tooltips;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    private Tooltip tooltip;

    public ActivityChart(Canvas canvas) {
        this.canvas = canvas;
        tooltip = new Tooltip();
        tooltip.setAutoHide(true);
        Tooltip.install(canvas, tooltip);

        canvas.setOnMouseMoved(e -> tooltip.setText(getTooltipText(e)));
        canvas.setOnMousePressed(e -> {
            tooltip.setText(getTooltipText(e));
            tooltip.show(canvas, e.getScreenX(), e.getScreenY());
        });
        canvas.setOnMouseExited(e -> tooltip.hide());
    }

    private String getTooltipText(MouseEvent event) {
        return tooltips.stream()
                .filter(e -> event.getX() >= e.minX && event.getX() <= e.maxX)
                .map(e -> "Работа\n"
                        + timeFormat.format(e.pomodoro.getStarted())
                        + " - "
                        + timeFormat.format(e.pomodoro.getFinished()))
                .findAny().orElse("Отдых");
    }

    public void draw(List<Pomodoro> pomodoros) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();

        tooltips = new ArrayList<>();

        // draw background
        Stop[] stops = new Stop[]{new Stop(0, Color.rgb(148, 232, 133)), new Stop(1, Color.rgb(90, 160, 64))};
        LinearGradient gradient = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE, stops);
        g.setFill(gradient);
        g.fillRoundRect(0, 0, width, canvas.getHeight(), 5, 5);

        if (pomodoros.isEmpty()) {
            return;
        }

        Pomodoro min = pomodoros.stream().min((a, b) -> a.compareTo(b)).orElse(null);

        long from = min.getStarted().getTime();
        long to = System.currentTimeMillis();
        long dayStart = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli();
        long scale = to - from;
        if (scale <= 0) {
            return;
        }
        long delta = Math.min(scale / 5, Math.max(from - dayStart, 0)); // increasing scale for beauty draw )

        pomodoros.stream().sorted().forEach(p -> {
            drawPomodoro(p, scale + delta, from - delta);
        });
    }

    private void drawPomodoro(Pomodoro p, long scale, long offset) {
        double width = canvas.getWidth();

        double x = (double) (p.getStarted().getTime() - offset) / (double) scale;
        double w = (double) p.getDuration() / (double) scale;

        double canvasX1 = Math.max(Math.ceil(x * width), 0);
        double canvasWidth = Math.ceil(w * width);

        tooltips.add(new PomTooltip(canvasX1, canvasX1 + canvasWidth, p));

        GraphicsContext g = canvas.getGraphicsContext2D();
        Stop[] stops = new Stop[]{new Stop(0, Color.rgb(240, 100, 100)), new Stop(1, Color.rgb(200, 50, 50))};
        LinearGradient gradient = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE, stops);
        g.setFill(gradient);
        g.fillRoundRect(canvasX1, 0, canvasWidth, canvas.getHeight(), 5, 5);
    }

    static class PomTooltip {

        double minX;
        double maxX;
        Pomodoro pomodoro;

        public PomTooltip(double minX, double maxX, Pomodoro pomodoro) {
            this.minX = minX;
            this.maxX = maxX;
            this.pomodoro = pomodoro;
        }
    }

}
