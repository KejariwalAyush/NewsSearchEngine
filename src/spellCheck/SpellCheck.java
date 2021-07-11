package spellCheck;

import java.util.ArrayList;
import java.util.Scanner;

public class SpellCheck {

    private  MyDictionary dict;
    final static String filePath = "words.txt";
    final static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public SpellCheck() {
        dict = new MyDictionary();
        dict.build(filePath);
    }

    String printSuggestions(String input) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> print = makeSuggestions(input);
        if (print.size() == 0) {
            return "and I have no idea what word you could mean.\n";
        }
        sb.append("perhaps you meant:\n");
        for (String s : print) {
            sb.append("\n  -" + s);
        }
        return sb.toString();
    }

    private  ArrayList<String> makeSuggestions(String input) {
        ArrayList<String> toReturn = new ArrayList<>();
        toReturn.addAll(charAppended(input));
        toReturn.addAll(charMissing(input));
        toReturn.addAll(charsSwapped(input));
        return toReturn;
    }

    private  ArrayList<String> charAppended(String input) {
        ArrayList<String> toReturn = new ArrayList<>();
        for (char c : alphabet) {
            String atBack = input + c;
            for(int i=0;i<input.length();i++) {
            	String temp = input.substring(0, i) + c + input.substring(i);
            	if (dict.contains(temp)) 
                    toReturn.add(temp);
            }
            if (dict.contains(atBack)) 
                toReturn.add(atBack);
            
        }
        return toReturn;
    }

    private  ArrayList<String> charMissing(String input) {
        ArrayList<String> toReturn = new ArrayList<>();

        int len = input.length() - 1;
        // try removing char from the front
        if (dict.contains(input.substring(1))) {
            toReturn.add(input.substring(1));
        }
        for (int i = 1; i < len; i++) {
            // try removing each char between (not including) the first and last
            String working = input.substring(0, i);
            working = working.concat(input.substring((i + 1), input.length()));
            if (dict.contains(working)) {
                toReturn.add(working);
            }
        }
        if (dict.contains(input.substring(0, len))) {
            toReturn.add(input.substring(0, len));
        }
        return toReturn;
    }

    private  ArrayList<String> charsSwapped(String input) {
        ArrayList<String> toReturn = new ArrayList<>();

        for (int i = 0; i < input.length() - 1; i++) {
            String working = input.substring(0, i);// System.out.println(" 0:" + working);
            working = working + input.charAt(i + 1); // System.out.println(" 1:" + working);
            working = working + input.charAt(i); // System.out.println(" 2:" + working);
            working = working.concat(input.substring((i + 2)));// System.out.println(" FIN:" + working);
            if (dict.contains(working)) {
                toReturn.add(working);
            }
        }
        return toReturn;
    }
    
//    void run() {
//        Scanner scan = new Scanner(System.in);
//        String input;
//
//        while (true) {
//            System.out.print("\n-------Enter a word: ");
//            input = scan.nextLine();
//            if (input.equals(""))
//                break;
//
//            if (dict.contains(input)) {
//                System.out.println("\n" + input + " is spelled correctly");
//                System.out.println(printSuggestions(input));
//            } else {
//                System.out.print("is not spelled correctly, ");
//                System.out.println(printSuggestions(input));
//            }
//        }
//        scan.close();
//    }
    
    public  String validate(String input) {
    	/*
    	 * This function checks if the input given is valid or not.
    	 * if it is just an empty input the while loop will break and return null
    	 * if it is a word contained in the dictionary that word will be returned
    	 * otherwise we assume that a spelling mistake has been made and the
    	 * "makeSuggestions" method is called.
    	 */
    	while(input != null) {
    		 if (input.equals(""))
                 break;
    		 if (dict.contains(input)) {
                 return input;
             } else {
            	ArrayList<String> sug = makeSuggestions(input);
            	if(sug.size()>0)
            	 return sug.get(0);
            	else return input;
             }
    	}
    	return null;
    }

//    public static void main(String[] args) {
//        SpellCheck sc = new SpellCheck();
//        sc.run();
//    }

}