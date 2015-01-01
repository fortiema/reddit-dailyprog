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
        solarSystem.add(new Planet("Sun", 0.0, Double.MAX_VALUE));      // Period hack to avoid div-by-0 Exception
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
            System.out.println();
            // --- END DEBUG ---

            // 4. Check for Syzygies
            printAngleMap(computeAngleMap(solarSystem));
            List<Set<Planet>> syzygies = findSyzygies(computeAngleMap(solarSystem));

            if (syzygies == null) {
                System.out.println("No syzygies at this epoch.");
            } else {
                System.out.println("Syzygy detected between:");
                for (Set<Planet> set : syzygies) {
                    System.out.print("\t");
                    for (Planet p : set) {
                        System.out.print(p.getName() + "-");
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

    public static Map<Planet, Map<Long, Set<Planet>>> computeAngleMap(List<Planet> starSystem) {
        Map<Planet, Map<Long, Set<Planet>>> angleStarSystemMap = new HashMap<>();

        // 1. For each planet in StarSystem
        for (Planet p1 : starSystem) {
            Map<Long, Set<Planet>> invertedAnglePlanetMap = new TreeMap<>();

            // 2. Treat its current position as origin and fetch vectors to all other planets
            for (Planet p2 : starSystem) {
                if (!p2.equals(p1)) {
                    double vX = p2.getPositionCartesian().getX() - p1.getPositionCartesian().getX();
                    double vY = p2.getPositionCartesian().getY() - p1.getPositionCartesian().getY();
                    long angle = Math.round(Math.toDegrees(Math.atan2(vY, vX)));
                    if (!invertedAnglePlanetMap.containsKey(angle)) {
                        invertedAnglePlanetMap.put(angle, new HashSet<>());
                    }
                    invertedAnglePlanetMap.get(angle).add(p2);
                }
            }

            angleStarSystemMap.put(p1, invertedAnglePlanetMap);
        }

        return angleStarSystemMap;
    }

    public static void printAngleMap(Map<Planet, Map<Long, Set<Planet>>> angleMap) {

        for (Planet p1 : angleMap.keySet()) {
            System.out.println(p1.getName() + " Positions");
            System.out.println("---");
            for (Long angle : angleMap.get(p1).keySet()) {
                System.out.print("\t@ " + angle +" deg.: ");
                for (Planet p2 : angleMap.get(p1).get(angle)) {
                    System.out.print(p2.getName() + " ");
                }
                System.out.println();
            }
        }

    }

    public static List<Set<Planet>> findSyzygies(Map<Planet, Map<Long, Set<Planet>>> angleMap) {

        List<Set<Planet>> syzygiesList = new ArrayList<>();

        for (Planet p1 : angleMap.keySet()) {

            Set<Planet> currSyzygy = new HashSet<>();

            // 1. For each angle in set, see if there is at least 2 planets aligned with p1
            for (Long ang : angleMap.get(p1).keySet()) {
                currSyzygy.add(p1);
                currSyzygy.addAll(angleMap.get(p1).get(ang));

                if (angleMap.get(p1).containsKey(ang+1)) {
                    currSyzygy.addAll(angleMap.get(p1).get(ang+1));
                }
                if (angleMap.get(p1).containsKey(ang+179)) {
                    currSyzygy.addAll(angleMap.get(p1).get(ang+179));
                }
                if (angleMap.get(p1).containsKey(ang+180)) {
                    currSyzygy.addAll(angleMap.get(p1).get(ang+180));
                }
                if (angleMap.get(p1).containsKey(ang+181)) {
                    currSyzygy.addAll(angleMap.get(p1).get(ang+181));
                }

                if (currSyzygy.size() >= 3) {
                    syzygiesList.add(new HashSet<>(currSyzygy));
                }

                currSyzygy.clear();
            }

        }

        return (syzygiesList.isEmpty() ? null : syzygiesList);
    }

    /**
     * Find sets of 3+ planets aligned to 1.0 deg or less (aka. "Syzygy")
     * <p/>
     * Based on Princeton CS226 Line Pattern Recognition in a point set.
     * <a>http://www.cs.princeton.edu/courses/archive/spring03/cs226/assignments/lines.html</a>
     * @param starSystem List of the Planets composing the system
     * @return A list of arrays of Planets forming syzygies (null if none)
     */
    public static List<Set<String>> findSyzygiesCartesian(List<Planet> starSystem) {

        List<Set<String>> syzygiesList = new ArrayList<>();

        // 1. For each planet in StarSystem
        for (Planet p1 : starSystem) {
            Map<Planet, Long> currPlanetVectMap = new HashMap<>();

            // 2. Treat its current position as origin and fetch vectors to all other planets
            for (Planet p2 : starSystem) {
                if (!p2.equals(p1)) {
                    double vX = p2.getPositionCartesian().getX() - p1.getPositionCartesian().getX();
                    double vY = p2.getPositionCartesian().getY() - p1.getPositionCartesian().getY();
                    currPlanetVectMap.put(p2, Math.round(Math.toDegrees(Math.atan2(vY, vX))));
                }
            }

            // Java 8 - Sorting of Map Entries using Stream API and lambda expression Comparator

            // Sorting a map by value does not seem to work, commenting out in favor of list
//            Map<Planet, Double> sortedAngleMap = currPlanetVectMap.entrySet().stream()
//                    .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            List<Map.Entry<Planet, Long>> sortedAngleList = currPlanetVectMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toList());

            // ----- DEBUG -----
            System.out.println(p1.getName() + " Vectors:");
            sortedAngleList.forEach(s -> System.out.println("\t" + s.getKey().getName() + " - " + s.getValue()));
            System.out.println();
            // --- END DEBUG ---

            // 3. Compare adjacent angles. If >= 2 other planets have <= 0.01 rad angle delta, add to syzygy list
            Set<String> syzNameList = new HashSet<>();

            for (int i = 0; i < sortedAngleList.size() - 1; i ++) {
                if ( Math.abs(sortedAngleList.get(i+1).getValue() - sortedAngleList.get(i).getValue()) <= 1.0) {
                    syzNameList.add(sortedAngleList.get(i).getKey().getName());
                    syzNameList.add(sortedAngleList.get(i+1).getKey().getName());
                } else {
                    if (!syzNameList.isEmpty()) {
                        syzNameList.add(p1.getName());
                        if (!syzygiesList.contains(syzNameList)) {
                            syzygiesList.add(new HashSet<>(syzNameList));
                        }
                    }
                    syzNameList.clear();
                }
            }

        }

        return (syzygiesList.size() > 2 ? syzygiesList : null);
    }

}