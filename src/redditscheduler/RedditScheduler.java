package redditscheduler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
 * Created on: 14-12-03.
 * Last Modified on: 14-12-03.
 */
public class RedditScheduler {

    private static final String TIME_TABLE_FILE_PATH = "/Users/matt/Coding/Reddit-DailyProg/src/redditscheduler/TimeTable.txt";
    private static ArrayList<PlannedActivity> plannedActivities = new ArrayList<PlannedActivity>();

    public static void main(String[] args) {

        String[] rawActivities = processTimeTableFile(TIME_TABLE_FILE_PATH);
        if (rawActivities == null) { System.exit(-1); }

        for (String s : rawActivities) {
            plannedActivities.add(new PlannedActivity(s));
        }

        System.out.println(plannedActivities.get(2).toString() );
        System.exit(0);

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

    private static LocalDateTime findBiggestFreeGap(ArrayList<PlannedActivity> activities) {
        return null;
    }

}
