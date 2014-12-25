package redditscheduler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

/**
 * Title: RedditScheduler
 * <p/>
 * Description:
 * <p/>
 * Reddit DailyProg #187 [INTERMEDIATE]
 * <a>http://www.reddit.com/r/dailyprogrammer/comments/2ledaj/11052014_challenge_187_intermediate_finding_time/</a>
 * <p/>
 * @author: Matt Fortier <fortiema@gmail.com>
 * <p/>
 * Created on: 2014-12-03.
 * Last Modified on: 2014-12-25.
 */
public class RedditScheduler {

    private static final String TIME_TABLE_FILE_PATH = "G:\\Coding\\reddit-dailyprog\\src\\redditscheduler\\TimeTable.txt";

    // Use TreeMap to guarantee sorted keys - May hinder performance on big sets but not a concern here
    private static Map<LocalDate, List<PlannedActivity>> schedule = new TreeMap<LocalDate, List<PlannedActivity>>();
    private static List<PlannedActivity> plannedActivities = new ArrayList<PlannedActivity>();

    private static long totalActivitiesTime = 0L;

    public static void main(String[] args) {

        // Recover contents of data file
        String[] rawActivities = processTimeTableFile(TIME_TABLE_FILE_PATH);
        if (rawActivities == null) { System.exit(-1); }

        // Populate the dataset
        for (String s : rawActivities) {
            plannedActivities.add(new PlannedActivity(s));
        }

        // Add activities to their date list in schedule map
        for (PlannedActivity pa : plannedActivities) {
            LocalDate date = pa.getDate();
            if (!schedule.containsKey(date)) {
                schedule.put(date, new ArrayList<PlannedActivity>());
            }
            schedule.get(date).add(pa);
        }

        // Add reditting activity
        for (LocalDate date : schedule.keySet()) {
            addRedditingInBiggestFreeGap(schedule.get(date));
        }

        Map<String, Long> actTimeStats = computeActivityStats();

        outputCalendarStats(schedule, actTimeStats);
        System.exit(0);

    }

    private static void outputCalendarStats(Map<LocalDate, List<PlannedActivity>> cal, Map<String, Long> actTimeStats) {
        // Output Schedule
        for (LocalDate date : cal.keySet()) {
            System.out.println(date.toString());
            System.out.println("---");
            for (PlannedActivity act : cal.get(date)) {
                System.out.println("\t" + act.getStartTime() + " to " + act.getEndTime() + " - " + act.getName());
            }
            System.out.println();
        }

        // Output Stats
        System.out.println("Statistics: ");
        for (String name : actTimeStats.keySet()) {
            System.out.println("\t" + name + " -> " + actTimeStats.get(name).intValue() + "min, or "
                                + String.format("%.2f", (actTimeStats.get(name).doubleValue() / (double) totalActivitiesTime) * 100.0)
                                + "% of the week.");
        }

        System.out.println();
    }

    private static String[] processTimeTableFile(String filePath) {
        try {
            List<String> rawList = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            return rawList.toArray(new String[rawList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addRedditingInBiggestFreeGap(List<PlannedActivity> activities) {

        // 1. Sort activities of the day by chronological order
        Collections.sort(activities);

        long maxFreeTimeSecs = 0;
        long currFreeTimeSecs = 0;
        PlannedActivity a1 = activities.get(0);
        PlannedActivity a2 = activities.get(1);

        // 2. Find largest free time gap in the day
        for (int i = 0; i < activities.size()-1; i++) {
            currFreeTimeSecs = Duration.between(activities.get(i).getEndTime(),
                    activities.get(i + 1).getStartTime()).getSeconds();
            if (currFreeTimeSecs > maxFreeTimeSecs) {
                maxFreeTimeSecs = currFreeTimeSecs;
                a1 = activities.get(i);
                a2 = activities.get(i+1);
            }
        }

        if (maxFreeTimeSecs > 0) {
            // 3. Insert Redditing in largest free time gap
            PlannedActivity redditing = new PlannedActivity();
            redditing.setName("redditing");
            redditing.setDate(a1.getDate());
            redditing.setStartTime(a1.getEndTime());
            redditing.setEndTime(a2.getStartTime());
            activities.add(redditing);
        }

        Collections.sort(activities);
    }

    private static Map<String, Long> computeActivityStats() {
        // 1. Regroup activities by name
        Map<String, List<PlannedActivity>> actByNameMap = new HashMap<String, List<PlannedActivity>>();

        for (List<PlannedActivity> l : schedule.values()) {
            for (PlannedActivity act : l) {
                String name = act.getName();
                if (!actByNameMap.containsKey(name)) {
                    actByNameMap.put(name, new ArrayList<PlannedActivity>());
                }
                actByNameMap.get(name).add(act);
            }
        }

        // 2. Compute total activity time
        Map<String, Long> actTotTimeMap = new HashMap<String, Long>();
        for (String name : actByNameMap.keySet()) {
            long totalActTime = 0L;
            for (PlannedActivity act : actByNameMap.get(name)) {
                totalActTime += Duration.between(act.getStartTime(), act.getEndTime()).toMinutes();
            }
            actTotTimeMap.put(name, totalActTime);
            totalActivitiesTime += totalActTime;
        }

        return actTotTimeMap;
    }

}
