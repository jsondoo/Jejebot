package com.jejebot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class MarkovBot {
    private HashMap<StringPair, ArrayList<String>> markovTable; // maps a string pair to all possible next value

    public MarkovBot(File f) {
        this.markovTable = parseFile(f);
    }


    public HashMap<StringPair, ArrayList<String>> parseFile(File file) {
        HashMap<StringPair, ArrayList<String>> table = new HashMap<>();

        try {
            Scanner input = new Scanner(file);
            String first = input.next();
            String second = input.next();
            String next;

            while (input.hasNext()) {
                next = input.next();
                StringPair pair = new StringPair(first, second);

                if (table.containsKey(pair)) { // if key exists, add value to the ArrayList
                    table.get(pair).add(next);
                } else { // if key does not exist, create key and initialize new ArrayList with value
                    ArrayList<String> values = new ArrayList<String>();
                    values.add(next);
                    table.put(pair, values);
                }

                first = second; // shift over one word
                second = next;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found...");
        }

        return table;
    }

    public String createSentence() {
        if (markovTable.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();

        StringPair nextSP = getInitialPair();
        sb.append(nextSP.toString());

        int wordCount = 0;
        Random random = new Random();
        while (wordCount < 18 || !nextSP.getTwo().endsWith(".")) { // create sentence until 18+ words and gets a string ending with a period
            ArrayList<String> values = markovTable.get(nextSP);
            if (values != null) {
                String next = values.get(random.nextInt(values.size()));
                sb.append(" " + next);

                nextSP = new StringPair(nextSP.getTwo(), next);
            }
            wordCount++;
        }
        return sb.toString();
    }


    public StringPair getRandomPair() { // returns a random pair from markovTable
        ArrayList<StringPair> stringPairs = new ArrayList<StringPair>(markovTable.keySet());
        Random rand = new Random();
        return stringPairs.get(rand.nextInt(stringPairs.size()));
    }

    public StringPair getInitialPair() { // returns a StringPair where first string in the pair starts with a capital letter
        ArrayList<StringPair> stringPairs = new ArrayList<StringPair>(markovTable.keySet());
        Random rand = new Random();
        StringPair stringPair = stringPairs.get(rand.nextInt(stringPairs.size()));
        while (!Character.isUpperCase(stringPair.getOne().charAt(0))) { // get random pair until it starts with a capital letter
            stringPair = stringPairs.get(rand.nextInt(stringPairs.size()));
        }
        return stringPair;
    }

    /**
     * A simple class representing a pair of strings
     */
    public class StringPair {
        private String one;
        private String two;

        public StringPair(String a, String b) {
            this.one = a;
            this.two = b;
        }

        public String getOne() {
            return one;
        }

        public String getTwo() {
            return two;
        }

        @Override
        public String toString() {
            return one + " " + two;
        }

        // Order of strings in a pair matters for equality/hashcode (that is StringPair("a","b") != StringPair("b","a")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringPair that = (StringPair) o;

            return one.equals(that.one) && two.equals(that.two);
        }

        @Override
        public int hashCode() {
            int result = one.hashCode();
            result = 31 * result + two.hashCode();
            return result;
        }
    }


}
