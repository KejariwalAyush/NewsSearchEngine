package searchEngine;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class LoadData {
    static int dataCount = 100; //limiting links to 100 from one particular site.

    public static HashSet<UrlDetails> getData() {

        HashSet<UrlDetails> database = new HashSet<>();  //Not an actual database but a hashSet that works 
        long startTime = System.currentTimeMillis();     //as a base where all data(links) are stored
        
        //The links that the user gets will be fetched from these top news sites.
        String[] news = { "https://news.google.com/topstories", "https://www.bbc.com/", "https://edition.cnn.com/" };

        HashSet<String> inUrls = new HashSet<>(); // using hashset to eliminate duplicate links 

        try {
            /*
            *Threads are being used to process multiple data at the same time. Doing this optimises
            *the overall running time for loading data. 
            */
            Thread t[] = new Thread[news.length];
            System.out.println("Getting Latest Links...");

            for (int i = 0; i < t.length; i++) {
                final int x = i;
                

                //using 200 links at a time to speed up the process of loading data. 
                t[x] = new Thread() {
                    public void run() {
                        HashSet<String> turls = getLinks(news[x]);
                        HashSet<String> tinUrls = new HashSet<>();

                        for (String s : turls) {
                            tinUrls.add(s);
                            if (tinUrls.size() < dataCount)
                                tinUrls.addAll(getLinks(s));

                            ConsoleHelper.animate(tinUrls.size() + " ");
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
        siteList.addAll(inUrls); //arraylist in which the hashset having all the urls has been copied
        try {
            fetchDatabase(database, startTime, inUrls, siteList);
        } catch (Exception e) {
        }
        
        // eta= total loading time for data. 
        long eta = (System.currentTimeMillis() - startTime);
        String etaHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));
        System.out.println("\r\nTotal Time Taken: " + etaHms);
        return database;
    }

    private static void fetchDatabase(HashSet<UrlDetails> database, long startTime, HashSet<String> inUrls,
            ArrayList<String> siteList) throws Exception {
                
            /*
            * All the urls that are being loaded are now divided for different threads to use
            * if the total number of links generated are less than 200 one thread per link is 
            * provided accordingly. If more than 200 links are there then the the total number is being 
            * divided and then the links are being divided in the threads accordingly. 
            */

        Thread td[] = new Thread[siteList.size() > 250 ? 250 : siteList.size()];
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
                            ConsoleHelper.printProgress(startTime, inUrls.size(), database.size());
                        } catch (InterruptedException e) {
                        }
                    }

                }
            };
        }
        for (Thread thread : td)
            thread.start();
        for (Thread thread : td) {
            thread.join();
        }
        ConsoleHelper.printProgress(startTime, inUrls.size(), inUrls.size());
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
        }
        return urls;
    }
}
