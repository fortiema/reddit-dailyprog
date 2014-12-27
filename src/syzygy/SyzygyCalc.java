package syzygy;

import javafx.geometry.Point2D;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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
    private static List<Planet> starSystem = new ArrayList<Planet>();

    public static void main(String[] args) {

        // 1. Generate Solar System
        starSystem.add(new Planet("Sun", 0.0, 1.0));            // HACK: Period set to 1 to avoid div-by-0 Exception
        starSystem.add(new Planet("Mercury", 0.387, 0.241));
        starSystem.add(new Planet("Venus", 0.723, 0.615));
        starSystem.add(new Planet("Earth", 1.000, 1.000));
        starSystem.add(new Planet("Mars", 1.524, 1.881));
        starSystem.add(new Planet("Jupiter", 5.204, 11.862));
        starSystem.add(new Planet("Saturn", 9.582, 29.457));
        starSystem.add(new Planet("Uranus", 19.189, 84.017));
        starSystem.add(new Planet("Neptune", 30.071, 164.795));

        try {
            // 2. Get User Input
            Scanner scanner = new Scanner(System.in);
            double yearDelta = Double.parseDouble(bufferedReader.readLine());

            // 3. Update Planets potiions
            for (Planet p : starSystem) {
                p.setElapsedYears(yearDelta);
            }

            // ----- DEBUG -----
            for (Planet p : starSystem) {
                System.out.println(p.toString());
            }
            // --- END DEBUG ---

            // 4. Check for Syzygies
            Map<String, Point2D> vectors = extractPlanetPairVectors();
            Set<String> syzygies = findSyzygies(vectors);

            if (syzygies == null) {
                System.out.println("No syzygies at this epoch.");
            } else {
                System.out.println("Syzygy detected between:");
                syzygies.forEach(s -> System.out.println(s));               // Java 8 lambda
            }

            // 5. Display graphical system (?)

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static Set<String> findSyzygies(Map<String, Point2D> vectors) {

        Set<String> syzygies = new TreeSet<>();

        for (Map.Entry<String, Point2D> v : vectors.entrySet()) {
            for (Map.Entry<String, Point2D> v2 : vectors.entrySet()) {
                if (! v2.getKey().equals(v.getKey())) {
                    if (v.getValue().angle(v2.getValue()) < 1.0) {
                        syzygies.addAll(Arrays.asList(v.getKey().split("-")));
                        syzygies.addAll(Arrays.asList(v2.getKey().split("-")));

                    }
                }
            }
        }

        return (syzygies.size() > 2 ? syzygies : null);
    }

    public static Map<String, Point2D> extractPlanetPairVectors() {

        Map<String, Point2D> planetVectorMap = new HashMap<>();

        for (int i = 0; i < starSystem.size(); i++) {
            for (int j = i+1; j < starSystem.size(); j++) {
                double newX = starSystem.get(i).getPositionCartesian().getX() - starSystem.get(j).getPositionCartesian().getX();
                double newY = starSystem.get(i).getPositionCartesian().getY() - starSystem.get(j).getPositionCartesian().getY();
                planetVectorMap.put(starSystem.get(i).getName() + "-" + starSystem.get(j).getName(), new Point2D(newX, newY));
            }
        }

        return planetVectorMap;
    }
}
