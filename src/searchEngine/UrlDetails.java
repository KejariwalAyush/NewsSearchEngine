package searchEngine;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import spellCheck.MyDictionary;

public class UrlDetails {
	String url;
	String content;
	String title;
	HashSet<String> unique_words;
	HashSet<String> innerUrls;

	// String regex
	private final String splitString = "[[ ]*|[,]*|[)]*|[(]*|[\"]*|[;]*|[-]*|[:]*|[']*|[?]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+";
	private MyDictionary dict;
	private final static String filePath = "stopwords.txt";

	UrlDetails(String url) {
		this.url = url;
		this.content = webCrawler(url);
		this.unique_words = getUniqueWords(this.content);
		this.innerUrls = new HashSet<>();
	}

	private String webCrawler(String url) {
		/*
		 * this function fetches all the data from the given/specified url.
		 */

		try {
			Document docs = Jsoup.connect(url).get();
			String text = docs.body().text();
			this.title = docs.title();
			Elements el = docs.select("a");
			for (Element element : el) {
				if (!element.absUrl("href").contains("#"))
					try {
						innerUrls.add(element.absUrl("href"));
					} catch (Exception e) {
					}
			}
			return text;
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		}

	}

	private HashSet<String> getUniqueWords(String text) {
		/*
		 * this function generates an array "words" that contains all the words that are
		 * there in the content that has been returned by the "webcrawler" method using
		 * string regex. This array is now copied to a hashset which removes all
		 * duplicates from the array. The hashset checks if it contains any generic
		 * words[and, or, not, there] and removes them thus giving us the number of
		 * unique words in that url.
		 */
		dict = new MyDictionary();
		dict.build(filePath);
		try {
			String[] words = text.split(splitString);
			// System.out.println("Total Words in site content: " + words.length);
			HashSet<String> temp = new HashSet<String>(Arrays.asList(words));
			temp.removeIf(s -> dict.contains(s));

			// System.out.println("Unique Words without stop words: " + temp.size());
			return temp;
		} catch (Exception e) {
			return null;
		}

	}
}
