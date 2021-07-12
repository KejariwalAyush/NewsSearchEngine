package data;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import searchEngine.UrlDetails;

public class LoadData {
    public static HashSet<UrlDetails> getData() {

        // HashSet<String> sites = importData("sites.txt", 0);
        HashSet<UrlDetails> database = new HashSet<>();
        long startTime = System.currentTimeMillis();

        String[] news = { "https://news.google.com/topstories", "https://www.bbc.com/", "https://edition.cnn.com/",
                "https://www.nbcnews.com/", };
        int in = 0;
        // Scanner sc = new Scanner(System.in);
        // System.out.println("Select Source of News:");
        // for (int i = 0; i < news.length; i++)
        // System.out.println(i + ": " + news[i]);
        // in = sc.nextInt();
        // // sc.nextLine();
        // sc.close();
        // if (in > news.length && in <= 0)
        // in = 0;

        HashSet<String> urls = getLinks(news[in]);
        HashSet<String> inUrls = new HashSet<>();

        for (String s : urls) {
            inUrls.add(s);
            if (inUrls.size() < 1000)
                inUrls.addAll(getLinks(s));
        }

        for (String site : inUrls) {
            UrlDetails ud = new UrlDetails(site);
            database.add(ud);

            try {
                Thread.sleep(50);
                printProgress(startTime, inUrls.size(), database.size());
            } catch (InterruptedException e) {
            }
        }

        long eta = (System.currentTimeMillis() - startTime);
        String etaHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));
        System.out.println("\nTotal Time Taken: " + etaHms);
        return database;
    }

    private static HashSet<String> importData(String filePath, int dataSize) {
        HashSet<String> data = new HashSet<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null && (dataSize == 0 || data.size() < dataSize))
                data.add(line);

            reader.close();
            return data;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }

    }

    private static HashSet<String> getLinks(String url) {
        HashSet<String> urls = new HashSet<>();
        urls.add(url);

        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("a");
            for (Element element : elements) {
                if (!element.absUrl("href").contains("#"))
                    urls.add(element.absUrl("href"));
            }
        } catch (Exception e) {
            System.err.println("Cannot Load data: " + e.toString());
        }
        return urls;
    }

    private static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 : (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A"
                : String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string.append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent)).append(String.join("", Collections.nCopies(percent, "=")))
                .append('>').append(String.join("", Collections.nCopies(100 - percent, " "))).append(']')
                .append(String.join("",
                        Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }
}
