package ru.code23rus.pomodoro;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * statistics storage
 */
public class Storage {

    private volatile List<Pomodoro> pomodoros = new CopyOnWriteArrayList<>();

    /**
     * @param timeFrom
     * @param timeTo
     * @return return pomodoros between timeFrom and timeTo, if not found - returns empty list
     */
    public List<Pomodoro> findInPeriod(Date timeFrom, Date timeTo) {
        return pomodoros.stream()
                .filter(e -> e.getStarted().compareTo(timeFrom) >= 0)
                .filter(e -> e.getStarted().before(timeTo))
                .collect(Collectors.toList());
    }

    /**
     * creates new pomodoro with current started and finished times
     *
     * @return
     */
    public Pomodoro create() {
        Pomodoro pom = new Pomodoro(new Date(), new Date());
        pomodoros.add(pom);
        return pom;
    }

    /**
     * update finished time for pomodoro
     *
     * @param pomodoro
     */
    public void update(Pomodoro pomodoro) {
        pomodoro.setFinished(new Date());
    }
}
