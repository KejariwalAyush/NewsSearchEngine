package searchEngine;

import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SearchEngine {
	private static Node<UrlData> node;
	private static int maxPts = Integer.MAX_VALUE;

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Intializing serach engine.....");
		HashSet<UrlDetails> database = LoadData.getData();

		System.out.println("\nInitialization complete!!\n");
		if (database.size() == 0)
			System.err.println("\n\n Error: Empty Database");

		String query = "Machne Learnin";
		while (database.size() != 0) {
			node = null;
			System.out.print("Enter Your Query: ");
			query = sc.nextLine();
			if (query.trim().equalsIgnoreCase("exit")) {
				System.out.println("\n\n***** Thank you :) ******");
				break;
			}

			for (UrlDetails ud : database) {
				search(query, ud);
			}
			if (node == null || node.data == null)
				System.out.println("Sorry No results Found!");
			else
				printBST(node);

			System.out.println("Search completed.");
			System.out.println("-------------------------------------------------------------");
		}

		sc.close();
	}

	private static void search(String query, UrlDetails ud) {
		HashSet<String> temp = new HashSet<String>();
		if (ud.title != null && ud.title.toLowerCase().contains(query.toLowerCase())) {
			node = BST.insert(node, new UrlData(ud.url, ud.title, maxPts));
			for (String url : ud.innerUrls) {
				Document docs;
				try {
					docs = Jsoup.connect(url).get();
					if (docs.title().toLowerCase().contains(query.toLowerCase()))
						node = BST.insert(node, new UrlData(url, docs.title(), maxPts - 500));
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
		for (String url : ud.innerUrls) {
			if (url.contains(query)) {
				Document docs;
				try {
					docs = Jsoup.connect(url).get();
					if (docs.title().toLowerCase().contains(query.toLowerCase()))
						node = BST.insert(node, new UrlData(url, docs.title(), maxPts - 1000));
				} catch (IOException e) {
				}
			}
		}
		int x = 0;
		for (String string : query.toLowerCase().split(" ")) {
			if (temp.contains(string))
				for (String s : ud.content.split(" "))
					if (s.contains(string))
						x += 5;

		}
		if (x != 0)
			node = BST.insert(node, new UrlData(ud.url, ud.title, x));
		return;

	}

	private static void printBST(Node<UrlData> node) {
		if (node == null)
			return;

		printBST(node.right);
		System.out.print(node.data + "\n");
		printBST(node.left);
	}
}
