package spellCheck;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MyDictionary {
	private int M = 1319; // prime number
	final private Bucket[] array;

	public MyDictionary() {
		array = new Bucket[M];
		for (int i = 0; i < M; i++) {
			array[i] = new Bucket();
		}
	}

	private int hash(String key) {
		return (key.hashCode() & 0x7fffffff) % M;
	}

	// call hash() to decide which bucket to put it in, do it.
	public void add(String key) {
		array[hash(key)].put(key);
	}

	// call hash() to find what bucket it's in, get it from that bucket.
	public boolean contains(String input) {
		input = input.toLowerCase();
		return array[hash(input)].get(input);
	}

	public void build(String filePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				add(line);
			}
			reader.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// this method is used in my unit tests
	public String[] getRandomEntries(int num) {
		String[] toRet = new String[num];
		for (int i = 0; i < num; i++) {
			// pick a random bucket, go out a random number
			Node n = array[(int) Math.random() * M].getFirst();
			int rand = (int) Math.random() * (int) Math.sqrt(num);

			for (int j = 0; j < rand && n.next != null; j++)
				n = n.next;
			toRet[i] = n.word;
		}
		return toRet;
	}

}

class Bucket {
	private Node first;

	public boolean get(String in) { // return key true if key exists
		Node next = getFirst();
		while (next != null) {
			if (next.word.equals(in)) {
				return true;
			}
			next = next.next;
		}
		return false;
	}

	public void put(String key) {
		for (Node curr = getFirst(); curr != null; curr = curr.next) {
			if (key.equals(curr.word)) {
				return; // search hit: return
			}
		}
		setFirst(new Node(key, getFirst())); // search miss: add new node
	}

	public Node getFirst() {
		return first;
	}

	public void setFirst(Node first) {
		this.first = first;
	}
}

class Node {
	String word;
	Node next;

	public Node(String key, Node next) {
		this.word = key;
		this.next = next;
	}
}