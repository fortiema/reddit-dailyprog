package syzygy;

import javafx.geometry.Point2D;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Title: SyzygyCalc
 * <p/>
 * Description:
 * <p/>
 * Reddit DailyProg #186 [INTERMEDIATE]
 * <a>http://www.reddit.com/r/dailyprogrammer/comments/2kpnky/10292014_challenge_186_intermediate_syzygyfication/</a>
 * <p/>
 * Basic solar system simulation that searches for Syzygies.
 * Simplified physics model (immobile Sun, flat plane, perfect circle orbits)
 * <p/>
 * @author: Matt Fortier <fortiema@gmail.com>
 * <p/>
 * Created on: 2014-12-26.
 * Last Modified on: 2014-12-26.
 */
public class SyzygyCalc {

    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private static List<Planet> solarSystem = new ArrayList<Planet>();

    public static void main(String[] args) {

        // 1. Generate Solar System
        solarSystem.add(new Planet("Sun", 0.0, 1.0));            // HACK: Period set to 1 to avoid div-by-0 Exception
        solarSystem.add(new Planet("Mercury", 0.387, 0.241));
        solarSystem.add(new Planet("Venus", 0.723, 0.615));
        solarSystem.add(new Planet("Earth", 1.000, 1.000));
        solarSystem.add(new Planet("Mars", 1.524, 1.881));
        solarSystem.add(new Planet("Jupiter", 5.204, 11.862));
        solarSystem.add(new Planet("Saturn", 9.582, 29.457));
        solarSystem.add(new Planet("Uranus", 19.189, 84.017));
        solarSystem.add(new Planet("Neptune", 30.071, 164.795));

        try {
            // 2. Get User Input
            Scanner scanner = new Scanner(System.in);
            double yearDelta = Double.parseDouble(bufferedReader.readLine());

            // 3. Update Planets potiions
            for (Planet p : solarSystem) {
                p.setElapsedYears(yearDelta);
            }

            // ----- DEBUG -----
            for (Planet p : solarSystem) {
                System.out.println(p.toString());
            }
            // --- END DEBUG ---

            // 4. Check for Syzygies
            List<Set<String>> syzygies = findSyzygies(solarSystem);

            if (syzygies == null) {
                System.out.println("No syzygies at this epoch.");
            } else {
                System.out.println("Syzygy detected between:");
                for (Set<String> set : syzygies) {
                    for (String s : set) {
                        System.out.print(s + "-");
                    }
                    System.out.println();
                }
            }

            // 5. Display graphical system (?)

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    /**
     * Find sets of 3+ planets aligned to 1.0 deg or less (aka. "Syzygy")
     * <p/>
     * Based on Princeton CS226 Line Pattern Recognition in a point set.
     * <a>http://www.cs.princeton.edu/courses/archive/spring03/cs226/assignments/lines.html</a>
     * @param starSystem List of the Planets composing the system
     * @return A list of arrays of Planets forming syzygies (null if none)
     */
    public static List<Set<String>> findSyzygies(List<Planet> starSystem) {

        List<Set<String>> syzygiesList = new ArrayList<>();

        Comparator<Map.Entry<Planet, Point2D>> byAngle = (p1, p2) -> new Double(p1.getValue().angle(new Point2D(0,0))).compareTo(
                                                                                p2.getValue().angle(new Point2D(0,0)));

        // 1. For each planet in StarSystem
        for (Planet p1 : starSystem) {
            Map<Planet, Point2D> currPlanetVectMap = new HashMap<>();

            // 2. Treat its current position as origin and fetch vectors to all other planets
            for (Planet p2 : starSystem) {
                if (!p2.equals(p1)) {
                    double vX = p2.getPositionCartesian().getX() - p1.getPositionCartesian().getX();
                    double vY = p2.getPositionCartesian().getY() - p1.getPositionCartesian().getY();
                    currPlanetVectMap.put(p2, new Point2D(vX, vY));
                }
            }

            // Java 8 - Sorting of Map Entries using Stream API and lambda expression Comparator
            Map<Planet, Point2D> sortedAngleMap = currPlanetVectMap
                    .entrySet()
                    .stream()
                    .sorted(byAngle)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // 3. Compare adjacent angles. If >= 2 other planets have <= 1.0 angle delta, add to syzygy list
            Set<String> syzNameList = new HashSet<>();

            for (Map.Entry<Planet, Point2D> e1 : sortedAngleMap.entrySet()) {
                for (Map.Entry<Planet, Point2D> e2 : sortedAngleMap.entrySet()) {
                    if (!e2.equals(e1)) {
                        if (e2.getValue().angle(e1.getValue()) <= 1.0) {
                            syzNameList.add(e1.getKey().getName());
                            syzNameList.add(e2.getKey().getName());
                        }
                    }
                }
            }

            if (syzNameList.size() > 1) {
                syzNameList.add(p1.getName());
                syzygiesList.add(syzNameList);
            }

        }

        return (syzygiesList.size() > 2 ? syzygiesList : null);
    }

}
