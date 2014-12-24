package flagonflags;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.HashMap;

/**
 * Title: MainFlagonFlags
 * <p/>
 * Description:
 * CLI flag command analyzer
 * <p/>
 * Reddit DailyProg #187 [EASY]
 * <a>http://www.reddit.com/r/dailyprogrammer/comments/2l6dll/11032014_challenge_187_easy_a_flagon_of_flags/</a>
 * <p/>
 * @author: Matt Fortier <a>fortiema@gmail.com</a>
 * <p/>
 * Created on: 14-12-03.
 * Last Modified on: 14-12-03.
 */
public class FlagonFlags {

    public static HashMap<Character, String> shortArgs = new HashMap<Character, String>();
    static BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( System.in ) );

    public static void main(String[] args) {
        try {
            // Read number of short-form flags
            Scanner scanner = new Scanner(System.in);
            int nbShortArgs = Integer.parseInt(bufferedReader.readLine());

            // Process the list of short-form args
            for (int i = 0; i < nbShortArgs; i++) {
                String newArg = scanner.nextLine();
                String[] argComponents = newArg.split(":");
                shortArgs.put(argComponents[0].toCharArray()[0], argComponents[1]);
            }

            // Process actual flag list passed
            String[] tokens = scanner.nextLine().split(" ");

            // Interpret flag list and output result
            for (String s : tokens) {
                outputFlag(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void outputFlag(String flag) {
        if (flag.startsWith("--")) {
            System.out.println("flag: " + flag.substring(2));
        } else if (flag.startsWith("-")) {
            char[] charFlags = flag.toCharArray();

            // Don't process arg 0, it's the hyphen!
            for (int j = 1; j < charFlags.length; j++) {
                if (shortArgs.containsKey(Character.valueOf(charFlags[j]))) {
                    System.out.println("flag: " + shortArgs.get(Character.valueOf(charFlags[j])));
                } else {
                    System.out.println("flag: " + charFlags[j]);
                }
            }
        } else {
            System.out.println("parameter: " + flag);
        }
    }

}
