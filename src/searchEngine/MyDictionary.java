package searchEngine;

import java.io.BufferedReader;
import java.io.FileReader;


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
		} catch (Exception e) {
			
		}
	}

	public String[] getRandomEntries(int num) {
		String[] toRet = new String[num];
		for (int i = 0; i < num; i++) {
			// pick a random bucket, go out a random number
			DictNode n = array[(int) Math.random() * M].getFirst();
			int rand = (int) Math.random() * (int) Math.sqrt(num);

			for (int j = 0; j < rand && n.next != null; j++)
				n = n.next;
			toRet[i] = n.word;
		}
		return toRet;
	}

}

class Bucket {
	private DictNode first;

	public boolean get(String in) { // return key true if key exists
		DictNode next = getFirst();
		while (next != null) {
			if (next.word.equals(in)) {
				return true;
			}
			next = next.next;
		}
		return false;
	}

	public void put(String key) {
		for (DictNode curr = getFirst(); curr != null; curr = curr.next) {
			if (key.equals(curr.word)) {
				return; // search hit: return
			}
		}
		setFirst(new DictNode(key, getFirst())); // search miss: add new DictNode
	}

	public DictNode getFirst() {
		return first;
	}

	public void setFirst(DictNode first) {
		this.first = first;
	}
}

class DictNode {
	String word;
	DictNode next;

	public DictNode(String key, DictNode next) {
		this.word = key;
		this.next = next;
	}
}