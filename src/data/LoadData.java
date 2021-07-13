package data;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import searchEngine.UrlDetails;

public class LoadData {
    static int dataCount = 1000;

    public static HashSet<UrlDetails> getData() {

        // HashSet<String> sites = importData("sites.txt", 0);
        HashSet<UrlDetails> database = new HashSet<>();
        long startTime = System.currentTimeMillis();

        String[] news = { "https://news.google.com/topstories", "https://www.bbc.com/", "https://edition.cnn.com/" };
        // int in = 0;

        HashSet<String> inUrls = new HashSet<>();

        try {
            Thread t[] = new Thread[news.length];
            System.out.println("Getting Latest Links...");

            for (int i = 0; i < t.length; i++) {
                final int x = i;
                t[x] = new Thread() {
                    public void run() {
                        HashSet<String> turls = getLinks(news[x]);
                        HashSet<String> tinUrls = new HashSet<>();

                        for (String s : turls) {
                            tinUrls.add(s);
                            if (tinUrls.size() < dataCount)
                                tinUrls.addAll(getLinks(s));
                        }
                        inUrls.addAll(tinUrls);
                    }
                };
            }
            for (Thread thread : t)
                thread.start();

            for (Thread thread : t)
                thread.join();

        } catch (Exception e) {
        }

        ArrayList<String> siteList = new ArrayList<String>();
        siteList.addAll(inUrls);
        try {
            fetchDatabase(database, startTime, inUrls, siteList);
        } catch (Exception e) {
        }
        // for (String site : inUrls) {
        // UrlDetails ud = new UrlDetails(site);
        // database.add(ud);

        // try {
        // Thread.sleep(50);
        // printProgress(startTime, inUrls.size(), database.size());
        // } catch (InterruptedException e) {
        // }
        // }

        long eta = (System.currentTimeMillis() - startTime);
        String etaHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));
        System.out.println("\nTotal Time Taken: " + etaHms);
        return database;
    }

    private static void fetchDatabase(HashSet<UrlDetails> database, long startTime, HashSet<String> inUrls,
            ArrayList<String> siteList) throws Exception {
        Thread td[] = new Thread[10];
        final int cnt = siteList.size() / td.length;

        for (int i = 0, start = 0, end = cnt; i < td.length; i++, start = end, end += cnt) {
            final int x = i;
            if (start < 0)
                start = 0;
            if (end >= siteList.size())
                end = siteList.size() - 1;
            final int fstart = start;
            final int fend = end;

            td[x] = new Thread() {

                public void run() {
                    for (int j = fstart; j < fend; j++) {
                        final String site = siteList.get(j);
                        UrlDetails ud = new UrlDetails(site);
                        database.add(ud);

                        try {
                            Thread.sleep(50);
                            printProgress(startTime, inUrls.size(), database.size());
                        } catch (InterruptedException e) {
                        }
                    }

                }
            };
        }
        for (Thread thread : td)
            thread.start();
        for (Thread thread : td)
            thread.join();
        Thread.sleep(50);
    }

    private static HashSet<String> getLinks(String url) {
        HashSet<String> urls = new HashSet<>();
        urls.add(url);

        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("a");
            for (Element element : elements) {
                if (!element.absUrl("href").contains("#") && urls.size() < (dataCount / 2))
                    urls.add(element.absUrl("href"));
            }
        } catch (Exception e) {
            // System.err.println("Cannot Load data: " + e.toString());
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
                .append(String.format(" %d%% ┤", percent)).append(String.join("", Collections.nCopies(percent, "█")))
                .append('█').append(String.join("", Collections.nCopies(100 - percent, " "))).append('├')
                .append(String.join("",
                        Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }

    // private static HashSet<String> importData(String filePath, int dataSize) {
    // HashSet<String> data = new HashSet<>();
    // try {
    // BufferedReader reader = new BufferedReader(new FileReader(filePath));
    // String line;
    // while ((line = reader.readLine()) != null && (dataSize == 0 || data.size() <
    // dataSize))
    // data.add(line);

    // reader.close();
    // return data;
    // } catch (IOException ioe) {
    // return null;
    // }

    // }
}
