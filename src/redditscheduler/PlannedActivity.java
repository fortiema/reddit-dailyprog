package redditscheduler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Title: PlannedActivity
 * <p/>
 * Description:
 * Abstraction of an activity on a planner
 * <p/>
 * @author: Matt Fortier <fortiema@gmail.com>
 * <p/>
 * Created on: 2014-12-04.
 * Last Modified on: 2014-12-25.
 */
public class PlannedActivity implements Comparable<PlannedActivity> {

    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public PlannedActivity() {}

    public PlannedActivity(PlannedActivity activity) {
        this.name = activity.name;
        this.startTime = activity.startTime;
        this.endTime = activity.endTime;
    }

    public PlannedActivity(String rawActivity) {
        String[] splitName = rawActivity.split("--");
        if (splitName.length == 2) {
            this.name = splitName[1].trim();

            try {
                String[] splitDateTime = splitName[0].split(":",2);
                this.date = LocalDate.parse(splitDateTime[0], DateTimeFormatter.ofPattern("M-d-yyyy"));

                String[] splitTime = splitDateTime[1].split("to");
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(" hh:mm a ");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(" hh:mm a ");
                this.startTime = LocalTime.parse(splitTime[0], formatter1);
                this.endTime = LocalTime.parse(splitTime[1], formatter2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public int compareTo(PlannedActivity a2) {
        if (a2 == null) throw new NullPointerException();
        if (this.getDate().isEqual(a2.getDate())) {
            if (this.getStartTime().isBefore(a2.getStartTime())) {
                return -1;
            } else {
                return 1;
            }
        } else if (this.getDate().isBefore(a2.getDate())) {
                return -1;
        } else {
            return 1;
        }
    }

    public String toString() {
        return (this.date.toString() + " - " + this.startTime.toString() + " to " + this.endTime.toString()
                + " -- " + this.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

}