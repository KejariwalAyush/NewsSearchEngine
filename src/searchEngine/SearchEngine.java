package searchEngine;

import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// Ayush Kejariwal
// 1941012408
// CSE-D

public class SearchEngine {
	private static Node<UrlData> node;
	private static int maxPts = Integer.MAX_VALUE;

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Intializing serach engine.....");
		HashSet<UrlDetails> database = LoadData.getData();

		// before asking the user for his/her query we check if the data from the
		// different sites has been loaded
		// correctly. If it hasn't then we dont take the query at all.
		System.out.println("\nInitialization complete!!\n");
		if (database.size() == 0)
			System.err.println("\n\n Error: Empty Database");

		String query = "Machne Learnin"; // here the word machine learning is just a demo query
		while (database.size() != 0) {
			node = null;
			System.out.print("Enter Your Query: ");
			query = sc.nextLine(); // the user input query is being entered and processed here
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
		/*
		 * The general work of this function is to search for data and if found put it
		 * in the BST When a url is ent to it, the title of the url and the user query
		 * are checked for a match. If the title itself contains/is the query it gets
		 * sent to the BST assigned with the maximum points. Then the urls conatined
		 * inside that particular url are also checked for matches. If match is found
		 * the child link gets max points - 500 and is sent to the BST where this data
		 * can be mapped acoording to its points and therefore releavency. After this
		 * process another check is made to see for the links whose title may not have
		 * matched, if the unique words contained in those links match any of the words
		 * in our query. If they do points are assigned and that url is sent to the bst
		 * too.
		 */
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
		// check for unique words matching the query
		if (ud.unique_words != null)
			temp.addAll(ud.unique_words);
		for (String string : query.toLowerCase().split(" ")) {
			if (ud.title != null && ud.title.toLowerCase().contains(string)) {
				node = BST.insert(node, new UrlData(ud.url, ud.title, 40));
				return;
			}
		}
		// check for inner links
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
		/*
		 * The printing of this BST is done from right to left so as the get the links
		 * with the highest points first.
		 */
		if (node == null)
			return;

		printBST(node.right);
		System.out.print(node.data + "\n");
		printBST(node.left);
	}
}
