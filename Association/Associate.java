/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package associate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.*;
import java.io.*;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Ordering;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 *
 * @author Michelle
 */
class ValueComparator implements Comparator {

    Map map;

    public ValueComparator(Map map) {
        this.map = map;
    }

    public int compare(Object keyA, Object keyB) {
        Comparable valueA = (Comparable) map.get(keyA);
        Comparable valueB = (Comparable) map.get(keyB);
        return valueB.compareTo(valueA);
    }
}

public class Associate {
    //public static ArrayList<Integer> sets = new ArrayList<Integer>(Collections.nCopies(20, 0));

    public static Map sortByValue(Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    public static HashMap<Integer, int[]> currentitemset = new HashMap<Integer, int[]>();
    public static HashMap<Integer, String[]> wordeditemset = new HashMap<Integer, String[]>();
    public static HashMap<Integer, String> relatedmap = new HashMap<Integer, String>();
    public static int transactions = 0;
    public static Vector<String> candidates = new Vector<String>();
    public static HashMap<String, Double> globalcandidates = new HashMap<String, Double>();
    public static Hashtable globalitems = new Hashtable();
    public static double Minimumsupport = .4;
    public static double Minimumconfidence = .4;
    public static Hashtable confidenceitems = new Hashtable();
    public static HashMap<String, Double> supportitems = new HashMap<String, Double>();

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    //************************Turn related list***********************************
    public static List<String> getrelatedterms() {
        List<String> relatedlist = new ArrayList<String>();
        int counter = 0;
        try {

            BufferedReader in = new BufferedReader(new FileReader("related.txt"));
            String str;

            while ((str = in.readLine()) != null) {
                relatedlist.add(str);
                relatedmap.put(counter, str);
                counter++;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return relatedlist;
    }

    //****************************************************************************
    public static void transforminverted(List<String> related) {
        List<String> terms = new ArrayList<String>();
        terms = related;

        for (int i = 0; i < terms.size(); i++) { //For every term in related
            String line;

            try (BufferedReader br = new BufferedReader(new FileReader("index.txt"))) {

                while ((line = br.readLine()) != null) { //read until null
                    StringTokenizer st = new StringTokenizer(line);
                    String word = st.nextToken();
                    String compare = terms.get(i);
                    //System.out.println("WORD " + word + " COMPARE: " + compare);
                    if (word.equals(compare)) {
                        //System.out.println("FOUND: " + terms.get(i));
                        StringTokenizer token = new StringTokenizer(line, " []|,");
                        int check = 0;

                        while (token.hasMoreTokens()) { //Retokenize
                            String value = token.nextToken();
                            if (check == 0) {
                                //System.out.println("SKIPPING: " + value);
                                check++;
                            } else if (check % 2 != 0) {//IF ODD

                                //System.out.println("ODD: " + value);
                                check++;
                                Mapenize(i, Integer.parseInt(value)); //Parameters: arrayindexforword:docnumber
                            } else if (check % 2 == 0) {//If EVEN
                                //System.out.println("EVEN: " + value);
                                check++;
                            }
                        }
                        break;
                    }

                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

        }//For term loop done

        try {
            File file = new File("test.txt");
            FileWriter fw = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);

            for (Integer key : Associate.wordeditemset.keySet()) {
                String current[] = new String[20];
                int array[] = new int[20];
                current = Associate.wordeditemset.get(key);
                //System.out.println("KEY: " + key + Arrays.toString(current));

                String content = Arrays.toString(current);
                pw.println(content);
                pw.println();

                Associate.transactions++;
            }
            pw.close();
            System.out.println("TOTAL TRANSACTOIONS: " + Associate.transactions);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void Mapenize(int wordindex, int docnumber) {

        if (Associate.wordeditemset.containsKey(docnumber)) {
            //Update the value
            String array[] = new String[20];
            array = Associate.wordeditemset.get(docnumber);
            array[wordindex] = Associate.relatedmap.get(wordindex);
            Associate.wordeditemset.put(docnumber, array);

        } else {//docnumber is not in global index
            String array[] = new String[20];
            array[wordindex] = Associate.relatedmap.get(wordindex);
            Associate.wordeditemset.put(docnumber, array);

        }
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        Boolean check = false;
        String word = inputStr;
        for (int i = 0; i < 20; i++) {
            //System.out.println("Comparing " + word +" : " + items[i]);
            try {
                if (word.equals(items[i])) {
                    //System.out.println("STOPWORDFOIUND");
                    //System.out.println("FOUND " + word);
                    check = true;
                }
            } catch (NullPointerException e) {

            }
        }
        return check;
    }

    public static void getCandidates(int n) {
        System.out.println("*****************************COMBINATION " + n + ":********************************");

        Vector<String> tempCandidates = new Vector<String>();
        if (n == 1) { //ADD THE SINGLE WORDS
            for (int i = 0; i < Associate.relatedmap.size(); i++) {
                tempCandidates.add(Associate.relatedmap.get(i)); //Table look up
            }
        } else if (n == 2) { // PAIR COMBINATIONS BRUTE FORCE
            for (int i = 0; i < candidates.size(); i++) {
                StringTokenizer token = new StringTokenizer(Associate.candidates.get(i));
                String str1 = token.nextToken();

                for (int j = i + 1; j < Associate.candidates.size(); j++) {
                    StringTokenizer token2 = new StringTokenizer(Associate.candidates.elementAt(j));
                    String str2 = token2.nextToken();
                    tempCandidates.add(str1 + " " + str2);
                }
            }
        } else {
            for (int i = 0; i < Associate.candidates.size(); i++) {
                for (int j = i + 1; j < Associate.candidates.size(); j++) {
                    String string1 = new String(); //Must be object to use comparator
                    String string2 = new String();
                    StringTokenizer token1 = new StringTokenizer(Associate.candidates.get(i));
                    StringTokenizer token2 = new StringTokenizer(Associate.candidates.get(j));

                    for (int s = 0; s < n - 2; s++) { //Get everything but the last token
                        string1 = string1 + " " + token1.nextToken(); // First combination
                        string2 = string2 + " " + token2.nextToken(); // Second combination
                    }
                    if (string2.compareToIgnoreCase(string1) == 0) {
                        //System.out.println("TRIM");
                        tempCandidates.add((string1 + " " + token1.nextToken() + " " + token2.nextToken()).trim());
                    }
                }
            }
        }

        Associate.candidates.clear();
        Associate.candidates = new Vector<String>(tempCandidates);
        tempCandidates.clear();
        //Iterator<String> itr = Associate.candidates.iterator();
        //while (itr.hasNext()) {
        //    System.out.println(itr.next());
        //}
    }

    public static void calculate(int n) {
        System.out.println("==============================PASS " + n + " ======================================");
        //System.out.println("NUMBER OF ITEMS " + Associate.wordeditemset.size());
        // HashMap<Vector<String>, Double> map = new HashMap<Vector<String>, Double>();
        Vector<String> tempCandidates = new Vector<String>();
        //Vector<String> combo = new Vector<String>();
        StringTokenizer token1;
        String[] array;
        int count[] = new int[Associate.candidates.size()];

        for (int i = 1; i < Associate.transactions + 1; i++) {
            array = Associate.wordeditemset.get(i);
            try {
                /*for (int d = 0; d < 20; d++) {
                 System.out.print(array[d] + ", ");
                 }*/
            } catch (NullPointerException e) {
            }
            //System.out.println();

            //******************************************************************************************
            for (int c = 0; c < Associate.candidates.size(); c++) {
                token1 = new StringTokenizer(Associate.candidates.get(c));
                //token2 = new StringTokenizer(Associate.candidates.get(c));

                //while (token2.hasMoreTokens()) { //Make a list of combination tokens for the hashmap
                //    combo.add(token2.nextToken() + " ");
                //}
                int needcounter = token1.countTokens();
                int currentcount = 0;

                while (token1.hasMoreTokens()) {
                    if (stringContainsItemFromList(token1.nextToken(), array)) {
                        currentcount++;
                    }
                }
                if (currentcount == needcounter) {
                    //System.out.println("Combination Found " + combo);

                    count[c]++;

                }

            }
            //******************************************************************************************

        }
        System.out.println();
        for (int i = 0; i < Associate.candidates.size(); i++) {
            double support = (count[i] / (double) Associate.wordeditemset.size());
            //System.out.println("SUPPORT: " + support);
            //System.out.println("Combination " + candidates.get(i));

            if (support >= Minimumsupport) { //Will auto skip blanks
                tempCandidates.add(Associate.candidates.get(i));
                //String value = "Support: " + support;
                Associate.globalcandidates.put(Associate.candidates.get(i), support);
                Associate.globalitems.put(Associate.candidates.get(i), count[i]);
                //Associate.globalsupport.add(support);

                System.out.println("COMBINATION " + candidates.get(i) + "     SUPPORT: " + support);
            }
        }
        Associate.candidates.clear();
        Associate.candidates = new Vector<String>(tempCandidates);
        tempCandidates.clear();
    }

    public static void main(String[] args) {
        List<String> relatedterms = new ArrayList<String>();
        //HashMap itemsets = new HashMap();
        relatedterms = getrelatedterms();
        transforminverted(relatedterms); //First set of Candidates
        int pass = 0;
        do {
            pass++;
            getCandidates(pass);
            calculate(pass);
            System.out.println("===========================================================================");
        } while (Associate.candidates.size() > 1);

        System.out.println("===========================================================================");

        TreeMap<String, Double> sorted = new TreeMap<>(globalcandidates);
        Set<Map.Entry<String, Double>> mappings = sorted.entrySet();

        //TreeMap<String, Integer> itemsorted = new TreeMap<>(globalitems);
        //Set<Map.Entry<String, Integer>> itemmappings = itemsorted.entrySet();
        System.out.println("**********************************SUPPORT***********************************");
        for (Map.Entry<String, Double> mapping : mappings) {
            System.out.println(mapping.getKey() + " ==> " + mapping.getValue());
        }

        System.out.println("===========================================================================");

        /*
         Enumeration names = globalitems.keys();
         Enumeration names1= globalitems.keys();
         while (names.hasMoreElements()) {
         String str = (String) names.nextElement(); //Get the element
         StringTokenizer token1 = new StringTokenizer(str);
            
         while(names1.hasMoreElements()){
         String str2 = (String) names1.nextElement();
                
         }
         }*/
        //String[] keys;
        //Enumeration keys = globalitems.keys();
        Enumeration keys1 = globalitems.keys();

        //Vector<String> test1 = new Vector<String>();
        //Vector<String> test2 = new Vector<String>();
        while (keys1.hasMoreElements()) {

            Enumeration keys2 = globalitems.keys();
            String string1 = (String) keys1.nextElement();

            while (keys2.hasMoreElements()) {
                String string2 = (String) keys2.nextElement();
                boolean match = false;
                if (string1.equals(string2)) {//Skip a perfect match

                    if (keys2.hasMoreElements()) {
                        string2 = (String) keys2.nextElement();
                    } else {
                        break;
                    }
                }

                if (string2.contains(string1)) {
                    int i = 0;
                    int max;
                    Vector<String> test1 = new Vector<String>();
                    Vector<String> test2 = new Vector<String>();

                    StringTokenizer token1 = new StringTokenizer(string1);
                    StringTokenizer token2 = new StringTokenizer(string2);

                    while (token1.hasMoreTokens()) {
                        test1.add(token1.nextToken());
                    }
                    while (token2.hasMoreTokens()) {
                        test2.add(token2.nextToken());
                    }

                    if (test2.size() - 1 == test1.size()) {   //If the comparing string is just 1 less than the word
                        max = test1.size();
                        while (i < max) {   //Test everything except test2's last word
                            String word1 = test1.get(i);
                            String word2 = test2.get(i);

                            if (word1.equals(word2)) { //Compare the two
                                i++;                 //Shift to the next index
                                match = true;
                            } else {
                                match = false;
                                break;
                            }
                        }
                    }
                    if (match == true) {
                        // System.out.println("MATCH FOUND OF " + test2 + " WITH " +test1);
                        String word1 = test2.get(test2.size() - 1);

                        double combos = (int) globalitems.get(string2); //Cast it
                        double combos2 = (int) globalitems.get(string1);
                        double confidence = combos / combos2;
                        if (confidence >= Minimumconfidence) {
                            //System.out.println("COMBO: " + combos +" " + combos2);
                            //System.out.println("__________________________________________________________");
                            String text = test1 + " ===> " + "[" + word1 + "]";
                            confidenceitems.put(text, confidence);
                            supportitems.put(text, globalcandidates.get(string2));
                            //System.out.println(test1 + " ===> " + "[" +word1 + "]");
                            //System.out.println("CONFIDENCE: " + confidence);

                            //System.out.println("STRING:     " + string2);
                            //System.out.println("SUPPORT:    " + globalcandidates.get(string2));
                            //System.out.println();
                        }
                        //System.out.format("%s  ====>  %10s  :  %f\n",test1,word1,support);
                    }

                }
            }
        }
        File file = new File("rules.txt");
        
        Map<String, Double> sortbysupport = sortByValue(supportitems);
        try {
            FileWriter fw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(fw);
                
            for (Map.Entry<String, Double> entry : sortbysupport.entrySet()) {

                pw.println(entry.getKey());
                pw.println("Support: " + entry.getValue());
                pw.println("Confidence: " + confidenceitems.get(entry.getKey()));
                pw.println("___________________________________________________");
            }
            pw.close();
            System.out.println("Association Rules written to file rules.txt");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
