package ru.code23rus.pomodoro;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 *
 */
public class TimeTool {

    public void getDayBegin(Date d) {
        ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1);
        
    }

}
