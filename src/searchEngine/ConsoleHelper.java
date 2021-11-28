package searchEngine;

import java.util.concurrent.TimeUnit;
import java.util.*;

// Ayush Kejariwal
// 1941012408
// CSE-D

//This class is responsible for generating animation in the console while
//data is being loaded

public class ConsoleHelper {
    private static String lastLine = "";
    private static byte anim;

    private static void print(String line) {
        // clear the last line if longer
        if (lastLine.length() > line.length()) {
            String temp = "";
            for (int i = 0; i < lastLine.length(); i++) {
                temp += " ";
            }
            if (temp.length() > 1)
                System.out.print("\r" + temp);
        }
        System.out.print("\r" + line);
        lastLine = line;
    }

    public static void animate(String line) {
        switch (anim) {
            case 1:
                print("[ \\ ] ");
                break;
            case 2:
                print("[ | ] ");
                break;
            case 3:
                print("[ / ] ");
                break;
            default:
                anim = 0;
                print("[ - ] ");
        }
        anim++;
    }

    public static void printProgress(long startTime, long total, long current) {

        // formula to estimate time for loading data.
        long eta = current == 0 ? 0 : (total - current) * (System.currentTimeMillis() - startTime) / current;

        // formatting the eta time in hh:mm:ss
        String etaHms = current == 0 ? "N/A"
                : String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);

        // calculating percentage. This percentage is the input variable for the
        // animation
        int percent = (int) (current * 100 / total);
        // '\r' - return catridge. It acts as a tool for helping text dissapear.
        string.append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% ┤", percent)).append(String.join("", Collections.nCopies(percent, "█")))
                .append('█').append(String.join("", Collections.nCopies(100 - percent, " "))).append('├')
                .append(String.join("",
                        Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }
}