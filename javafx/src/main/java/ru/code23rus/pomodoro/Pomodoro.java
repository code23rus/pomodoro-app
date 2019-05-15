package ru.code23rus.pomodoro;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Pomodoro work interval
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pomodoro implements Comparable<Pomodoro> {

    @Getter @Setter
    private Date started;

    @Getter @Setter
    private Date finished;

    @Override
    public int compareTo(Pomodoro o) {
        return started.compareTo(o.getStarted());
    }

    /**
     * @return duration in ms
     */
    public long getDuration() {
        return Math.max(finished.getTime() - started.getTime(), 0);
    }
}
