package searchEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import spellCheck.SpellCheck;

public class SearchEngine {
	private static Node<UrlData> node;

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Intializing serach engine.....");
		HashSet<String> sites = importData("sites.txt", 20);
		HashSet<UrlDetails> database = new HashSet<>();
		long startTime = System.currentTimeMillis();
		for (String site : sites) {
			UrlDetails ud = new UrlDetails(site);
			database.add(ud);

			try {
				Thread.sleep(50);
				printProgress(startTime, sites.size(), database.size());
			} catch (InterruptedException e) {
			}

		}
		System.out.println("\nInitialization complete!!\n");

		String query = "Machne Learnin";
		while (true) {
			node = null;
			System.out.print("Enter Your Query: ");
			query = sc.nextLine();
			if (query.trim().equalsIgnoreCase("exit")) {
				System.out.println("\n\n***** Thank you :) ******");
				break;
			}

			String correctedQuery = spellCheckSentence(query);
			if (!query.trim().equalsIgnoreCase(correctedQuery.trim()))
				System.out.println("Did you mean: " + correctedQuery);

			System.out.println("Searching for: " + correctedQuery);
			System.out.println();
			for (UrlDetails ud : database) {
				search(query, ud);
			}
			if (node == null || node.data == null)
				System.out.println("Sorry No results Found!");
			else
				printBST(node);

			System.out.println("Search completed.\n");
		}

		sc.close();
	}

	private static String spellCheckSentence(String query) {
		SpellCheck spell = new SpellCheck();
		StringBuilder manipulatedQuery = new StringBuilder();
		StringTokenizer st = new StringTokenizer(query);
		while (st.hasMoreTokens()) {
			String temp = st.nextToken();
			String validString = spell.validate(temp);
			manipulatedQuery.append(validString);
			manipulatedQuery.append(" ");
		}
		return manipulatedQuery.toString();
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

	private static void search(String query, UrlDetails ud) {
		HashSet<String> temp = new HashSet<String>();
		if (ud.title != null && ud.title.toLowerCase().contains(query.toLowerCase())) {
			node = BST.insert(node, new UrlData(ud.url, ud.title, 50));
			for (String url : ud.innerUrls) {
				Document docs;
				try {
					docs = Jsoup.connect(url).get();
					if (docs.title().toLowerCase().contains(query.toLowerCase()))
						node = BST.insert(node, new UrlData(url, docs.title(), 50));
				} catch (IOException e) {
				}
			}
			return;
		}
		if (ud.unique_words != null)
			temp.addAll(ud.unique_words);
		for (String string : query.toLowerCase().split(" ")) {
			if (ud.title != null && ud.title.toLowerCase().contains(string)) {
				node = BST.insert(node, new UrlData(ud.url, ud.title, 40));
				return;
			}
		}
		int x = 0;
		for (String string : query.toLowerCase().split(" ")) {
			if (temp.contains(string))
				x += 10;
		}
		if (x != 0)
			node = BST.insert(node, new UrlData(ud.url, ud.title, x));
		return;

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

	private static void printBST(Node<UrlData> node) {
		if (node == null)
			return;

		printBST(node.right);
		System.out.print(node.data + "\n");
		printBST(node.left);
	}
}

// private static void getLinks(String url, Set<String> urls) {

// if (urls.contains(url)) {
// return;
// }
// urls.add(url);

// try {
// Document doc = Jsoup.connect(url).get();
// Elements elements = doc.select("a");
// for (Element element : elements) {
// if (!element.absUrl("href").contains("#"))
// try {
// if (urls.size() < 200) {
// System.out.println(element.absUrl("href"));
// // getLinks(element.absUrl("href"), urls);
// }
// } catch (Exception e) {
// // System.err.println(e.toString());
// }
// }
// } catch (IOException e) {

// System.err.println(e.toString());
// }
// }
